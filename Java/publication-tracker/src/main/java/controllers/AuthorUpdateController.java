
package controllers;

import beans.Author;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import middleware.GraphManager;

public class AuthorUpdateController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TextField nameText;
    @FXML
    private Button updateButton;
    @FXML
    private TextField emailText;
    @FXML
    private TextField headingText;
    @FXML
    private TextField affiliationText;
    @FXML
    private Text errorText; 
    
    private final SessionController controller;
    private final GraphManager graphManager;
    private final int currentPage;
    private final long authorId;
    
    public AuthorUpdateController(SessionController c, List<Object> args){
        authorId = (long)args.get(0);
        currentPage = (int)args.get(1);
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                updateAuthor();
            }
        });  
        controller.load_Topbar(topbar, 2);
        loadInformation();  
    } 
    private void updateAuthor(){
        String name = nameText.getText();
        String email = emailText.getText();
        String heading = headingText.getText();
        String affiliation = affiliationText.getText();
        if(nameCheck(name) && emailCheck(email) && headingCheck(heading) && affiliationCheck(affiliation)){
            graphManager.updateAuthor(authorId, name, email, heading, affiliation);
            controller.navigate(5,null);
        }
    }
    private void loadInformation(){
        Author author = graphManager.getAuthorById(authorId);
        nameText.setText(author.getName());
        emailText.setText(author.getEmail());
        headingText.setText(author.getHeading());
        affiliationText.setText(author.getAffiliation());
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
            String regex = "[a-zA-Z0-9.!?,_:; ]+";
            if (! heading.matches(regex)) {  
                errorText.setText("The heading contains illegal characters");
                return false;
            }  
        }
        return true;
    }
    public boolean affiliationCheck(String affiliation){
        for (int i = 0; i < affiliation.length()-1; i++) {  
            String regex = "[a-zA-Z0-9.!?,_:;]+";
            if (! affiliation.matches(regex)) {  
                errorText.setText("The affiliation contains illegal characters");
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
