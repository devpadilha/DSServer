package augustopadilha.serverdistributedsystems.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Segment {
    private String direcao;
    private String distancia;
    private String obs;
    private int ponto_origem;
    private int ponto_destino;
    private int id;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Segment(String direction, String distance, String obs, int originPointId, int destinyPointId) {
        this.direcao = direction;
        this.distancia = distance;
        this.obs = obs;
        this.ponto_origem = originPointId;
        this.ponto_destino = destinyPointId;
    }

    public static Segment fromJsonString(String jsonString) throws JsonProcessingException {
        // Utilize a biblioteca Jackson para converter a string JSON em um objeto JsonNode
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Extrair os valores do JsonNode para criar um novo objeto Segment
        JsonNode originNode = jsonNode.path("segmento").path("ponto_origem");
        JsonNode destNode = jsonNode.path("segmento").path("ponto_destino");

        String direction = jsonNode.path("segmento").path("direcao").asText();
        String distance = jsonNode.path("segmento").path("distancia").asText();
        String obs = jsonNode.path("segmento").path("obs").asText();

        int originPointId = originNode.path("id").asInt();
        int destinyPointId = destNode.path("id").asInt();

        return new Segment(direction, distance, obs, originPointId, destinyPointId);
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