package augustopadilha.serverdistributedsystems.models;

public class Point {
    private String name;
    private String obs;
    private int id;

    public Point(String name, String obs, int id) {
        this.name = name;
        this.name = obs;
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
}
