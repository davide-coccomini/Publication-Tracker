package backend;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import middleware.User;


public class DatabaseManager {
    private final String db_Address = "//localhost:3306/";
    private final String db_User = "root";
    private final String db_Password = "";
    private final String db_Name = "publication-tracker";
    private Connection conn;
  
    private String makeStrString(){
        String str = "";
        str = "jdbc:mysql:" + db_Address + db_Name + "?user=" + db_User;
        return str;
    }
    
    /* Handles JDBC to open a connection with database */
    public boolean connectionStart() throws SQLException{
        String connStr = makeStrString();
        conn = DriverManager.getConnection(connStr);
        return true;  
    }
    /* Handles JDBC to close a connection */
    public boolean connectionClose() throws SQLException{
        conn.close();
        return true;
    }
    
    /* METHODS FOR USERS MANAGEMENT */
    public void createUser(Object[] args){
        String query = "INSERT INTO user(name, password, email, role) VALUES(?,?,?,?)";
        worker(query, args);
    }
    
    public void deleteUser(Object[] args){
        String query = "DELETE FROM user WHERE id = ?";
        worker(query, args);
    }
    
    public User getUser(Object[] args){
        String query = "SELECT * FROM user WHERE id = ?";
        List<Object> result = worker(query, args);
        User u = (User) result.get(0);
        return u;
    }
    
    public List<Object> getUsers(){
        String query = "SELECT * FROM user";
        List<Object> users = worker(query, null);
        return users;
    }
    
    public void updateUserField(String field, Object[] args){
        String query = "UPDATE user SET "+field+" = ?";
        worker(query,args);
    }
    public User autentication(Object[] args){    
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        List<Object> result = worker(query,args);
        User u = (User) result.get(0);
        return u;       
    }
    /* Creates an ArrayList of the Object that needs to be returned as a result of the query */
    public List<Object> worker(String query, Object[] args){
        try{
            if(args == null){
                Statement stmt = conn.createStatement();
                stmt.execute(query);                                    
                ResultSet rs = stmt.getResultSet();  

                List<Object> result = new ArrayList<>();
                User u;

                while(rs.next()){
                    u = new User(rs.getInt("idUser"),rs.getString("name"), rs.getString("password"),rs.getString("email"), rs.getInt("role"));
                    result.add(u);
                }

                rs.close();
                stmt.close();
                return result; 
            }else{
                PreparedStatement ps = conn.prepareStatement(query);
                for(int i=0; i<args.length; i++){
                    ps.setObject(i + 1,args[i]);
                }
                ps.executeUpdate();
                return null;
            }
        }catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }
}
