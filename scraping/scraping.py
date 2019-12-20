import scholarly
import json

author_id = 0
publication_id = 0
authors = {}
publications = {}

def sanitizeString(publication):
    return " ".join((publication.replace("\n", "")).split()).replace("'",'"')


def getNewAuthorId(email):
    global authors
    global author_id
    if(email in authors):
        return authors[email]
    else:
        authors[email] = author_id
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
    authorDict["id"] = getNewAuthorId(author.email)
    authorDict["name"] = author.name
    authorDict["email"] = author.email
    authorDict["affiliation"] = author.affiliation
    authorDict["citedBy"] = author.citedby
    print("AUTHOR:")
    print(authorDict)
    return authorDict


def extractPublicationInfo(idAuthor, publication):
    publicationDict = {}
    publicationDict["id"] = getNewPublicationId(publication.bib.get("title"),publication.bib.get("year"))
    publicationDict["title"] = publication.bib.get("title")
    publicationDict["year"] = publication.bib.get("year")
    publicationDict["idAuthor"] = idAuthor
    for citation in publication.get_citedby():
        publicationDict["citations"] = [getNewPublicationId(citation.bib.get("title"),citation.bib.get("year")) for citation in publication.get_citedby()]
    print("PUBLICATION:")
    print(publicationDict)
    return publicationDict
def main():
    i = 0
    result_authors_file = open("data/authors.json", "w")
    result_publications_file = open("data/publications.json", "w")

    search_authors_query = scholarly.search_author('University of Pisa')
    author = next(search_authors_query).fill()
    while author:
        authorDict = extractAuthorInfo(author)
        result_authors_file.write(str(json.dumps(authorDict))+"\n") 
        j = 0
        for publication in author.publications:
            if j == 2:
                break
            publicationDict = extractPublicationInfo(authorDict["id"], publication.fill())
            result_publications_file.write(str(json.dumps(publicationDict))+"\n")
            j += 1
        
        i += 1
        author = next(search_authors_query).fill()
    
main()
