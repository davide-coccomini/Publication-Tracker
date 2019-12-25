package controllers;

import java.util.ArrayList;
import java.util.List;

import backend.GraphManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import middleware.User;

public class UsersViewController {
	private SessionController controller;
	private final GraphManager graphManager;
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
	        graphManager = c.getGraphManager();
	   }
	    public void initController(){
	        controller.load_Topbar(topbar);
	        topbar.toFront();

	        String buttonText;
	        if(currentUser!=-1){
	        	loadUserById(currentUser);
	        	buttonText = "Update";
	        	enableTextBoxes(false);
		        updateButton.setOnAction(new EventHandler<ActionEvent>() {
		            @Override public void handle(ActionEvent e) {
		            	update();
		            }
		        });
	        }
	        else {
	        	buttonText = "Create";
	        	enableTextBoxes(true);
		        updateButton.setOnAction(new EventHandler<ActionEvent>() {
		            @Override public void handle(ActionEvent e) {
		            	create();
		            }
		        });
	        }
	        updateButton.setText(buttonText);

	    }
	    private void enableTextBoxes(Boolean status){
	    	name.setEditable(status);
	    	email.setEditable(status);
		    password.setEditable(status);
	//	    role.setEditable(status);
	    }
	    private void loadUserById(int id){
	    	User u = graphManager.getUserById(id);
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
	    public void create(){
	    	long roleid = 1;// get choicebox value
	    	long result = graphManager.addUser(name.getText(), email.getText(), roleid ,password.getText());
	    	if(result<=0){
	    		System.out.println("Error creating user");
	    	}
	    }
	    public void update(){
	    	long id = Long.parseLong(errorText.getText());
	    	graphManager.updateUser(id,name.getText(), email.getText(),password.getText());

	    }
}
