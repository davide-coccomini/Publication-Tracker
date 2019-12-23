/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import backend.DatabaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class AuthorsListController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TableView authorsTable;
    @FXML
    private Button createButton;
    private final SessionController controller;
    private final DatabaseManager dbManager;
    
    public AuthorsListController(SessionController c){
        controller = c;
        dbManager = c.getDbManager();
    }
    public void initController(){
        controller.load_Topbar(topbar);
        topbar.toFront();
    }
    
    
}
