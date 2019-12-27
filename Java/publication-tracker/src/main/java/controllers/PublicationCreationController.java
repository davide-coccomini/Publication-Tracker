
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
            try{
                Long authorId = Long.parseLong(authorName.split("#")[1]);
                authorsList.add(authorId);
            }catch(Exception e){}
        }   
        List<Long> citationsList = new ArrayList();
        for(Node n:publicationsContainer.getChildren()){
            ChoiceBox c = (ChoiceBox) n;
            String citationName = (String) c.getValue();
            try{
                Long citationId = Long.parseLong(citationName.split("#")[1]);
                citationsList.add(citationId);
            }catch(Exception e){}
        }   
        
        if(nameCheck(publicationName) && listsCheck(authorsList,citationsList)){
            graphManager.addPublication(publicationName, authorsList, citationsList);
            controller.navigate(7,null);  
        }
 
    }
    public boolean nameCheck(String name){
        for (int i = 0; i < name.length()-1; i++) {  
            String regex = "[a-zA-Z-_0-9]+";
            if (! name.matches(regex)) {  
                errorText.setText("The name must contain only letters and numbers");
                return false;
            }  
        }
        return true;
    }
    public boolean listsCheck(List<Long> authorsList, List<Long> citationsList){
        List<Long> checkedAuthors = new ArrayList();
        for(Long a:authorsList){
          if(checkedAuthors.contains(a)){
              errorText.setText("You added the same author more than one time");
              return false;
          }else{
              checkedAuthors.add(a);
          }
        }
        
        List<Long> checkedCitations = new ArrayList();
        for(Long c:citationsList){
          if(checkedCitations.contains(c)){
              errorText.setText("You added the same publication more than one time");
              return false;
          }else{
              checkedCitations.add(c);
          }
        }
        
        return true;
    }

 
}
