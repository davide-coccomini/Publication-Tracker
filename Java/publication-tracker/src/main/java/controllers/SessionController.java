package controllers;

import middleware.DatabaseManager;
import middleware.GraphManager;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import beans.User;

public class SessionController {
    private User session;
     private final Scene scene;
    private final Stage stage;

    private final DatabaseManager dbManager;
    private final GraphManager graphManager;

   
    public void setSession(User session) {

        if(session==null){
            this.session = new User(0,"Unknown","Unknown","Unknown",0);
        }else{
            this.session = session;
        }
    }
    public SessionController(DatabaseManager dm,GraphManager gm, Scene scene, Stage stage){
        dbManager = dm;
        graphManager = gm;
        this.scene = scene;
        this.stage = stage;
        session = null;
    }
    public User getLoggedUser(){
        return session;
    }
    public DatabaseManager getDbManager(){
        return dbManager;
    }
    public GraphManager getGraphManager(){
        return graphManager;
    }
    public void navigate(int page, List<Object> args) {
         switch(page){
            case 0: // Login
               LoginController c0 = new LoginController(this);
               load_Page(c0,"login.fxml");
               c0.initController();
               break;
            case 1: // Registration
               RegistrationController c1 = new RegistrationController(this);
               load_Page(c1,"registration.fxml");
               c1.initController();
               break;
            case 2: // Menu
               MenuController c2 = new MenuController(this);
               load_Page(c2,"menu.fxml");
               c2.initController();
               break;
            case 3: //  Users List
            	UsersListController c3 = new UsersListController(this);
                load_Page(c3,"usersList.fxml");
                c3.initController();
                break;
            case 4: //  Users View
            	UserViewController c4 = new UserViewController(this,args);
                load_Page(c4,"userView.fxml");
                c4.initController();
                break;
            case 5: //  Authors List
                AuthorsListController c5 = new AuthorsListController(this);
                load_Page(c5,"authorsList.fxml");
                c5.initController();
                break;
            case 6: //  Statistics
                StatisticsController c6 = new StatisticsController(this);
                load_Page(c6,"statistics.fxml");
                c6.initController();
                break;
            case 7: //  Publication List
                PublicationsListController c7 = new PublicationsListController(this);
                load_Page(c7,"publicationsList.fxml");
                c7.initController();
                break;
         }
        stage.sizeToScene();
    }

    private <T extends Object> void load_Page(T controller, String page){
        String url = "/fxml/"+page;
        try{
            java.net.URL target = getClass().getResource(url);
            FXMLLoader loader = new FXMLLoader(target);
            loader.setController(controller);
            scene.setRoot((Parent) loader.load());
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    public TopController load_Topbar(AnchorPane pane, int page){
        String url = "/fxml/topbar.fxml";
        TopController controller = new TopController(this, page);
        try{
            java.net.URL target = getClass().getResource(url);
            FXMLLoader loader = new FXMLLoader(target);
            loader.setController(controller);

            AnchorPane newPane =  FXMLLoader.load(target);
            pane.getChildren().add(newPane);
            pane.getChildren().add((AnchorPane) loader.load());
            controller.initController();
        }catch(IOException e){
            System.out.println(e);
        }
        return controller;
    }
    public void logout(){
        session = null;
        navigate(0,null);
    }

}
