package controllers;

import beans.Author;
import beans.Publication;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import middleware.GraphManager;

public class PublicationUpdateController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private Text errorText;
    @FXML
    private TextField nameText;
    @FXML
    private Button updateButton;

    private final SessionController controller;
    private final GraphManager graphManager;
    private final int currentPage;
    private final long publicationId;
    
    public PublicationUpdateController(SessionController c, List<Object> args){
        publicationId = (long)args.get(0);
        currentPage = (int)args.get(1);
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                updatePublication();
            }
        });  
        controller.load_Topbar(topbar, 3);
        loadInformation();  
    } 
    private void loadInformation(){
        Publication publication = graphManager.getPublicationById(publicationId);
        nameText.setText(publication.getName());
    }
    private void updatePublication(){
        String name = nameText.getText();
        graphManager.updatePublication(publicationId, name);
        controller.navigate(7, null);
    }
    public boolean nameCheck(String name){
        for (int i = 0; i < name.length()-1; i++) {  
            String regex = "[a-zA-Z0-9.!?,_:;+ ]";
            if (! name.matches(regex)) {  
                errorText.setText("The name contains illegal characters");
                return false;
            }  
        }
        return true;
    }
}
