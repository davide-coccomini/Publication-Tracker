/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import backend.DatabaseManager;
import backend.GraphManager;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import middleware.Author;

public class AuthorsListController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TableView authorsTable;
    @FXML
    private Button createButton;
    private final SessionController controller;
    private final GraphManager graphManager;
    private int currentPage;
    private int ELEMENTS_PER_PAGE = 15;
    
    public AuthorsListController(SessionController c){
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar);
        topbar.toFront();
        loadAuthors();
    }
    private void loadAuthors(){
        final ObservableList<Author> authors = getAuthorsList();
        TableColumn<Author,String> name_Col;
        name_Col = new TableColumn<>("Name");
        name_Col.setCellValueFactory(new PropertyValueFactory("name"));
        TableColumn<Author,String> email_Col;
        email_Col = new TableColumn<>("Email");
        email_Col.setCellValueFactory(new PropertyValueFactory("email"));
        TableColumn<Author,String> heading_Col;
        heading_Col = new TableColumn<>("Heading");
        heading_Col.setCellValueFactory(new PropertyValueFactory("heading"));
        TableColumn<Author,String> affiliation_Col;
        affiliation_Col = new TableColumn<>("Affiliation");
        affiliation_Col.setCellValueFactory(new PropertyValueFactory("affiliation"));
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
                        if(getIndex()<authors.size() && getIndex() >= 0){
                            Author a = authors.get(getIndex());
                            setGraphic(null);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        action_Col.setCellFactory(cellFactory);
        authorsTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col, action_Col);
        
        authorsTable.setItems(authors);
    }
    private void loadPagesButtons(){
        
    }
    private ObservableList<Author> getAuthorsList(){
        loadPagesButtons();
        List<Author> authors = graphManager.getAuthors(ELEMENTS_PER_PAGE, currentPage);
        
        final ObservableList<Author> observableAuthors = FXCollections.observableArrayList(authors);

        return observableAuthors;
    }
    
}
