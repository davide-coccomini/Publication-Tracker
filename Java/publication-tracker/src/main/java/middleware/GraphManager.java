/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package middleware;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;
import beans.Author;
import beans.Publication;
import beans.User;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.driver.v1.Record;

import org.neo4j.driver.v1.types.Node;

public class GraphManager implements AutoCloseable{
    private final Driver driver;

    public GraphManager(String uri, String user, String password) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    // Close connection (to be done at the end of the application)
    @Override
    public void close() {
        try{
            driver.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    ///// AUTHORS METHODS /////
    // Insert an Author and return his ID
    public long addAuthor(final String name, final String email, final String heading, final String affiliation){
        try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MERGE (a:Author {name: $name, email:$email,heading:$heading, affiliation:$affiliation})", parameters("name", name, "email", email, "heading",heading, "affiliation", affiliation));
                tx.success();
            }

            StatementResult result = session.run(
                    "MATCH (a:Author {name: $name, email:$email, affiliation:$affiliation}) RETURN id(a) as id",
                    parameters("name", name, "email", email, "affiliation", affiliation));
            return result.single().get("id").asLong();
        }
    }
    // Given a key and a value, get a single author that matches
    public Author getAuthorBy(final String key, final String value){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author {"+key+": $value}) RETURN a LIMIT 1",
                    parameters("value", value));
            return new Author(result.single());
        }catch(Exception e){
            return null;
        }
    }
    // Given an id, get the matching Author
    public Author getAuthorById(long id){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author) WHERE id(a) = "+id+" RETURN a");
            return new Author(result.single());
        }
    }

    // Given a key and a value, get a list of authors that match
    public List<Author> getAuthorsBy(final String key, final String value){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author {"+key+": $value}) RETURN a",
                    parameters("value", value));
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                authors.add(new Author(result.next()));
            }
            return authors;
        }
    }
    // Method used for pagination in authorsList
    public List<Author> getAuthors(int limit, int page){
        int toSkip = page * limit;
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author) RETURN a SKIP $skip LIMIT $limit",
                    parameters("skip", toSkip, "limit", limit));
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                authors.add(new Author(result.next()));
            }
            return authors;
        }
    }
    // Return all the authors
    public List<Author> getAuthors(){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author) RETURN a");
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                authors.add(new Author(result.next()));
            }
            return authors;
        }
    }
    // Given the id of an Author and new values, update it
    public void updateAuthor(long id, String name, String email, String heading, String affiliation){
        try (Session session = driver.session()){
            session.run("MATCH (a:Author) WHERE id(a) = $id SET a.name = $name, a.email = $email, a.heading = $heading, a.affiliation = $affiliation RETURN a", parameters("id",id, "name", name, "email", email, "heading", heading, "affiliation", affiliation));
        }catch(Exception e){
            System.out.println(e);
        }
    }
   // Given the id of an Author, get all the relationships with it
   public StatementResult getAuthorRelationships(final Long id){
        try (Session session = driver.session()){
            StatementResult result = session.run("MATCH (a:Author)-[r]-(b) WHERE id(a)="+id+" RETURN type(r) as relation, a as author, p as publication");
            return result;
        }
   }
   // Given an author id, retrieve the list of his publications
   public List<Publication> getAuthorPublications(final long id){
        try (Session session = driver.session()){
            StatementResult result = session.run("MATCH (a1)-[:PUBLISHES]->(p1) WHERE id(a1) = $id RETURN p1 as publication", parameters("id",id));
            List<Publication> publications = new ArrayList();
            while (result.hasNext()){
                Node publicationNode = result.next().get("publication").asNode();
                publications.add(new Publication(publicationNode.id(), publicationNode.get("name").asString(),getPublicationAuthors(publicationNode.id()),null));
            }
            return publications;
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
   }
   // Given an author id, delete it
   public void deleteAuthor(final Long id){
       try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MATCH (a:Author) WHERE id(a) = "+id+" DETACH DELETE a");
                tx.success();
            }
       }
   }
   // Given an author id, remove all relationships with it
   public void detatchAuthor(final Long id){
       try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MATCH (a:Author) WHERE id(a) = "+id+" DETACH a");
                tx.success();
            }
       }
   }
   // Get most cited author
   public Author getMostCitedAuthor(){
       String query = "MATCH (a1)-[:PUBLISHES]->(p1)-[:CITES]->(p2)\n" +
                      "RETURN  a1, SIZE(COLLECT(p1)), COLLECT(p1) as citations\n" +
                      "ORDER BY SIZE(citations) DESC LIMIT 1";
       try (Session session = driver.session()){
            StatementResult result = session.run(query);
            return new Author(result.single());
       } 
   }
   // Get citation from id author
   public int getAuthorCitations(long id){
       String query = "MATCH (a1)-[:PUBLISHES]->(p1)<-[:CITES]-(p2)<-[:PUBLISHES]-(a2) WHERE id(a1) = $id1 AND id(a2) <> $id2 RETURN  size(COLLECT(p1))";
       try (Session session = driver.session()){
            StatementResult result = session.run(query, parameters("id1",id, "id2", id));
            return result.single().get(0).asInt();
        }catch(Exception e){
            return 0;
        }
   }
   // Given an author id, return other authors that wrote publications with him:
   public List<Author> getCoauthors(long id){
       String query = "MATCH (a1)-[:PUBLISHES]->()<-[:PUBLISHES]-(a2) WHERE id(a1) = $id \n" +
                      "RETURN collect(DISTINCT a2) as coauthors \n" +
                      "ORDER BY SIZE(coauthors) DESC";
        try (Session session = driver.session()){
            StatementResult result = session.run(query, parameters("id",id));
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                List<Object> authorsNode = result.next().get("coauthors").asList();
                for(Object authorObject: authorsNode){
                    Node authorNode = (Node) authorObject;
                    authors.add(new Author(authorNode));
                }
            }
            return authors;
        }catch(Exception e){
            return null;
        }
   }
   // Given two authors, find common coauthors
   public List<Author> getCommonCoauthors(long idAuthor1, long idAuthor2){
       String query = "MATCH (a1)-[:PUBLISHES]->()<-[:PUBLISHES]-(a2)-[:PUBLISHES]->()<-[:PUBLISHES]-(a3) WHERE id(a1) = $id1 and id(a3) = $id2 RETURN collect(DISTINCT a2) as coauthors";
        try (Session session = driver.session()){
            StatementResult result = session.run(query, parameters("id1",idAuthor1, "id2", idAuthor2));
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                List<Object> authorsNode = result.next().get("coauthors").asList();
                for(Object authorObject: authorsNode){
                    Node authorNode = (Node) authorObject;
                    authors.add(new Author(authorNode));
                }
            }
            return authors;
        }catch(Exception e){
            return null;
        }
   }
    // Return the number of authors
    public int getAuthorsNumber(){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author) RETURN count(a) as number");
            return result.single().get(0).asInt();
        }
    }
    // Return not direct authors
    public List<Author> getIndirectCoauthors(long id){
        String query = "MATCH (a:Author)-[:PUBLISHES]->(p:Publication)<-[:PUBLISHES]-(a2:Author)-[:PUBLISHES]->(p2:Publication)<-[]-(a3:Author) WHERE id(a) = $id AND id(a3) <> id(a2) AND id(a3) <> id(a) RETURN COLLECT(DISTINCT a2) as direct_authors,COLLECT(DISTINCT a3) as all_authors";
        try (Session session = driver.session()){
            StatementResult result = session.run(query, parameters("id", id));
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                Record r = result.next();
                Set<Object> allAuthorsNodes = new HashSet(r.get("all_authors").asList());
                Set<Object> directAuthorsNodes = new HashSet(r.get("direct_authors").asList());
                directAuthorsNodes.removeAll(allAuthorsNodes);
                List<Object> authorsNode = new ArrayList(directAuthorsNodes);
                for(Object authorObject: authorsNode){
                    Node authorNode = (Node) authorObject;
                    authors.add(new Author(authorNode));
                }
            }
            return authors;
        }catch(Exception e){
            e.printStackTrace();
            return new ArrayList();
        }
    }
   ///// END AUTHORS METHODS /////

   ///// PUBLICATIONS METHODS /////
   // Add a publication node and all its relationships
   public Long addPublication(String name, List<Long> idAuthors, List<Long> idCitations){
        try (Session session = driver.session()){
            Long idNewPublication;
            try (Transaction tx = session.beginTransaction()){
                StatementResult result = tx.run("CREATE (p:Publication {name: $name}) RETURN id(p) as id", parameters( "name", name));
                idNewPublication = result.single().get("id").asLong();
                // Add all authors relationships
                for(Long idAuthor : idAuthors){
                    tx.run("MATCH (a:Author),(p:Publication) WHERE id(a) = "+idAuthor+" AND id(p) = "+idNewPublication+" CREATE (a)-[:PUBLISHES]->(p)");
                }
                // Add all publications relationships
                for(Long idPublication : idCitations){
                    tx.run("MATCH (p1:Publication),(p2:Publication) WHERE id(p1) = "+idPublication+" AND id(p2) = "+idNewPublication+" CREATE (p2)-[:CITES]->(p1)");
                }
                tx.success();
            }
            return idNewPublication;
        }
   }
   // Method used for pagination in publicationsList
    public List<Publication> getPublications(int limit, int page){
        int toSkip = page * limit;
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (p:Publication) RETURN p SKIP $skip LIMIT $limit",
                    parameters("skip", toSkip, "limit", limit));
            
            List<Publication> publications = new ArrayList();
            while (result.hasNext()){
                publications.add(new Publication(result.next()));
            }
            return publications;
        }
    }
    // Return all the publications
    public List<Publication> getPublications(){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (p:Publication) RETURN p");
            
            List<Publication> publications = new ArrayList();
            while (result.hasNext()){
                publications.add(new Publication(result.next()));
            }
            return publications;
        }
    }
    // Given the publication id and the name value, update the name
    public void updatePublication(long id, String name){
        try (Session session = driver.session()){
            session.run("MATCH (p:Publication) WHERE id(p) = $id SET p.name = $name RETURN p", parameters("id",id, "name", name));
        }catch(Exception e){
            System.out.println(e);
        }
    }
    // Given an id, get the matching Publication
    public Publication getPublicationById(long id){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (p:Publication) WHERE id(p) = "+id+" RETURN p");
            return new Publication(result.single());
        }
    }
    // Retrieve all the publication and their citations too
    public Publication getPublicationByIdComplete(long id){ // Computationally Espensive
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (p:Publication) WHERE id(p) = "+id+" RETURN p");
            Node publication = result.single().get(0).asNode();
            return new Publication(publication.id(), publication.get("name").asString(),getPublicationAuthors(id), getPublicationCitations(id));
        }
    }
   // Given the id of a Publication, get all the publications that cites it
   public List<Publication> getPublicationCitations(final Long id){
        try (Session session = driver.session()){
            StatementResult result = session.run("MATCH (p1)<-[:CITES]-(p2) WHERE id(p1) = $id RETURN collect(p2) as citations", parameters("id",id));
            List<Publication> publications = new ArrayList();
            
            List<Object> publicationNodes = result.single().get("citations").asList();
            for(Object publicationObject: publicationNodes){
                Node publicationNode = (Node) publicationObject;
                publications.add(new Publication(publicationNode.id(), publicationNode.get("name").asString(),getPublicationAuthors(publicationNode.id()), getPublicationCitations(publicationNode.id())));
            }
            
            return publications;
        }
   }
   // Given the id of a publication, get all the publications cited by it
   public List<Publication> getPublicationCitationsMade(final Long id){
        try (Session session = driver.session()){
            StatementResult result = session.run("MATCH (p1)-[:CITES]->(p2) WHERE id(p1) = $id RETURN collect(p2) as citations", parameters("id",id));
            List<Publication> publications = new ArrayList();
            
            List<Object> publicationNodes = result.single().get("citations").asList();
            for(Object publicationObject: publicationNodes){
                Node publicationNode = (Node) publicationObject;
                publications.add(new Publication(publicationNode.id(), publicationNode.get("name").asString(),getPublicationAuthors(publicationNode.id()), getPublicationCitations(publicationNode.id())));
            }
            return publications;
        }
   }
   // Get the authors that wrote the publication
   public List<Author> getPublicationAuthors(final Long id){
        try (Session session = driver.session()){
            StatementResult result = session.run("MATCH (p:Publication)-[r]-(a) WHERE id(p)="+id+" AND type(r) = 'PUBLISHES' RETURN id(a) as author");
            List<Author> authors = new ArrayList();
            while (result.hasNext()){
                Author p = getAuthorById(result.next().get("author").asLong());
                authors.add(p);
            }
            return authors;
        }
   }
   // Given a key and a value, get a single publication that matches
   public Publication getPublicationBy(final String key, final String value){
        String query = "MATCH (p:Publication{"+key+": $value}) RETURN p,id(p) as id LIMIT 1";
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    query,
                    parameters("value", value));
      
            Node publication = result.single().get(0).asNode();
            return new Publication(publication.id(), publication.get("name").asString(),getPublicationAuthors(publication.id()),null);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
    }
   // Given publication id, delete it
   public void deletePublication(final Long id){
       try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MATCH (p:Publication) WHERE id(p) = "+id+" DETACH DELETE p");
                tx.success();
            }
       }
   }
   // Return the number of publications
    public int getPublicationsNumber(){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (p:Publication) RETURN count(p) as number");
            return result.single().get(0).asInt();
        }
    }
    
   // Given publication id, remove all the relationships with it
   public void detatchPublication(final Long id){
       try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MATCH (p:Publication) WHERE id(p) = "+id+" DETACH p");
                tx.success();
            }
       }
   }
   // Retrieve the publication most cited from other publications
   public Publication getMostCitedPublication(){
       String query = "MATCH (p1)-[:CITES]->(p2)\n" +
                      "RETURN  p2,COLLECT(p2) as publications\n" +
                      "ORDER BY SIZE(publications) DESC LIMIT 1";
        try (Session session = driver.session()){
            StatementResult result = session.run(query);
            return new Publication(result.single());
        } 
   }
}
