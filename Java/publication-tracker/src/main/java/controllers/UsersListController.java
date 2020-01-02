package controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import beans.User;
import java.io.File;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import middleware.DatabaseManager;

public class UsersListController {
	@FXML
    private AnchorPane topbar;
    @FXML
    private TableView usersTable;
    
    private final SessionController controller;
    private final DatabaseManager dbManager;

    public UsersListController(SessionController c){
        controller = c;
        dbManager = c.getDbManager();
    }
    public void initController(){
        controller.load_Topbar(topbar,0);
        loadUsers();

    }
    private void loadUsers(){
        final ObservableList<User> users = getUsersList();
        TableColumn<User,String> name_Col;
        name_Col = new TableColumn<>("Username");
        name_Col.setCellValueFactory(new PropertyValueFactory("username"));
        TableColumn<User,String> email_Col;
        email_Col = new TableColumn<>("Email");
        email_Col.setCellValueFactory(new PropertyValueFactory("email"));

        TableColumn action_Col = new TableColumn("Action");
        action_Col.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactory = //
                new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
            @Override
            public TableCell call(final TableColumn<Object, String> param) {
                final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(getIndex()<users.size() && getIndex() >= 0){
                            User u = users.get(getIndex());
                            setGraphic(make_Buttons(u.getId()));
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        action_Col.setCellFactory(cellFactory);
        usersTable.getColumns().setAll(action_Col,name_Col, email_Col);

        usersTable.setItems(users);
    }
    private HBox make_Buttons(final long id){
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(0, 0, 0, 10));
        
        File file = new File("src/main/resources/assets/delete.png");
        Image imageDelete = new Image(file.toURI().toString());
        ImageView iconDelete = new ImageView(imageDelete);
        iconDelete.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.HAND); 
            }
        });
        iconDelete.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.DEFAULT); 
            }
        });
        file = new File("src/main/resources/assets/update.png");
        Image imageUpdate = new Image(file.toURI().toString());
        ImageView iconUpdate = new ImageView(imageUpdate);
        iconUpdate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.HAND); 
            }
        });
        iconUpdate.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.DEFAULT);
            }
        });
     
        iconDelete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Object[] arg = new Object[1];
                arg[0] = (Object) id;
                dbManager.deleteUser(arg);
                loadUsers();
            }
        }); 
        iconUpdate.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                List<Object> args = new ArrayList<>();
                args.add(id);
                controller.navigate(4,args);
            }
        }); 
        hbox.getChildren().addAll(iconDelete,iconUpdate);
        return hbox;
    }
    private ObservableList<User> getUsersList(){
    	List<User> users = null;
    	try{
        users =  (List<User>) (List) dbManager.getUsers();
    	}catch(Exception e){
    		System.out.println("Error fetching user list");
    		System.out.println(e);
    	}
        final ObservableList<User> observableAuthors = FXCollections.observableArrayList(users);

        return observableAuthors;
    }
}
