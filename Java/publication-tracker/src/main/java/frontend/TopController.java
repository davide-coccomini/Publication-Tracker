/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frontend;

import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    
    private SessionController controller;
    public TopController(SessionController c){
        controller = c;
    }
    public void initController(){
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                logout();
                }
            });
        authorsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(3,null);
                setSectionButtonStyle(authorsButton);
                }
        });
        publicationsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(4,null);
                setSectionButtonStyle(publicationsButton);
                }
        });   
        statisticsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(5,null);
                setSectionButtonStyle(statisticsButton);
                }
        });
        usersButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(6,null);
                setSectionButtonStyle(usersButton);
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
        authorsButton.setStyle("-fx-background-color: #28abe3");
        publicationsButton.setStyle("-fx-background-color: #28abe3");
        statisticsButton.setStyle("-fx-background-color: #28abe3");
        usersButton.setStyle("-fx-background-color: #28abe3");
        focusedButton.setStyle("-fx-background-color: #095185");
    }
    private void show_Name(){
        
    }
    public void logout(){
        controller.logout();
    }
}
