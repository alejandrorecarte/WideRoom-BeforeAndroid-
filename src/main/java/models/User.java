package models;

public class User {

    private String email;
    private String hashedPassword;
    private String username;

    public User(String email, String hashedPassword, String username) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
