
package middleware;

import java.util.List;
import org.neo4j.driver.v1.Record;

public class Publication {
    private long id;
    private String name;
    private List<Author> authors;
    private List<Publication> citations;

    public Publication(long id, String name, List<Author> authors, List<Publication> citations) {
        this.id = id;
        this.name = name;
        this.authors = authors;
        this.citations = citations;
    }
    public Publication(Record publication){
        name = publication.get("name").toString();
        authors = null;
        citations = null;
    }
    public Publication(Record publication, List<Author> authors, List<Publication> citations){
        name = publication.get("name").toString();
        this.authors = authors;
        this.citations = citations;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<Publication> getCitations() {
        return citations;
    }

    public void setCitations(List<Publication> citations) {
        this.citations = citations;
    }
    
    
}
