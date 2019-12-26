
package beans;

import org.neo4j.driver.v1.Record;

public class User {
    int id;
    String username;
    String email;
    String password;
    int role;

    public User(int id, String username, String email, String password, int role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public User(Record user){
        username = user.get("username").toString();
        email = user.get("email").toString();
        role = user.get("role").asInt();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

}
