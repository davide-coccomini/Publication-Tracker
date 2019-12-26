
package controllers;

import beans.Author;
import beans.Publication;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import middleware.GraphManager;

public class StatisticsController {
    @FXML
    private AnchorPane topbar;
    @FXML
    private AnchorPane statisticsContainer;
    @FXML
    private Text mostCitedPublicationResult;
    @FXML
    private Text mostCitedAuthorResult;
    @FXML
    private Text numAuthorsResult;
    @FXML
    private Text numPublicationsResult;
    @FXML
    private TextField queryTextField;
    @FXML 
    private ChoiceBox querySelector;
    @FXML 
    private Button queryButton;
    @FXML
    private TableView queryResultTable;
    @FXML
    private AnchorPane noResultMessageContainer;
    @FXML
    private Button showStatisticsButton;
    
    private final SessionController controller;
    private final GraphManager graphManager;
    
    public StatisticsController(SessionController c){
        controller = c;
        graphManager = c.getGraphManager();
    }
    public void initController(){
        controller.load_Topbar(topbar, 1);
        
              
        queryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                executeQuery();
                }
            });   
        showStatisticsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                showStatistics();
                }
            });   
        
        mostCitedAuthorResult.wrappingWidthProperty().set(150.0);
        mostCitedPublicationResult.wrappingWidthProperty().set(150.0);
        loadStatistics();
    }
    private void loadStatistics(){
        numAuthorsResult.setText(String.valueOf(graphManager.getAuthorsNumber()));
        numPublicationsResult.setText(String.valueOf(graphManager.getPublicationsNumber()));
        mostCitedAuthorResult.setText(graphManager.getMostCitedAuthor().getName());
        mostCitedPublicationResult.setText(graphManager.getMostCitedPublication().getName());
    }
    private void executeQuery(){
        String queryType = (String) querySelector.getValue();
        String queryValue = queryTextField.getText();
        if(queryType == null){
            showNoResultMessage();
        }
        switch(queryType){
            case "Common coauthors":
                Author author =  graphManager.getAuthorBy("name", queryValue);
                if(author == null){
                    showNoResultMessage();
                    break;
                }
                long authorId =author.getId();
                
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
                queryResultTable.getColumns().setAll(name_Col, email_Col, heading_Col, affiliation_Col);
                queryResultTable.setItems(FXCollections.observableArrayList(coauthors));
                showTableResult();
                break;
            case "Publication citations":
                Publication publication =  graphManager.getPublicationBy("name", queryValue);
                if(publication == null){
                    showNoResultMessage();
                    break;
                }
                long publicationId = publication.getId();
                System.out.println(publicationId);
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
                queryResultTable.getColumns().setAll(publication_Name_Col, authors_Col);
                queryResultTable.setItems(FXCollections.observableArrayList(publications));
                showTableResult();
                break;
            default:
                showNoResultMessage();
                break;
        }
        
    }
    private void showTableResult(){
        queryResultTable.setVisible(true);
        statisticsContainer.setVisible(false);
        noResultMessageContainer.setVisible(false);
    }
    private void showNoResultMessage(){
        queryResultTable.setVisible(false);
        statisticsContainer.setVisible(false);
        noResultMessageContainer.setVisible(true);
    }
    private void showStatistics(){
        queryResultTable.setVisible(false);
        statisticsContainer.setVisible(true);
        noResultMessageContainer.setVisible(false);
    }
}
