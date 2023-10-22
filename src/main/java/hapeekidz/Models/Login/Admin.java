package hapeekidz.Models.Login;

public class Admin {
    private String username;
    private String password;
    private String role;
    boolean Authenticated = false;

    public Admin() {
    }
    public Admin(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    public String getUsername() {
        return this.username;
    }
    public String getPassword() {
        return this.password;
    }
    public String getRole() {
        return this.role;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public boolean Authenticate(String username, String password) {
        if (username.equals(this.username) && password.equals(this.password)) {
            this.Authenticated = true;
        }
        return this.Authenticated;
    }
}
