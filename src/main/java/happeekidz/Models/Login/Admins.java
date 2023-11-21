package happeekidz.Models.Login;

import java.sql.*;

public class Admins {
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    private String username;
    private String password;
    private String accessLevel;
    boolean Authenticated = false;

    public Admins() {
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAccessLevel() {
        return this.accessLevel;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccessLevel(String role) {
        this.accessLevel = role;
    }

    public boolean Authenticate(String username, String password) {
        this.username = username;
        this.password = password;

        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306",
                    "root",
                    "MkbcMySQL2023-");
            //create happeekidz database
            pst = con.prepareStatement("CREATE DATABASE IF NOT EXISTS happeekidz");
            pst.executeUpdate();
            //use happeekidz database
            pst = con.prepareStatement("USE happeekidz");
            pst.executeUpdate();
            //create users table
            pst = con.prepareStatement("CREATE TABLE IF NOT EXISTS happeekidz.users (ID_USERS INT NOT NULL AUTO_INCREMENT, USERS_USERNAME VARCHAR(255) NOT NULL, USERS_PASSWORD VARCHAR(255) NOT NULL, USERS_ACCESS_LEVEL VARCHAR(255) NOT NULL, PRIMARY KEY (ID_USERS))");
            pst.executeUpdate();
            pst = con.prepareStatement("INSERT INTO happeekidz.users (USERS_USERNAME, USERS_PASSWORD, USERS_ACCESS_LEVEL) VALUES ('admin', 'admin', 'admin')");
            pst.executeUpdate();
            pst = con.prepareStatement("SELECT * FROM HAPPEEKIDZ.USERS WHERE USERS_USERNAME = ? AND USERS_PASSWORD = ?");
            pst.setString(1, username);
            pst.setString(2, password);
            rs = pst.executeQuery();

            if (rs.next()) {
                this.accessLevel = rs.getString("USERS_ACCESS_LEVEL");
                this.Authenticated = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return this.Authenticated;
    }
}
