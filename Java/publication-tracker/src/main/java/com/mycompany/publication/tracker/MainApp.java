package com.mycompany.publication.tracker;

import backend.DatabaseManager;
import backend.GraphManager;
import frontend.SessionController;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
    private GraphManager graphManager;
    private DatabaseManager dbManager;
    private SessionController controller;
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new StackPane());
        graphManager = new GraphManager("bolt://localhost:7687", "neo4j", "test");
        dbManager = new DatabaseManager();
        dbManager.connectionStart();
        controller = new SessionController(dbManager,graphManager,scene,stage);
        controller.navigate(0,null); // go to login
        

        stage.setTitle("Task 3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public void graphTest(){
        List<Long> authors = new ArrayList();
        authors.add(graphManager.addAuthor("Davide Coccomini", "d.coccomini@studenti.unipi.it", "University of Pisa"));
        authors.add(graphManager.addAuthor("Marilisa Lippini", "m.lippini@studenti.unipi.it", "University of Pisa"));
        
        List<Long> citations = new ArrayList();
        citations.add(graphManager.addPublication("Test1", 2018, authors, citations));
        citations.add(graphManager.addPublication("Test2", 2019, authors, citations));
    }
}