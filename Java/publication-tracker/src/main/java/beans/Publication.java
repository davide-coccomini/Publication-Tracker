
package beans;

import java.util.List;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

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
    public Publication(Record publicationRecord){
        Node publication = publicationRecord.get(0).asNode();
        id = publication.id();
        name = publication.get("name").asString();
        authors = null;
        citations = null;
    }
    public Publication(Record publication, List<Author> authors, List<Publication> citations){
        name = publication.get("name").toString();
        this.authors = authors;
        this.citations = citations;
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
