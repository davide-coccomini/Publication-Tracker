package controllers;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import beans.User;
import javafx.scene.shape.Line;

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
    @FXML 
    private Line usersLine;
    int page;
    
    private final SessionController controller;
    public TopController(SessionController c, int page){
        controller = c;
        this.page = page;
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
            usersLine.setVisible(true);
            usersButton.setVisible(true);
        }else{ // User
            usersLine.setVisible(false);
            usersButton.setVisible(false);
        }
        show_Name();
        setSectionButtonStyle(page);
        
    } 
    private void setSectionButtonStyle(Button focusedButton){
        authorsButton.setStyle("-fx-background-color: #28abe3;-fx-background-radius: 50 50 50 50;");
        publicationsButton.setStyle("-fx-background-color: #28abe3; -fx-background-radius: 50 50 50 50;");
        statisticsButton.setStyle("-fx-background-color: #28abe3; -fx-background-radius: 50 50 50 50;");
        usersButton.setStyle("-fx-background-color: #28abe3; -fx-background-radius: 50 50 50 50;");
        focusedButton.setStyle("-fx-background-color: #095185;-fx-background-radius: 50 50 50 50;");
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
        String role = (u.getRole()==1)?"Admin":"User";
        roleText.setText(role);
        nameText.setText(u.getUsername());
    }
    public void logout(){
        controller.logout();
    }
}
