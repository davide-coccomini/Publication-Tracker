package com.mycompany.publication.tracker;

import backend.GraphManager;
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        Scene scene = new Scene(root);
        
        
        graphManager = new GraphManager("bolt://localhost:7687", "neo4j", "test");
        long id = graphManager.addAuthor("Davide Coccomini", "d.coccomini@studenti.unipi.it", "University of Pisa");
        System.out.println(id);
        graphManager.printAuthors("Davide");
        stage.setTitle("Task 3");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}