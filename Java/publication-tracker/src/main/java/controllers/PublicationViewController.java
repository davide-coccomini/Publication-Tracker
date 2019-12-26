package controllers;

import beans.Author;
import beans.Publication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import middleware.GraphManager;

public class PublicationViewController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private TableView informationTable;
    @FXML
    private TableView citationsTable;
        
    private final SessionController controller;
    private final GraphManager graphManager;
    private final int currentPage;
    private final long publicationId;
    
    public PublicationViewController(SessionController c, List<Object> args){
        publicationId = (long)args.get(0);
        currentPage = (int)args.get(1);
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar, 3);
        loadInformation();
        loadCitations();
    } 
    private void loadInformation(){
        final Publication publication = graphManager.getPublicationById(publicationId);
        final List<Publication> publications = new ArrayList();
        publications.add(publication);
        TableColumn<Publication,String> publication_Name_Col;
        publication_Name_Col = new TableColumn<>("Name");
        publication_Name_Col.setCellValueFactory(new PropertyValueFactory("name"));
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
        informationTable.getColumns().setAll(publication_Name_Col,authors_Col);
        informationTable.setItems(FXCollections.observableArrayList(publications));
    }
    
    private void loadCitations(){
        final List<Publication> publications = graphManager.getPublicationCitations(publicationId);
        TableColumn<Publication,String> publication_Name_Col;
        publication_Name_Col = new TableColumn<>("Name");
        publication_Name_Col.setCellValueFactory(new PropertyValueFactory("name"));
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
        citationsTable.getColumns().setAll(publication_Name_Col,authors_Col);
        citationsTable.setItems(FXCollections.observableArrayList(publications)); 
    }
    
}
