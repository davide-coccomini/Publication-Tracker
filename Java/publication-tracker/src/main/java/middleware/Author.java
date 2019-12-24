package middleware;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;


public class Author {
    private long id;
    private String name;
    private String email;
    private String heading;
    private String affiliation;

    public Author(long id, String name, String email, String heading, String affiliation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.heading = heading;
        this.affiliation = affiliation;
    }
    public Author(Record authorRecord){
        Node author = authorRecord.get(0).asNode();
        id = author.id();
        name = author.get("name").asString();
        email = author.get("email").asString();
        heading = author.get("heading").asString();
        affiliation = author.get("affiliation").asString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
    
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }
    
}
