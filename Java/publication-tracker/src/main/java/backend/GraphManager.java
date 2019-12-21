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
    // Create an author node
    public static void createAuthorNode(Transaction tx, String name, String email, String affiliation){
        tx.run( "CREATE (a:Author {name: $name, email: $email, affiliation: $affiliation}) RETURN id(a)", parameters( "name", name, "email", email, "affiliation", affiliation ));
    }
    // Insert an Author and return his ID
    public long addAuthor(final String name, final String email, final String affiliation){
        try (Session session = driver.session()){
            try (Transaction tx = session.beginTransaction()){
                tx.run("MERGE (a:Author {name: $name, email:$email, affiliation:$affiliation})", parameters("name", name, "email", email, "affiliation", affiliation));
                tx.success();  // Mark this write as successful.
            }
            
            StatementResult result = session.run(
                    "MATCH (a:Author {name: $name, email:$email, affiliation:$affiliation}) RETURN id(a) as id",
                    parameters("name", name, "email", email, "affiliation", affiliation));
            return result.single().get("id").asLong();
        }
        
    }
    public void getAuthorByName(final String name){
        
    }
    public void printAuthors(String matching){
        try (Session session = driver.session()){
            StatementResult result = session.run(
                    "MATCH (a:Author) WHERE a.name STARTS WITH {x} RETURN a.name AS name",
                    parameters("x", matching));
            while (result.hasNext()){
                Record record = result.next();
                System.out.println(record.get("name").asString());
            }
        }
    }
   
    
}
