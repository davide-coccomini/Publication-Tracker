
package controllers;

import beans.Author;
import beans.Publication;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import middleware.GraphManager;

public class PublicationCreationController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private Button createButton;
    @FXML
    private TextField name;
    @FXML
    private Text errorText;
    @FXML
    private VBox authorsContainer;
    @FXML
    private VBox publicationsContainer;
    @FXML
    private Button addAuthorButton;
    @FXML
    private Button addCitationButton;
    
  
       
    private final SessionController controller;
    private final GraphManager graphManager;
    private final List<String> publications;
    private final List<String> authors;
    
    public PublicationCreationController(SessionController controller) {
        this.controller = controller;
        this.graphManager = controller.getGraphManager();
        this.publications = new ArrayList();
        this.authors = new ArrayList();
    }
    public void initController(){
        
        controller.load_Topbar(topbar, 3);

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                create();
            }
        });
        addAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                addAuthor();
            }
        });
        addCitationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                addCitation();
            }
        });
        
        List<Publication> publicationsList = graphManager.getPublications();
        for(Publication publication:publicationsList){
            publications.add(publication.getName()+"#"+Long.toString(publication.getId()));
        }
        List<Author> authorsList = graphManager.getAuthors();
        for(Author author:authorsList){
            authors.add(author.getName()+"#"+Long.toString(author.getId()));
        }
        addAuthor();
        addCitation();
    }
    private void addAuthor(){
        ChoiceBox c = new ChoiceBox();
        c.setPrefWidth(248.0);
        c.setItems(FXCollections.observableArrayList(authors));
     
        authorsContainer.getChildren().add(c);
    }
    private void addCitation(){
        ChoiceBox c = new ChoiceBox();
        c.setPrefWidth(481.0);
        c.setItems(FXCollections.observableArrayList(publications));
        publicationsContainer.getChildren().add(c);
    }
    private void create(){
        String publicationName = name.getText();
        List<Long> authorsList = new ArrayList();
        for(Node n:authorsContainer.getChildren()){
            ChoiceBox c = (ChoiceBox) n;
            String authorName = (String) c.getValue();
            Long authorId = Long.parseLong(authorName.split("#")[1]);
            authorsList.add(authorId);
        }   
        List<Long> citationsList = new ArrayList();
        for(Node n:publicationsContainer.getChildren()){
            ChoiceBox c = (ChoiceBox) n;
            String citationName = (String) c.getValue();
            Long citationId = Long.parseLong(citationName.split("#")[1]);
            citationsList.add(citationId);
        }   
        graphManager.addPublication(publicationName, authorsList, citationsList);
        controller.navigate(7,null);
    }
}
