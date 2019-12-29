package controllers;

import middleware.GraphManager;
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
import beans.Author;
import beans.Publication;
import beans.User;
import java.io.File;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                controller.navigate(10,null);
            }
        });  
        controller.load_Topbar(topbar,3);
        User u = controller.getLoggedUser();
        if(u.getRole()==1){
            createButton.setVisible(true);
        }else{
            createButton.setVisible(false);
        }
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
                            if(authorsString.length()>0){
                                authorsString = authorsString.substring(0, authorsString.length() - 1);
                            }
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
                     graphManager.deletePublication(id);
                    loadPublications();
                }
            }); 
            iconUpdate.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    List<Object> args = new ArrayList<>();
                    args.add(id);
                    args.add(currentPage);
                    controller.navigate(13,args);
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
                controller.navigate(11,args);
           }
       }); 
        hbox.getChildren().add(iconMore);
       
        return hbox;
    }
    private ObservableList<Publication> getPublicationsList(){
        loadPagesButtons();
        List<Publication> publications = graphManager.getPublications(ELEMENTS_PER_PAGE, currentPage);
        
        final ObservableList<Publication> observablePublications = FXCollections.observableArrayList(publications);

        return observablePublications;
    }
    
    
}
