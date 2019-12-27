
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
        if(nameCheck(authorName) && emailCheck(authorEmail) && headingCheck(authorHeading) && affiliationCheck(authorAffiliation)){
            graphManager.addAuthor(authorName, authorEmail, authorHeading, authorAffiliation);
            controller.navigate(5,null);
        }
    }
    public boolean nameCheck(String name){
        for (int i = 0; i < name.length()-1; i++) {  
            String regex = "[a-zA-Z ]+";
            if (! name.matches(regex)) {  
                errorText.setText("The name must contain only alphabetic characters");
                return false;
            }  
        }
        return true;
    }
    public boolean headingCheck(String heading){
        for (int i = 0; i < heading.length()-1; i++) {  
            String regex = "[a-zA-Z0-9]+";
            if (! heading.matches(regex)) {  
                errorText.setText("The heading must contain only alphabetic characters or numbers");
                return false;
            }  
        }
        return true;
    }
    public boolean affiliationCheck(String affiliation){
        for (int i = 0; i < affiliation.length()-1; i++) {  
            String regex = "[a-zA-Z0-9]+";
            if (! affiliation.matches(regex)) {  
                errorText.setText("The affiliation must contain only alphabetic characters or numbers");
                return false;
            }  
        }
        return true;
    }
    
    public boolean emailCheck(String email){
        String regex = "^@([\\w]+\\.)+[\\w]+[\\w]$";
        
        if(!email.matches(regex)){
            errorText.setText("The email is invalid");
            return false;
        }
        
        return true;
    }

}
