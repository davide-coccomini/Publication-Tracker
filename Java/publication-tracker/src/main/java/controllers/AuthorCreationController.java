
package controllers;

import beans.Author;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import middleware.GraphManager;


public class AuthorCreationController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private Button createButton;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private Text errorText;
    @FXML
    private TextField heading;
    @FXML
    private TextField affiliation;
   
    private final SessionController controller;
    private final GraphManager graphManager;

    public AuthorCreationController(SessionController controller) {
        this.controller = controller;
        this.graphManager = controller.getGraphManager();
    }
    public void initController(){
        
        controller.load_Topbar(topbar, 2);

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                create();
            }
        });
    }
    private void create(){
        String authorName = name.getText();
        String authorEmail = email.getText();
        String authorHeading = heading.getText();
        String authorAffiliation = affiliation.getText();
        graphManager.addAuthor(authorName, authorEmail, authorHeading, authorAffiliation);
        controller.navigate(5,null);
    }
}
