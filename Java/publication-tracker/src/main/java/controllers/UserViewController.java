package controllers;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import beans.User;
import middleware.DatabaseManager;

public class UserViewController {
    private SessionController controller;
    private final DatabaseManager dbManager;
    private long currentUser;
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
    long currentUserId;
	   public UserViewController(SessionController c, List<Object> args){
	        controller = c;
	        currentUser = (long) args.get(0);
	        dbManager = c.getDbManager();
	   }
	    public void initController(){
	        controller.load_Topbar(topbar,0);
	        topbar.toFront();

        	loadUserById(currentUser);
                
                enableTextBoxes(controller.getLoggedUser().getRole()==1); // role 1 = admin
                
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
	    private void loadUserById(long id){
                
	    	Object[] args = new Object[1];
	    	args[0] = (Object) id;
	    	User u = dbManager.getUserById(args);
	    	if(u!=null){ 
                    currentUserId= id;
                    name.setText(u.getUsername());
                    password.setText(u.getPassword());
                    email.setText(u.getEmail());
                    Object r = "User";
                    if(u.getRole()==1){
                            r = "Admin";
                    }
                    role.setValue(r);
	    	}
	    	else{
	    		System.out.println("user not found");
	    	}
	    }
	    public void update(){
	    	 
	    	Object[] args = new Object[1];
	    	args[0] = currentUserId;
	    	User u = dbManager.getUserById(args);

	    	int newRole = 0;
	    	try{
	    		String read = role.getValue().toString();
	    		if("Admin".equals((String) read)){
	    			newRole=1;
	    		}
	    	}catch(Exception e){
	    		System.out.print("Error in reading role input: ");
	    		System.out.println(e);
	    	}
	    	if(u.getRole()!=newRole){
	    		updateField(currentUserId,"Role",(Object) newRole);
	    	}

	    	checkField(currentUserId,u.getEmail(),email.getText(),"email");
	    	checkField(currentUserId,u.getUsername(),name.getText(),"name");
	    	checkField(currentUserId,u.getPassword(),password.getText(),"password");
	    }
	    private void checkField(long id, String oldVal, String newVal, String fieldName){
	    	if(!oldVal.equals(newVal)){
	    		updateField(id,fieldName,(Object) newVal);
	    	}
	    }
	    private void updateField(long id, String field, Object val){
	    	Object[] args= new Object[2];
	    	args[0] = val;
	    	args[1] = id;
	    	dbManager.updateUserField(field,args);
	    }
}
