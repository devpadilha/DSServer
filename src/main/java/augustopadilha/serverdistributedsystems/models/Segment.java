package augustopadilha.serverdistributedsystems.models;

public class Segment {
    private String direcao;
    private String distancia;
    private String obs;
    private int ponto_origem;
    private int ponto_destino;
    private int id;

    public Segment(String direction, String distance, String obs, int originPointId, int destinyPointId) {
        this.direcao = direction;
        this.distancia = distance;
        this.obs = obs;
        this.ponto_origem = originPointId;
        this.ponto_destino = destinyPointId;
    }

    public int getOriginPoint() {
        return this.ponto_origem;
    }
    public int getDestinyPoint() {
        return this.ponto_destino;
    }
    public String getDirection() {
        return this.direcao;
    }
    public String getDistance() {
        return this.distancia;
    }
    public String getObs() {
        return this.obs;
    }
    public int getId() {
        return this.id;
    }
}