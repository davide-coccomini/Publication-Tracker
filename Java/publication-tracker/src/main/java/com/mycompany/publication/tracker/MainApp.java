package com.mycompany.publication.tracker;

import backend.GraphManager;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private GraphManager graphManager;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root);
        
        
        graphManager = new GraphManager("bolt://localhost:7687", "neo4j", "test");
        List<Long> authors = new ArrayList();
        authors.add(graphManager.addAuthor("Davide Coccomini", "d.coccomini@studenti.unipi.it", "University of Pisa"));
        authors.add(graphManager.addAuthor("Marilisa Lippini", "m.lippini@studenti.unipi.it", "University of Pisa"));
        
        List<Long> citations = new ArrayList();
        citations.add(graphManager.addPublication("Test1", 2018, authors, citations));
        citations.add(graphManager.addPublication("Test2", 2019, authors, citations));
        stage.setTitle("Task 3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}