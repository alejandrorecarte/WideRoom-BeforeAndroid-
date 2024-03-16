package models;

public class User {

    private String idToken;
    private String localId;
    private String email;
    private String username;

    public User(String idToken, String localId, String email, String username) {
        this.idToken = idToken;
        this.localId = localId;
        this.email = email;
        this.username = username;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", localId='" + localId + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}
