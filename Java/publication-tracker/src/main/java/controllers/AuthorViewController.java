
package controllers;

import beans.Author;
import beans.Publication;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import middleware.GraphManager;

public class AuthorViewController {
    @FXML
    private TableView informationTable;
    @FXML
    private TableView coauthorsTable;
    @FXML
    private TableView publicationsTable;
    @FXML
    private TableView commonCoauthorsTable;
    @FXML
    private TableView indirectCoauthorsTable;
    @FXML
    private AnchorPane noResultMessageContainer;
    @FXML
    private AnchorPane topbar;
    @FXML
    private AnchorPane resultContainer;
    @FXML
    private AnchorPane informationContainer;
    @FXML
    private Button queryButton;
    @FXML
    private Button showAuthorButton;
    @FXML
    private TextField queryTextField;
    @FXML
    private Button goBackButton;
   
    
    private final SessionController controller;
    private final GraphManager graphManager;
    private final int currentPage;
    private final long authorId;
    
    public AuthorViewController(SessionController c, List<Object> args){
        authorId = (long)args.get(0);
        currentPage = (int)args.get(1);
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar, 2);
        loadInformation();
        loadCoauthors();
        loadIndirectAuthors();
        loadPublications();
        queryButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            executeQuery();
            }
        });   
        showAuthorButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            showAuthor();
            }
        });  
        goBackButton.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent e) {
            showAuthor();
            }
        }); 
    } 
    private void executeQuery(){
        String authorName = queryTextField.getText();
        Author searchedAuthor = graphManager.getAuthorBy("name", authorName);
        if(searchedAuthor == null){
            showNoResultMessage();
            return;
        }
        long searchedAuthorId = searchedAuthor.getId();
        List<Author> commonCoauthors = graphManager.getCommonCoauthors(authorId, searchedAuthorId);
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
        commonCoauthorsTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col);
        commonCoauthorsTable.setItems(FXCollections.observableArrayList(commonCoauthors));
        if(commonCoauthors.size()>0){
            showTableResult();
        }else{
            showNoResultMessage();
        }
    }
    private void loadInformation(){
        Author author = graphManager.getAuthorById(authorId);
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
        informationTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col);
        informationTable.setItems(FXCollections.observableArrayList(author));
    }
    private void loadIndirectAuthors(){
        List<Author> indirectCoAuthors = graphManager.getIndirectCoauthors(authorId);
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
        indirectCoauthorsTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col);
        indirectCoauthorsTable.setItems(FXCollections.observableArrayList(indirectCoAuthors));
    }
    private void loadCoauthors(){
        List<Author> coauthors = graphManager.getCoauthors(authorId);
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
        coauthorsTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col);
        coauthorsTable.setItems(FXCollections.observableArrayList(coauthors));
    }
    private void loadPublications(){
        final List<Publication> publications = graphManager.getAuthorPublications(authorId);
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
        publicationsTable.getColumns().setAll(publication_Name_Col, authors_Col);
        if(publications != null){
            publicationsTable.setItems(FXCollections.observableArrayList(publications));
        }
    }
    private void showTableResult(){
        resultContainer.setVisible(true);
        informationContainer.setVisible(false);
        noResultMessageContainer.setVisible(false);
    }
    private void showNoResultMessage(){
        resultContainer.setVisible(false);
        informationContainer.setVisible(false);
        noResultMessageContainer.setVisible(true);
    }
    private void showAuthor(){
        resultContainer.setVisible(false);
        informationContainer.setVisible(true);
        noResultMessageContainer.setVisible(false);
    }
}
