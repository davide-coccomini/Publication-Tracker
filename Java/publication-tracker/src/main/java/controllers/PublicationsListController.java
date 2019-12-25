package controllers;

import backend.GraphManager;
import java.util.ArrayList;
import java.util.List;
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
import middleware.Publication;

public class PublicationsListController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TableView publicationsTable;
    @FXML
    private Button createButton;

    @FXML 
    private HBox pagesButtons;

    private final SessionController controller;
    private final GraphManager graphManager;
    private int currentPage;
    private final int ELEMENTS_PER_PAGE = 10;
    private final int BUTTONS_TO_SHOW = 6;
    
    public PublicationsListController(SessionController c){
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar,3);
        topbar.toFront();
        loadPublications();
    }
    private void loadPublications(){
        final ObservableList<Publication> publications = getPublicationsList();
        TableColumn<Publication,String> name_Col;
        name_Col = new TableColumn<>("Name");
        name_Col.setCellValueFactory(new PropertyValueFactory("name"));
        TableColumn authors_Col = new TableColumn("Authors");
        authors_Col.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactoryAuthors = //
                new Callback<TableColumn<Object, String>, TableCell<Object, String>>() {
            @Override
            public TableCell call(final TableColumn<Object, String> param) {
                final TableCell<Object, String> cell = new TableCell<Object, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(getIndex()<publications.size() && getIndex() >= 0){
                            Publication p = publications.get(getIndex());
                            List<Author> authors = graphManager.getPublicationAuthors(p.getId());
                            String authorsString = "";
                            for(Author author:authors){
                                 authorsString += author.getName() + ",";
                            }
                            authorsString = authorsString.substring(0, authorsString.length() - 1);
                            setText(authorsString);
                        }
                    }
                };
                return cell;
            }
        };
        authors_Col.setCellFactory(cellFactoryAuthors);
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
                        if(getIndex()<publications.size() && getIndex() >= 0){
                            Publication p = publications.get(getIndex());
                            setGraphic(make_Buttons(p.getId()));
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        action_Col.setCellFactory(cellFactory);
        publicationsTable.getColumns().setAll(action_Col,name_Col, authors_Col);
        
        publicationsTable.setItems(publications);
    }
    void loadPagesButtons(){  
        int elements = graphManager.getPublicationsNumber();
        long pages = elements/ELEMENTS_PER_PAGE;
        
        while(!pagesButtons.getChildren().isEmpty()){
            pagesButtons.getChildren().remove(0);
        }
         
        if(currentPage > 0){
            Button btt = new Button();
            btt.setText("←");
            btt.setMinWidth(40);
            btt.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white; -fx-background-radius: 50 50 50 50;");
            btt.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    currentPage = (int) currentPage - 1;
                    loadPagesButtons();
                    loadPublications();
                }
            });   
            pagesButtons.getChildren().add(btt);    
        }
        for(long i=currentPage -((currentPage >= BUTTONS_TO_SHOW)?BUTTONS_TO_SHOW:currentPage); i <= pages && i <= currentPage + BUTTONS_TO_SHOW; i++){
            final long currentPageIndex = i;
                        
            Button b = new Button();
            b.setText((Long.toString(i+1)));
            b.setMinWidth(40);
            if(i == currentPage)
                b.setStyle("-fx-background-color: #095185; -fx-text-fill: white; -fx-background-radius: 50 50 50 50;");
            else
                b.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white; -fx-background-radius: 50 50 50 50;");
            
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    currentPage = (int) currentPageIndex;
                    loadPagesButtons();
                    loadPublications();
                }
            });  
             
            pagesButtons.getChildren().add(b);
        }
         
        if(currentPage < pages){
            Button btt = new Button();
            btt.setText("→");
            btt.setMinWidth(40);
            btt.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white; -fx-background-radius: 50 50 50 50;");
            btt.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    currentPage = (int) currentPage + 1;
                    loadPagesButtons();
                    loadPublications();
                }
            });   
            pagesButtons.getChildren().add(btt);
        }
    }
    private HBox make_Buttons(final long id){
        Button b1 = new Button();
        b1.setText("DELETE");
        b1.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white");
        
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                graphManager.deletePublication(id);
                loadPublications();
            }
        });    
        
        Button b2 = new Button();
        b2.setText("VISUALIZE");
        b2.setStyle("-fx-background-color: #28abe3; -fx-text-fill: white");
        
        b2.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                List<Object> args = new ArrayList<>();
                args.add(id);
                args.add(currentPage);
                controller.navigate(7,args);
            }
        });    
        
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(0, 0, 0, 10));  
        hbox.getChildren().addAll(b1,b2);
       
        return hbox;
    }
    private ObservableList<Publication> getPublicationsList(){
        loadPagesButtons();
        List<Publication> publications = graphManager.getPublications(ELEMENTS_PER_PAGE, currentPage);
        
        final ObservableList<Publication> observablePublications = FXCollections.observableArrayList(publications);

        return observablePublications;
    }
    
    
}
