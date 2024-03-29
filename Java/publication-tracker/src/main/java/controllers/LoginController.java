package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import beans.User;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registrationButton;
    @FXML private Text errorText;
    SessionController controller;
    
    public LoginController(SessionController c) { 
        controller = c;
    }
     @FXML
    public void authorize() { 
            String e = emailField.getText();
            String p = passwordField.getText();
            if("".equals(e) || "".equals(p)){
                errorText.setVisible(true);
                return;
            }
            Object[] args = new Object[2];
            args[0] = e;
            args[1] = p;
            User session = controller.getDbManager().autentication(args);
            if (session != null) {
                controller.setSession(session);
                controller.navigate(2,null); // go to menu
                errorText.setVisible(false);
            }else{
                System.out.println("Login failed: wrong credentials");
                errorText.setVisible(true);
            }
     
    }

    public void initController() {
        emailField.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                authorize();
            }
        });  
        passwordField.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                authorize();
            }
        });  
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                authorize();
            }
        });   
        registrationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(1, null);
            }
        });   
    }
}