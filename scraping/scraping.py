import scholarly
import json
import random 

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


def extractPublicationInfo(idAuthor, publication):
    global tmp_author_publications
    publicationDict = {}
    idpublication = getNewPublicationId(publication.bib.get("title"),publication.bib.get("year"))
    tmp_author_publications.append(idpublication)
    publicationDict["id"] = idpublication
    publicationDict["title"] = publication.bib.get("title")
    publicationDict["year"] = publication.bib.get("year")
    publicationDict["idAuthor"] = idAuthor
    if len(publications) > 6 and len(authors) > 1:
        try:
            citations = random.sample(range(0,idpublication-5), random.randrange(0, 5))
            if citations not in tmp_author_publications:
                publicationDict["citedby"] = citations
        except:
            publicationDict["citedby"] = []
            print("PUBLICATION:")
            print(publicationDict)
            return publicationDict
    else:
        publicationDict["citedby"] = []
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
        print(publication)

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
                if citation == tmp_publication["id"]: # The citation is correct and referring to a real publication
                    citation_found = True
                    break
            if not citation_found: # the citation must be removed from the citations
                current_citations.remove(citation)
        
        publication["citedby"] = current_citations
        result_file.write(str(json.dumps(publication))+"\n")
def main():
    
    global tmp_author_publications
    i = 0
    result_authors_file = open("data/authors.json", "w")
    result_publications_file = open("data/publications.json", "w")

    search_authors_query = scholarly.search_author('University of Pisa')
    author = next(search_authors_query).fill()
    while author:
        authorDict = extractAuthorInfo(author)
        result_authors_file.write(str(json.dumps(authorDict))+"\n") 
        j = 0
        tmp_author_publications = []
        for publication in author.publications:
            if j == 5:
                break
            publicationDict = extractPublicationInfo(authorDict["id"], publication.fill())
            result_publications_file.write(str(json.dumps(publicationDict))+"\n")
            j += 1
        
        i += 1
        author = next(search_authors_query).fill()
    remove_duplicates_publications()
    
    remove_unreferred_citations()
    
main()
