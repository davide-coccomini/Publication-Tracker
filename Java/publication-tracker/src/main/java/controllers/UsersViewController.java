package controllers;

import java.util.ArrayList;
import java.util.List;

import backend.GraphManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import middleware.User;
import backend.DatabaseManager;

public class UsersViewController {
	private SessionController controller;
	private final DatabaseManager dbManager;
	private int currentUser;
	@FXML
    private AnchorPane topbar;
    @FXML
    private Button updateButton;
    @FXML
    TextField name;
    @FXML
    TextField email;
    @FXML
    TextField password;
    @FXML
	ChoiceBox role;
    @FXML
    Text errorText;

	   public UsersViewController(SessionController c, List<Object> args){
	        controller = c;
	        currentUser =  (int) args.get(0);
	        dbManager = c.getDbManager();
	   }
	    public void initController(){
	        controller.load_Topbar(topbar,0);
	        topbar.toFront();

        	loadUserById(currentUser);

        	enableTextBoxes(false);
	        updateButton.setOnAction(new EventHandler<ActionEvent>() {
	            @Override public void handle(ActionEvent e) {
	            	update();
	            }
	        });

	    }
	    private void enableTextBoxes(Boolean status){
	    	name.setEditable(status);
	    	email.setEditable(status);
		    password.setEditable(status);

	    }
	    private void loadUserById(int id){
	    	Object[] args = new Object[1];
	    	args[0] = (Object) id;
	    	User u = dbManager.getUserById(args);
	    	if(u!=null){
	    		errorText.setText(Integer.toString(u.getId()));

	    		name.setText(u.getUsername());
	    		password.setText(u.getPassword());
	    		email.setText(u.getEmail());

	    		//role.set 	u.getRole();
	    	}
	    	else{
	    		System.out.println("user not found");
	    	}
	    }
	    public void update(){
	    	int id = Integer.parseInt(errorText.getText());
	    	Object[] args = new Object[1];
	    	args[0] = id;
	    	User u = dbManager.getUserById(args);

	    	int newRole =  0; // TODO: read checkbox
	    	if(u.getRole()!=newRole){
	    		updateField(id,"Role",(Object) newRole);
	    	}

	    	checkField(id,u.getEmail(),email.getText(),"email");
	    	checkField(id,u.getUsername(),name.getText(),"username");
	    	checkField(id,u.getPassword(),password.getText(),"password");
	    }
	    private void checkField(int id, String oldVal, String newVal, String fieldName){
	    	if(!oldVal.equals(newVal)){
	    		updateField(id,fieldName,(Object) newVal);
	    	}
	    }
	    private void updateField(int id, String field, Object val){
	    	Object[] args= new Object[2];
	    	args[0] = val;
	    	args[1] = id;
	    	dbManager.updateUserField(field,args);
	    }
}
