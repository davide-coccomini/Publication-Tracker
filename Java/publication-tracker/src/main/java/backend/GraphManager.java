/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.List;
import middleware.Author;
import org.neo4j.driver.v1.Value;

public class GraphManager implements AutoCloseable{
    private final Driver driver;
    
    public GraphManager(String uri, String user, String password) {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }
    
    // Close connection (to be done at the end of the application)
    @Override
    public void close() throws Exception {
        driver.close();
    }
    
    ///// AUTHORS METHODS /////
    // Insert an Author and return his ID
    public long addAuthor(final String name, final String email, final String affiliation){
        try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MERGE (a:Author {name: $name, email:$email, affiliation:$affiliation})", parameters("name", name, "email", email, "affiliation", affiliation));
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
   
   ///// END AUTHORS METHODS /////
    
   ///// PUBLICATIONS METHODS /////
   // Add a publication node and all its relationships
   public Long addPublication(String name, int year, List<Long> idAuthors, List<Long> idCitations){
        try (Session session = driver.session()){
            Long idNewPublication;
            try (Transaction tx = session.beginTransaction()){
                StatementResult result = tx.run("CREATE (p:Publication {name: $name, year: $year}) RETURN id(p) as id", parameters( "name", name, "year", year));
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
}
