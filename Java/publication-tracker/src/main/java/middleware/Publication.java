
package middleware;

import java.util.List;

public class Publication {
    private String name;
    private int year;
    private List<Author> authors;
    private List<Publication> citations;

    public Publication(String name, int year, List<Author> authors, List<Publication> citations) {
        this.name = name;
        this.year = year;
        this.authors = authors;
        this.citations = citations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
