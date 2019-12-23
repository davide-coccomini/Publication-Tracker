package frontend;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import middleware.User;

public class MenuController {
    @FXML
    private Label label;
    @FXML
    private Button buttonAuthors;
    @FXML
    private Font x1;
    @FXML
    private Button buttonPublications;
    @FXML
    private Button buttonStatistics;
    @FXML
    private Button buttonUsers;
    @FXML
    private ImageView buttonLogout;

  private final SessionController controller;
 
    public MenuController(SessionController c){
        controller = c;
    }
    public void initController(){
        
        buttonAuthors.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(5,null);
                }
            });    
        
        buttonPublications.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(7,null);
                }
            }); 
       
        buttonStatistics.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(6,null);
                }
            });  
       
        buttonUsers.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(3,null);
                }
            }); 
        buttonLogout.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    controller.logout();
                    event.consume();
                }
           }); 
        User u = controller.getLoggedUser();
        if(u.getRole()==1){ // Admin
            buttonUsers.setVisible(true);
        }else{ // User
            buttonUsers.setVisible(false);
        }
    }
    
}
