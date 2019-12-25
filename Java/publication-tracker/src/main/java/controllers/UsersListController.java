package controllers;

import java.util.ArrayList;
import java.util.List;

import backend.GraphManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import middleware.Author;
import middleware.User;
import backend.DatabaseManager;

public class UsersListController {
	@FXML
    private AnchorPane topbar;
    @FXML
    private TableView usersTable;
    @FXML
    private Button createButton;

    private final SessionController controller;
    private final DatabaseManager dbManager;

    public UsersListController(SessionController c){
        controller = c;
        dbManager = c.getDbManager();
    }
    public void initController(){
        controller.load_Topbar(topbar);
        topbar.toFront();
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
        Button b1 = new Button();
        b1.setText("DELETE");
        b1.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white");

        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	Object[] arg = new Object[1];
            	arg[0] = (Object) id;
                dbManager.deleteUser(arg);
                loadUsers();
            }
        });

        Button b2 = new Button();
        b2.setText("VISUALIZE");
        b2.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white");

        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                List<Object> args = new ArrayList<>();
                args.add(id);
                controller.navigate(4,args);
            }
        });

        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(0, 0, 0, 10));
        hbox.getChildren().addAll(b1,b2);

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
