import json
import os


def main():
    publications_file = open("data/publications.json", "r")
    data_lines = publications_file.readlines()

    result_paths = ["data/publications-neo4j", "data/authors-neo4j", "data/authors_relation","data/publications_relation"]
    result_publication_file = open(result_paths[0], "w")
    authors_relation_file = open(result_paths[2], "w")
    publications_relation_file = open(result_paths[3], "w")
    for data_line in data_lines:
        publication = json.loads(data_line)
        new_publication = {
            "name": publication["title"],
            "year": publication["year"]
        }
        result_publication_file.write("(p"+str(publication["id"])+":Publication "+str(json.dumps(new_publication))+"),\n")

        for author in publication["idAuthor"]:
            relation = "(a"+str(author)+")-[:PUBLISHES]"+"->(p"+str(publication["id"])+"),\n"
            authors_relation_file.write(relation)
        for citation in publication["citedby"]:
            relation = "(p"+str(citation)+")-[:CITES]->(p"+str(publication["id"])+"),\n"
            publications_relation_file.write(relation)

    authors_file = open("data/authors.json", "r")
    result_author_file = open(result_paths[1], "w")
    data_lines = authors_file.readlines()
    for data_line in data_lines:
        author = json.loads(data_line)
        new_author = {
            "name": author["name"].title(),
            "email": author["email"],
            "affiliation": author["affiliation"]
        }
        result_author_file.write("(a"+str(author["id"])+":Author "+str(json.dumps(new_author))+"),\n")

    with open("data/neo4j_script", 'w') as outfile:
        outfile.write("CREATE ")
        for fname in result_paths:
            with open(fname) as infile:
                for line in infile:
                    outfile.write(line)
        outfile.truncate()
main()
