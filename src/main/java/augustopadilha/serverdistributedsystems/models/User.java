package augustopadilha.serverdistributedsystems.models;

public class User {
    private String name;
    private String email;
    private String password;
    private String type;
    private int id;

    public User(String name, String email, String password, String type, int id) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.id = id;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }
    public String getType() {
        return type;
    }
    public Object getName() { return name; }
    public String getPassword() {
        return password;
    }
    public int getId() {
        return id;
    }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}