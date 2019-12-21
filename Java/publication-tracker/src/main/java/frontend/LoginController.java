package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {
    @FXML private TextField email;
    @FXML private TextField password;
    @FXML private Button loginButton;
    SessionController controller;
    
    public LoginController(SessionController c) { 
        controller = c;
    }
    @FXML
    public void authorize() { 

    }

    public void initController() {
      email.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
              authorize();
          }
      });  
      password.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
              authorize();
          }
      });  
      loginButton.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
              authorize();
          }
      });        

      }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
 
    
}