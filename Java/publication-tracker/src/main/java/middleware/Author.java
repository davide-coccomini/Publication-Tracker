package middleware;

import org.neo4j.driver.v1.Record;


public class Author {
    private String name;
    private String email;
    private String affiliation;

    public Author(String name, String email, String affiliation) {
        this.name = name;
        this.email = email;
        this.affiliation = affiliation;
    }
    public Author(Record author){
        name = author.get("name").asString();
        email = author.get("email").asString();
        affiliation = author.get("affiliation").asString();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
}
