package frontend;

import backend.DatabaseManager;
import backend.GraphManager;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import middleware.User;

public class SessionController {
    private User session;
     private Scene scene;
    private Stage stage;
    
    private DatabaseManager dbManager;
    private GraphManager graphManager;
    
    
    public void setSession(User session) {

        if(session==null){
            session = new User (0,"Unknown","Unknown","Unknown",0);
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
               
                break;
             case 2: // Menu 
                MenuController c2 = new MenuController(this);
                load_Page(c2,"menu.fxml");
                c2.initController();
                break;
         }
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
    public void logout(){
        session = null;
        navigate(0,null);
    } 

}
