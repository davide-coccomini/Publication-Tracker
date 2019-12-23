/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import backend.DatabaseManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import middleware.User;


public class RegistrationController {
    @FXML
    private TextField emailText;
    @FXML
    private PasswordField passwordText;
    @FXML
    private Button createButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField nameText;
    @FXML
    private PasswordField passwordConfirmText;  
    @FXML
    private Text errorText;
    
    private final SessionController controller;
    private final DatabaseManager dbManager;
    
    public RegistrationController(SessionController c){
        controller = c;
        dbManager = c.getDbManager();
    }
    
    public void initController(){

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                createAccount();
            }
        }); 
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(0,null);
            }
        }); 
    }
     
    private void createAccount(){
        errorText.setText("");
        String email = emailText.getText();
        String name = nameText.getText();
        String password = passwordText.getText();
        String passwordConfirm = passwordConfirmText.getText();
        if(!nameCheck(name) || !emailCheck(email) || !passwordCheck(password,passwordConfirm)) {
            System.out.println("User can't be created");
            return;
        }
        Object[] args = new Object[4];
        args[0] = name;
        args[1] = password;
        args[2] = email;
        args[3] = 0;
        
        dbManager.createUser(args);
        controller.navigate(0, null);
    }
    
    public boolean nameCheck(String name){
        for (int i = 0; i < name.length()-1; i++) {  
            String regex = "[a-zA-Z ]+";
            if (! name.matches(regex)) {  
                errorText.setText("Your name must contain only alphabetic characters");
                return false;
            }  
        }
        return true;
    }
    public boolean emailCheck(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if(!email.matches(regex)){
            errorText.setText("Your email is invalid");
            return false;
        }
        
        Object[] args = new Object[1];
        args[0] = email;
        User u = dbManager.getUserByEmail(args);
 
        return u == null;
    }
    public boolean passwordCheck(String password, String passwordConfirm){
        if (password == null ? passwordConfirm != null : !password.equals(passwordConfirm)){
            errorText.setText("The two passwords don't match");
            return false;
        }
        if (password.length() < 8) {   
            errorText.setText("Your password must contain at least 8 characters");
            return false;
        } else {      
            String regex = "[a-zA-Z]+[0-9]+";
            if(!password.matches(regex)){
                errorText.setText("Your password must contain at least a digit");
                return false;
            }
        }
        return true;
    }  
}
