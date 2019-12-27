package middleware;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import beans.User;


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
    public boolean connectionClose(){
        try{
            conn.close();
        }catch(SQLException e){
            System.out.println(e);
            return false;
        }
        return true;
    }

    /* METHODS FOR USERS MANAGEMENT */
    public void createUser(Object[] args){
        String query = "INSERT INTO user(name, password, email, role) VALUES(?,?,?,?)";
        worker(query, args, 1);
    }

    public void deleteUser(Object[] args){
        String query = "DELETE FROM user WHERE id = ?";
        worker(query, args, 0);
    }

    public User getUserById(Object[] args){
        String query = "SELECT * FROM user WHERE id = ?";
        List<Object> result = worker(query, args, 0);
        User u = (User) result.get(0);
        return u;
    }
     public User getUserByEmail(Object[] args){
        String query = "SELECT * FROM user WHERE email = ?";
        List<Object> result = worker(query, args, 0);
        System.out.println(result);
        if(result.size()>0){
            User u = (User) result.get(0);
            return u;
        }else{
            return null;
        }
    }
    public List<Object> getUsers(){
        String query = "SELECT * FROM user";
        List<Object> users = worker(query, null, 1);
        return users;
    }

    public void updateUserField(String field, Object[] args){
        String query = "UPDATE user SET "+field+" = ? WHERE id = ?";
        worker(query,args,0);
    }
    public User autentication(Object[] args){
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        List<Object> result = worker(query,args, 0);
        if(result.size()>0){
            User u = (User) result.get(0);
            return u;
        }else{
            return null;
        }
    }
    /* Creates a List of the Object that needs to be returned as a result of the query */
    public List<Object> worker(String query, Object[] args, int type){
        try{
            if(args == null){
                Statement stmt = conn.createStatement();

                stmt.execute(query);
                ResultSet rs = stmt.getResultSet();

                List<Object> result = new ArrayList();
                User u;

                while(rs.next()){
                    u = new User(rs.getInt("id"),rs.getString("name"), rs.getString("password"),rs.getString("email"), rs.getInt("role"));
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
                if(type == 0){
                    ResultSet rs = ps.executeQuery();
                    List<Object> result = new ArrayList();
                    User u;

                    while(rs.next()){
                        u = new User(rs.getInt("id"),rs.getString("name"), rs.getString("password"),rs.getString("email"), rs.getInt("role"));
                        result.add(u);
                    }
                    return result;
                }else{
                    ps.executeUpdate();
                }
                return null;
            }
        }catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }
}
