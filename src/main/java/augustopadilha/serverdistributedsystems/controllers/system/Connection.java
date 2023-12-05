package augustopadilha.serverdistributedsystems.controllers.system;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connection {

    Socket socket = null;
    OutputStream out = null;
    InputStream in = null;

    public void connect(String ip, String port) throws Exception {
        try {
            socket = new Socket(ip, Integer.parseInt(port));
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (UnknownHostException e) {
            throw new Exception("Servidor não encontrado");

        } catch (Exception e) {
            throw new Exception("Erro ao conectar com o servidor");
        }
    }

    public String send(String message) {
        String response = null;
        try {
            PrintWriter writer = new PrintWriter(out, true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            // Send a message to the server
            System.out.println("Json enviado: " + message);
            writer.println(message);
            // Get the response from the server
            if((response = reader.readLine()) != null) {
                System.out.println("Json recebido: " + response + "\n");
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem para o servidor: " + e.getMessage());
        }
        return response;
    }
}