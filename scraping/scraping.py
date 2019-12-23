import scholarly
import json
import random 
import time
from lxml.html import fromstring
import requests 

RESUME = True
tot_requests = 0
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


def getNewPublicationId(title):
    global publications
    global publication_id
    key = title
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
    idpublication = getNewPublicationId(publication.bib.get("title"))
    tmp_author_publications.append(idpublication)
    publicationDict["id"] = idpublication
    publicationDict["title"] = publication.bib.get("title")
    publicationDict["idAuthor"] = idAuthor
    citations = []
    # DEEP LEVEL:  Author -> Publication -> Citation -> Author
    citation_limit = 10
    k = 0
    for citation in publication.get_citedby():
        increase_requests()
        time.sleep(6+random.uniform(0,3))
        if k == citation_limit:
            continue
        k += 1
        title = citation.bib['title']
        citationKey = citation.bib.get("title")
        if citationKey in publications: # The publication is already on the file
            idCitation = getNewPublicationId(citation.bib.get("title"))
            citations.append(idCitation)
            time.sleep(20+random.uniform(5,10))
        else: # Add the publication to the file
            idCitation = getNewPublicationId(citation.bib.get("title"))
            citations.append(idCitation)
            citationDict = {}
            citationDict["id"] = idCitation
            citationDict["title"] = citation.bib.get("title")

            authorName = citation.bib.get("author").split("and")[0]
            search_author_query = scholarly.search_author(authorName)
            increase_requests()

            time.sleep(3 + random.uniform(0, 2))
            authorDict = {}
            try:
                authorElement = next(search_author_query)
                time.sleep(2 + random.uniform(0,3))
                key = authorElement.email + authorElement.name
                idCitationAuthor = None
                if key in authors: # The author information are already in the file
                    idCitationAuthor = getNewAuthorId(authorElement.email, authorElement.name)
                else: # Write a new author in the file
                    authorDict = extractAuthorInfo(authorElement)
                    time.sleep(1 + random.uniform(1,5))
                    idCitationAuthor = authorDict["id"]
                    result_authors_file.write(str(json.dumps(authorDict))+"\n")

                citationDict["idAuthor"] = idCitationAuthor
                citationDict["citedby"] = []

                print("CITATION:")
                print(citationDict)
                result_publications_file.write(str(json.dumps(citationDict))+"\n")
            except Exception as e: # The associated author is not found
                citations.remove(idCitation)
                key = citation.bib.get("title")
                if key in publications:
                    del publications[key]
                time.sleep(2 + random.uniform(0,5))
                continue
      
        

    publicationDict["citedby"] = citations
    if len(citations) == 0:
        print("**** DANGER!!! Probably detected as bot. Change IP and resume the scraping. ****")
        result_publications_file.write("***************************************")
        change_ip()
    print("PUBLICATION:")
    print(publicationDict)
    time.sleep(3 + random.uniform(2,4))
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

def increase_requests():
    global tot_requests
    tot_requests += 1
    if tot_requests == 5 + random.uniform(0,3):
        change_useragent()
        tot_requests = 0
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

def get_proxies():
    url = 'https://free-proxy-list.net/'
    response = requests.get(url)
    parser = fromstring(response.text)
    proxies = set()
    for i in parser.xpath('//tbody/tr')[:10]:
        if i.xpath('.//td[7][contains(text(),"yes")]'):
            #Grabbing IP and corresponding PORT
            proxy = ":".join([i.xpath('.//td[1]/text()')[0],
                              i.xpath('.//td[2]/text()')[0]])
            proxies.add(proxy)
    return proxies
def change_ip():
    proxies = get_proxies()
    print(proxies)
    while True:
        for proxy in proxies:
            print("Testing "+ proxy)
            current_proxies = {'http': proxy,
                    'https': proxy}
            works = scholarly.scholarly.use_proxy(**current_proxies)
            
            if works:
                print("IP CHANGED CORRECTLY:" + proxy)
                return True
            else:
                continue
        proxies = get_proxies()
    return False

def change_useragent():
    import urllib.request
    url = 'https://httpbin.org/user-agent'
    USER_AGENTS = [
        ('Mozilla/5.0 (X11; Linux x86_64) '
         'AppleWebKit/537.36 (KHTML, like Gecko) '
         'Chrome/57.0.2987.110 '
         'Safari/537.36'),  # chrome
        ('Mozilla/5.0 (X11; Linux x86_64) '
         'AppleWebKit/537.36 (KHTML, like Gecko) '
         'Chrome/61.0.3163.79 '
         'Safari/537.36'),  # chrome
        ('Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:55.0) '
         'Gecko/20100101 '
         'Firefox/55.0'),  # firefox
        ('Mozilla/5.0 (X11; Linux x86_64) '
         'AppleWebKit/537.36 (KHTML, like Gecko) '
         'Chrome/61.0.3163.91 '
         'Safari/537.36'),  # chrome
        ('Mozilla/5.0 (X11; Linux x86_64) '
         'AppleWebKit/537.36 (KHTML, like Gecko) '
         'Chrome/62.0.3202.89 '
         'Safari/537.36'),  # chrome
        ('Mozilla/5.0 (X11; Linux x86_64) '
         'AppleWebKit/537.36 (KHTML, like Gecko) '
         'Chrome/63.0.3239.108 '
         'Safari/537.36'),  # chrome
        ('Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/41.0.2272.76 Chrome/41.0.2272.76 Safari/537.36'),
        ('Mozilla/5.0 (Linux; Android 8.0.0; SM-G960F Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.84 Mobile Safari/537.36'),
        ('Mozilla/5.0 (Linux; Android 7.0; SM-G892A Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/60.0.3112.107 Mobile Safari/537.36'),
        ('Mozilla/5.0 (Linux; Android 7.0; SM-G930VC Build/NRD90M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/58.0.3029.83 Mobile Safari/537.36'),
        ('Mozilla/5.0 (Linux; Android 6.0.1; SM-G935S Build/MMB29K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Mobile Safari/537.36'),
        ('Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Mobile/15E148 Safari/604.1'),
        ('Mozilla/5.0 (Apple-iPhone7C2/1202.466; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3'),
        ('Mozilla/5.0 (Windows Phone 10.0; Android 6.0.1; Microsoft; RM-1152) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Mobile Safari/537.36 Edge/15.15254'),
        ('Mozilla/5.0 (Windows Phone 10.0; Android 4.2.1; Microsoft; Lumia 950) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Mobile Safari/537.36 Edge/13.1058'),
        ('Mozilla/5.0 (Linux; Android 6.0.1; SHIELD Tablet K1 Build/MRA58K; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/55.0.2883.91 Safari/537.36'),
        ('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9')
    ]
    useragent = random.choice(USER_AGENTS)
    scholarly.scholarly.change_useragent(useragent)
def resume():
    global authors
    global publications
    global publication_id
    global author_id

    resumed_publications_file = open("data/publications.json", "r")
    data_lines = resumed_publications_file.readlines()
    for data_line in data_lines:
        publication = dict(json.loads(data_line))
      
        publications[publication["title"]] = publication["id"]
        if publication["id"] >= publication_id:
            publication_id = publication["id"] + 1
    resumed_publications_file.close()
    resumed_authors_file = open("data/authors.json", "r")
    data_lines = resumed_authors_file.readlines()
    for data_line in data_lines:
        author = dict(json.loads(data_line))
        authors[author["email"]+author["name"]] = author["id"]
        if author["id"] >= author_id:
            author_id = author["id"] + 1
    resumed_authors_file.close()
def main(): 
    global tmp_author_publications
    change_useragent()
    # change_ip()
    if RESUME:
        resume()
    
    publication_limit = 5
    old_authors = ["Carlo Vallati", "Enzo Mingozzi", "Giovanni Stea", "Marco Cococcioni", "Giuseppe Anastasi", "Marco Avvenuti", "Alessio Vecchio", "Cinzia Bernardeschi",
                   "Beatrice Lazzerini", "gigliola vaglini", "Mario Giovanni Cosimo Antonio Cimino", "Pericle Perazzo","Antonio Virdis", "Gianluca Dini", "Giuseppe Lettieri", "Pietro Ducange", "Francesco Marcelloni","Alessio Vecchio", "Cinzia Bernardeschi","Beatrice Lazzerini", "gigliola vaglini","University of Pisa"]
    authors = ["Giovanni Nardini","Guglielmo Cola", "Massimo Piotto","Marco Luise","Guido Tonelli","UNIPI",  "Università di Pisa", "University of Pisa", ]
   
    i = 0
    result_authors_file = open("data/authors.json", "a")
    result_publications_file = open("data/publications.json", "a")
    if RESUME:
        result_authors_file.write("\n")
        result_publications_file.write("\n")
        
    for currentAuthor in authors:
        print("__________________________"+currentAuthor+"___________________________")
     
        search_authors_query = scholarly.search_author(currentAuthor)
        increase_requests()

        try:
            time.sleep(1 + random.uniform(0, 5))
            author = next(search_authors_query).fill()
            time.sleep(0 + random.uniform(1,2))
        except:
            continue
        while author:
            time.sleep(10 + random.uniform(0,10))
            if "unipi" not in author.email and "Università di Pisa" not in author.affiliation and "University of Pisa" not in author.affiliation and "UNIPI" not in author.affiliation:
                time.sleep(3 + random.uniform(0, 3))
                author = next(search_authors_query).fill()
                time.sleep(2 + random.uniform(0,5))
                continue
            authorDict = extractAuthorInfo(author)
            result_authors_file.write(str(json.dumps(authorDict))+"\n") 
            

            j = 0
            tmp_author_publications = []
            for publication in author.publications:
                if j == publication_limit:
                    break
                try:
                    publicationDict = extractPublicationInfo(result_authors_file, result_publications_file, authorDict["id"], publication)
                except:
                    print("Connection error... retry ...")
                    change_ip()
                result_publications_file.write(str(json.dumps(publicationDict))+"\n")
                j += 1
            
            i += 1
            
            try:
                print("Sleeping 180 seconds ...")
                time.sleep(180 + random.uniform(0, 15))
                author = next(search_authors_query).fill()
            except:
                break
    #remove_duplicates_publications()
    
    #remove_unreferred_citations()
    
main()
