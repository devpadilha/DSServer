package augustopadilha.serverdistributedsystems.controllers.responses;

import java.io.PrintWriter;

public class ResponseController {
    public static void sendResponse(PrintWriter outToClient, String jsonRequest) {
        System.out.println("Enviando JSON para o cliente...");
        System.out.println("JSON enviado: " + jsonRequest + "\n");
        outToClient.println(jsonRequest);
        outToClient.flush();
    }
}
