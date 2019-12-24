/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import backend.DatabaseManager;
import backend.GraphManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
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
    
    
    public AuthorsListController(SessionController c){
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar);
        topbar.toFront();
    }
    private void loadAuthors(){
        
    }
    private void loadPagesButtons(){
        
    }
    private ObservableList<Author> getAuthorsList(long page){
 
        loadPagesButtons();
        
       
        final ObservableList<Author> authors = FXCollections.observableArrayList();

        return authors;
    }
    
}
