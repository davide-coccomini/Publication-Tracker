import scholarly
import json
import random 
import time

author_id = 0
publication_id = 0
authors = {}
publications = {}
tmp_author_publications = []
def sanitizeString(publication):
    return " ".join((publication.replace("\n", "")).split()).replace("'",'"')


def getNewAuthorId(email,name):
    global authors
    global author_id
    key = email + name
    if(key in authors):
        return authors[key]
    else:
        authors[key] = author_id
        author_id += 1
        return author_id - 1


def getNewPublicationId(title,year):
    global publications
    global publication_id
    key = title + str(year)
    if key in publications:
        return publications[key]
    else:
        publications[key] = publication_id
        publication_id += 1
        return publication_id - 1 


def extractAuthorInfo(author):
    authorDict = {}
    authorDict["id"] = getNewAuthorId(author.email,author.name)
    authorDict["name"] = author.name
    authorDict["email"] = author.email
    authorDict["affiliation"] = author.affiliation
    print("AUTHOR:")
    print(authorDict)
    return authorDict


def extractPublicationInfo(result_authors_file, result_publications_file, idAuthor, publication):
    global tmp_author_publications
    publicationDict = {}
    idpublication = getNewPublicationId(publication.bib.get("title"),publication.bib.get("year"))
    tmp_author_publications.append(idpublication)
    publicationDict["id"] = idpublication
    publicationDict["title"] = publication.bib.get("title")
    publicationDict["year"] = publication.bib.get("year")
    publicationDict["idAuthor"] = idAuthor
    citations = []
    # DEEP LEVEL:  Author -> Publication -> Citation -> Author
    for citation in publication.get_citedby():
        title = citation.bib['title']
        citationKey = citation.bib.get("title") + str(citation.bib.get("year"))
        if citationKey in publications: # The publication is already on the file
            idCitation = getNewPublicationId(citation.bib.get("title"),citation.bib.get("year"))
            citations.append(idCitation)
        else: # Add the publication to the file
            idCitation = getNewPublicationId(citation.bib.get("title"),citation.bib.get("year"))
            citations.append(idCitation)
            citationDict = {}
            citationDict["id"] = idCitation
            citationDict["title"] = citation.bib.get("title")
            citationDict["year"] = citation.bib.get("year")
        
            authorName = citation.bib.get("author").split("and")[0]
            search_author_query = scholarly.search_author(authorName)
            authorDict = {}
            try:
                authorElement = next(search_author_query)
                key = authorElement.email + authorElement.name
                idCitationAuthor = None
                if key in authors: # The author information are already in the file
                    idCitationAuthor = getNewAuthorId(authorElement.email, authorElement.name)
                else: # Write a new author in the file
                    authorDict = extractAuthorInfo(next(search_author_query))
                    idCitationAuthor = authorDict["id"]
                    result_authors_file.write(str(json.dumps(authorDict))+"\n")

                citationDict["idAuthor"] = idCitationAuthor
                citationDict["citedby"] = []

                print("CITATION:")
                print(citationDict)
                result_publications_file.write(str(json.dumps(citationDict))+"\n")
            except:
                time.sleep(2)
                continue
      
        

    publicationDict["citedby"] = citations
    
    print("PUBLICATION:")
    print(publicationDict)
    return publicationDict

def remove_duplicates_publications():
    # Remove duplicates in publications (the ones published by more than one person) and collapse them in a single voice
    result_publications_file = open("data/publications.json", "r")
    data_lines = result_publications_file.readlines()
    result_file = open("data/publications_collapsed.json", "w")
    readed_publications = []
    for data_line in data_lines:
        publication = json.loads(data_line)
        publication_authors = []

        current_author = publication["idAuthor"]
        publication_authors.append(current_author)
        current_title = publication["title"]
        if current_title in readed_publications:
            continue
        readed_publications.append(current_title)

        for tmp_data_line in data_lines:
            tmp_publication = json.loads(tmp_data_line)
            tmp_title = tmp_publication["title"]
            tmp_author = tmp_publication["idAuthor"]
            if tmp_author == current_author:
                continue
            if tmp_title == current_title:
                publication_authors.append(tmp_publication["idAuthor"])
        publication["idAuthor"] = publication_authors
        result_file.write(str(json.dumps(publication))+"\n")


def remove_unreferred_citations():
    # Remove the citations referring to publications removed in remove_duplicates_publications)
    result_publications_file = open("data/publications_collapsed.json", "r")
    data_lines = result_publications_file.readlines()
    result_file = open("data/publications_collapsed_checked.json", "w")
    readed_publications = []
    for data_line in data_lines:
        publication = json.loads(data_line)

        current_citations = publication["citedby"]
        for citation in current_citations:
            citation_found = False
            for tmp_data_line in data_lines:
                tmp_publication = json.loads(tmp_data_line)
                # The citation is correct and referring to a real publication
                if citation == tmp_publication["id"]:
                    citation_found = True
                    break
            if not citation_found:  # the citation must be removed from the citations
                current_citations.remove(citation)

        publication["citedby"] = current_citations
        result_file.write(str(json.dumps(publication))+"\n")
def main():
    publication_limit = 5
    authors = ["Giuseppe Lettieri", "Pietro Ducange","Francesco Marcelloni", "Francesco Pistolesi", "Mario Giovanni Cosimo Antonio Cimino","Carlo Vallati", "Enzo Mingozzi", "Giovanni Stea", "Marco Cococcioni", "Giuseppe Anastasi", "Marco Avvenuti", "Alessio Vecchio", "Cinzia Bernardeschi", "Beatrice Lazzerini", "gigliola vaglini", "University of Pisa", "Università di Pisa"]
    global tmp_author_publications
    i = 0
    result_authors_file = open("data/authors.json", "w")
    result_publications_file = open("data/publications.json", "w")
    
    for currentAuthor in authors:
        print("__________________________"+currentAuthor+"___________________________")
        search_authors_query = scholarly.search_author(currentAuthor)
        
        try:
            author = next(search_authors_query).fill()
        except:
            continue
        while author:
            if "Università di Pisa" not in author.affiliation and "University of Pisa" not in author.affiliation and "UNIPI" not in author.affiliation:
                continue
            authorDict = extractAuthorInfo(author)
            result_authors_file.write(str(json.dumps(authorDict))+"\n") 
            j = 0
            tmp_author_publications = []
            for publication in author.publications:
                if j == publication_limit:
                    break
                publicationDict = extractPublicationInfo(result_authors_file, result_publications_file, authorDict["id"], publication)
                result_publications_file.write(str(json.dumps(publicationDict))+"\n")
                j += 1
            
            i += 1
            
            try:
                print("Sleeping 60 seconds ...")
                time.sleep(60)
                author = next(search_authors_query).fill()
            except:
                break
    #remove_duplicates_publications()
    
    #remove_unreferred_citations()
    
main()
