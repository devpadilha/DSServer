package augustopadilha.serverdistributedsystems.models;

public class Point {
    private String name;
    private String obs;
    private int id;

    public Point(String name, String obs, int id) {
        this.name = name;
        this.obs = obs;
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public String getObs() {
        return this.obs;
    }
    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
