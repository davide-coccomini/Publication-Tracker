import json
import os
from collections import Counter
from difflib import get_close_matches
from functools import reduce

def findPatterns():
    authors_file = open("data/authors.json", "r")
    banned_affiliations = ["iet", "mail", "ing", "net"]
    emails = []
    data_lines = authors_file.readlines()
    for data_line in data_lines:
        author = json.loads(data_line)
        email = '.'.join((author["email"].replace("@", "").split("."))[:-1])
        if email != "":
            emails.append(email)
    affiliations = Counter(reduce(lambda x,y: x+y,map (lambda x : x.split("."),emails))).most_common(50)
    affiliations.append("unisa")
    affiliations.append("harvard")
    affiliations.append("polimi")
    affiliations.append("mit")
    affiliations.append("nokia")
    affiliations.append("microsoft")
    affiliations.append("nih")
    
    for affiliation in affiliations:
        if len(affiliation[0]) <= 2 or affiliation in  banned_affiliations:
            affiliations.remove(affiliation)
    authors_file.close()
    return affiliations

def sanitizeString(string):
    replaces = [('"email"', "email"), ('"name"', "name"), ('"affiliation"', "affiliation"), ('"heading"',"heading") ]
    for k, v in replaces:
        string = string.replace(k, v)
    return string

def main():
    publications_file = open("data/publications.json", "r")
    data_lines = publications_file.readlines()

    result_paths = ["data/publications-neo4j.cypher", "data/authors-neo4j.cypher", "data/authors_relation.cypher","data/publications_relation.cypher"]
    result_publication_file = open(result_paths[0], "w")
    authors_relation_file = open(result_paths[2], "w")
    publications_relation_file = open(result_paths[3], "w")
    for data_line in data_lines:
        publication = json.loads(data_line)
        new_publication = {
            "name": publication["title"]
        }
        publicationString = sanitizeString(str(json.dumps(new_publication)))
        result_publication_file.write("(p"+str(publication["id"])+":Publication "+publicationString+"),\n")

        for author in publication["idAuthor"]:
            relation = "(a"+str(author)+")-[:PUBLISHES]"+"->(p"+str(publication["id"])+"),\n"
            authors_relation_file.write(relation)
        for citation in publication["citedby"]:
            relation = "(p"+str(citation)+")-[:CITES]->(p"+str(publication["id"])+"),\n"
            publications_relation_file.write(relation)

   

    affiliations = findPatterns()
    authors_file = open("data/authors.json", "r")
    affiliations = [affiliation[0] for affiliation in affiliations]

    result_author_file = open(result_paths[1], "w")
    data_lines = authors_file.readlines()
    known_authors  = ["Carlo Vallati", "Enzo Mingozzi", "Giovanni Stea", "Marco Cococcioni", "Giuseppe Anastasi", "Marco Avvenuti", "Alessio Vecchio", "Cinzia Bernardeschi",
                               "Alessio Bechini", "Andrea Domenici", "Luigi Rizzo", "Massimo Pappalardo", "Giovanni Nardini", "Massimo Piotto", "Marco Luise", "Beatrice Lazzerini", "gigliola vaglini", "Mario Giovanni Cosimo Antonio Cimino", "Pericle Perazzo", "Antonio Virdis", "Gianluca Dini", "Giuseppe Lettieri", "Pietro Ducange", "Francesco Marcelloni", "Alessio Vecchio", "Cinzia Bernardeschi", "Beatrice Lazzerini", 
                               "gigliola vaglini", "Armando Segatori" ]

    for data_line in data_lines:
        author = json.loads(data_line)
        affiliation = get_close_matches(author["affiliation"], affiliations,1, cutoff=0.3)
        if "Pisa" in author["affiliation"] or author["name"] in known_authors:
            affiliation = "unipi"
        
        if len(affiliation) == 0:
            affiliation = "Unknown"
        else:
            affiliation = affiliation[0]
        new_author = {
            "name": author["name"].title(),
            "email": author["email"],
            "heading": author["affiliation"],
            "affiliation": affiliation
        }
        authorString = sanitizeString(str(json.dumps(new_author)))
        result_author_file.write("(a"+str(author["id"])+":Author "+authorString+"),\n")

    with open("data/neo4j_script.cypher", 'w') as outfile:
        outfile.write("CREATE ")
        for fname in result_paths:
            with open(fname) as infile:
                for line in infile:
                    outfile.write(line)
        outfile.close()
    with open("data/neo4j_script.cypher", 'rb+') as outfile:
        outfile.seek(-3, os.SEEK_END)
        outfile.truncate()
        outfile.close()
main()
