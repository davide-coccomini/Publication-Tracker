package controllers;

import middleware.DatabaseManager;
import middleware.GraphManager;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
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
import beans.Author;
import beans.User;
import java.io.File;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class AuthorsListController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TableView authorsTable;
    @FXML
    private Button createButton;
    @FXML 
    private HBox pagesButtons;

    private final SessionController controller;
    private final GraphManager graphManager;
    private int currentPage;
    private final int ELEMENTS_PER_PAGE = 10;
    private final int BUTTONS_TO_SHOW = 6;
    
    public AuthorsListController(SessionController c){
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(9,null);
            }
        });  
        controller.load_Topbar(topbar, 2);
        User u = controller.getLoggedUser();
        if(u.getRole()==1){
            createButton.setVisible(true);
        }else{
            createButton.setVisible(false);
        }
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
                            setGraphic(make_Buttons(a.getId()));
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        action_Col.setCellFactory(cellFactory);
        authorsTable.getColumns().setAll(action_Col,name_Col, email_Col, heading_Col, affiliation_Col);
        
        authorsTable.setItems(authors);
    }
    void loadPagesButtons(){  
        int elements = graphManager.getAuthorsNumber();
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
                    loadAuthors();
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
                    loadAuthors();
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
                    loadAuthors();
                }
            });   
            pagesButtons.getChildren().add(btt);
        }
    }
    private HBox make_Buttons(final long id){
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
        file = new File("src/main/resources/assets/more.png");
        Image imageMore = new Image(file.toURI().toString());     
        ImageView iconMore = new ImageView(imageMore);
        iconMore.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.HAND); 
            }
        });
        iconMore.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
                controller.getScene().setCursor(Cursor.DEFAULT);
            }
        });
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(0, 0, 0, 10));  
        int role = controller.getLoggedUser().getRole();
        if(role == 1){
            iconDelete.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    graphManager.deleteAuthor(id);
                    loadAuthors();
                }
            }); 
            iconUpdate.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                   List<Object> args = new ArrayList<>();
                   args.add(id);
                   args.add(currentPage);
                   controller.navigate(12,args);
                }
            }); 
            hbox.getChildren().addAll(iconDelete,iconUpdate);
        }
        
        iconMore.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                   List<Object> args = new ArrayList<>();
                   args.add(id);
                   args.add(currentPage);
                   controller.navigate(8,args);
                }
            }); 

        
        hbox.getChildren().add(iconMore);
       
        return hbox;
    }
    private ObservableList<Author> getAuthorsList(){
        loadPagesButtons();
        List<Author> authors = graphManager.getAuthors(ELEMENTS_PER_PAGE, currentPage);
        
        final ObservableList<Author> observableAuthors = FXCollections.observableArrayList(authors);

        return observableAuthors;
    }
    
}
