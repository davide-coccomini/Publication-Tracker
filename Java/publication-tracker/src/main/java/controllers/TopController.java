package controllers;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import middleware.User;

public class TopController {
    @FXML
    private Button usersButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private Button authorsButton;
    @FXML
    private Button publicationsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Text roleText;
    @FXML
    private Text nameText;
    
    private final SessionController controller;
    public TopController(SessionController c){
        controller = c;
    }
    public void initController(){
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                logout();
            }
        });
        usersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(3,null);
                setSectionButtonStyle(usersButton);
            }
        });
        statisticsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(6,null);
                setSectionButtonStyle(statisticsButton);
            }
        });
        authorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(5,null);
                setSectionButtonStyle(authorsButton);
            }
        });
        publicationsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(7,null);
                setSectionButtonStyle(publicationsButton);
            }
        });   
        User u = controller.getLoggedUser();
        if(u.getRole()==1){ // Admin
            usersButton.setVisible(true);
        }else{ // User
            usersButton.setVisible(false);
        }
        show_Name();
        
    } 
    private void setSectionButtonStyle(Button focusedButton){
        System.out.println(focusedButton);
        authorsButton.setStyle("-fx-background-color: #28abe3");
        publicationsButton.setStyle("-fx-background-color: #28abe3");
        statisticsButton.setStyle("-fx-background-color: #28abe3");
        usersButton.setStyle("-fx-background-color: #28abe3");
        focusedButton.setStyle("-fx-background-color: #095185");
    }
    private void setSectionButtonStyle(int button){
        switch(button){
            case 0:
              setSectionButtonStyle(usersButton);
              break;
            case 1:
              setSectionButtonStyle(statisticsButton);
              break;
            case 2:
              setSectionButtonStyle(authorsButton);
              break;
            case 3:
              setSectionButtonStyle(publicationsButton);
              break;
        }
    }
    private void show_Name(){
        User u=controller.getLoggedUser();
        roleText.setText(String.valueOf(u.getRole()));
        nameText.setText(u.getUsername());
    }
    public void logout(){
        controller.logout();
    }
}
