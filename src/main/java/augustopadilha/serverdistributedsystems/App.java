package augustopadilha.serverdistributedsystems;

import augustopadilha.serverdistributedsystems.controllers.responses.*;
import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.controllers.system.JWTController;
import augustopadilha.serverdistributedsystems.controllers.system.SessionController;
import augustopadilha.serverdistributedsystems.controllers.system.UserCredentialsController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import augustopadilha.serverdistributedsystems.models.UserModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class App {
    static DatabaseController databaseController = new DatabaseController();

    static {
        databaseController.registerUser("administrador", "admin@admin.com", "0192023a7bbd73250516f069df18b500", "admin"); //senha: admin123
        databaseController.registerUser("comum", "comum@comum.com", "d2bd5f6696f1fa6aba4d72b49c161974", "user"); // senha: comum123
    }

    public static void main(String[] args) throws NumberFormatException, java.io.IOException {
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Iniciando servidor...");
        System.out.println("IP do servidor: " + InetAddress.getLocalHost().getHostAddress());
        System.out.print("Digite o número da porta: ");
        int serverPort = Integer.parseInt(stdIn.readLine());

        System.out.println("Servidor iniciado na porta " + serverPort + ".");


        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Servidor esperando por conexões...\n");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());

                // Tratar a solicitação do cliente em uma thread separada
                new Thread(() -> {
                    try {
                        handleClientRequest(socket);
                        socket.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket socket) throws java.io.IOException {
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ler o JSON enviado pelo cliente
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = inFromClient.readLine()) != null) {
                jsonBuilder.append(line);

                if (line.endsWith("}")) {
                    String receivedJson = jsonBuilder.toString();
                    // Imprimir o JSON no console
                    System.out.println("JSON recebido:" + receivedJson);

                    // Processar o JSON e identificar a ação
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(receivedJson);
                    String action = jsonNode.get("action").asText();

                    // Obtenha os dados do usuário do JSON recebido
                    JsonNode userData = jsonNode.get("data");
                    String name = null;
                    String email = null;
                    String password = null;
                    String type = null;
                    String token = null;
                    int id = -1;
                    int user_id = -1;

                    if(userData.get("name") != null)
                        name = userData.get("name").asText();

                    if(userData.get("email") != null)
                        email = userData.get("email").asText();

                    if(userData.get("password") != null)
                        password = userData.get("password").asText();

                    if(userData.get("type") != null)
                        type = userData.get("type").asText();

                    if(userData.get("token") != null)
                        token = userData.get("token").asText();

                    if(userData.get("id") != null)
                        id = userData.get("id").asInt();

                    if(userData.get("user_id") != null)
                        user_id = userData.get("user_id").asInt();

                    boolean error = false;
                    String message = "";

                    UserModel user = databaseController.getUserByEmail(email);

                    switch (action) {
                        case "cadastro-usuario":
                            // Checar se existe algum usuário com o mesmo email
                            if (databaseController.userExistsWithEmail(email)) {
                                error = true;
                                message = "Erro: Já existe um usuário com o email fornecido.";
                            } else {
                                // Verificar se o token não representa um administrador quando o papel é "admin"
                                if (type.equals("admin") && !JWTController.isTokenAdmin(token)) {
                                    error = true;
                                    message = "Erro: O token não possui privilégios de administrador.";
                                } else {
                                    error = false;
                                    message = "Usuário cadastrado com sucesso!";
                                    // Registre um novo usuário com os dados recebidos
                                    databaseController.registerUser(name, email, password, type);
                                }
                            }
                            // Enviar a resposta de volta ao cliente
                            RegisterResponseController.send(action, error, message, socket);
                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "autocadastro-usuario":
                            // Checar se existe algum usuário com o mesmo email
                            if (databaseController.userExistsWithEmail(email)) {
                                error = true;
                                message = "Erro: Já existe um usuário com o email fornecido.";
                            } else {
                                error = false;
                                message = "Usuário cadastrado com sucesso!";
                                // Registre um novo usuário com os dados recebidos
                                databaseController.registerUser(name, email, password, type);
                            }

                            // Enviar a resposta de volta ao cliente
                            RegisterResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "login":
                            if (token == null || token.isEmpty()) {
                                // Obter o usuário que está tentando fazer login
                                if (user != null) System.out.println("Usuário encontrado.");
                                // Verificar se as credenciais são válidas
                                if (user != null && UserCredentialsController.validate(email, password)) {
                                    // Login bem-sucedido
                                    error = false;
                                    message = "logado com sucesso";

                                    boolean isAdm = false;
                                    if (user.getType().equals("admin")) isAdm = true;

                                    // Criar o token
                                    token = JWTController.generateToken(1, isAdm);

                                    // Criar a sessão
                                    SessionController.createSession(token, user);
                                } else {
                                    // Login falhou
                                    error = true;
                                    message = "falha no login";
                                }
                            } else {
                                // O usuário já está logado
                                error = true;
                                message = "usuário já logado";
                            }
                            // Criar o JSON de resposta
                            LoginResponseController.send(action, error, message, socket, token);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);

                            break;


                        case "logout":
                            // Valide o token JWT usando a função isValidToken de JWTManager

                            SessionController.removeSession(token);

                            error = false;
                            message = "Logout efetuado com sucesso!";

                            LogoutResponseController.send(action, error, message, socket);

                            jsonBuilder.setLength(0);
                            break;

                        case "listar-usuarios":
                            // Valide o token JWT usando a função isValidToken de JWTManager
                            if (JWTController.isTokenAdmin(token)) {
                                // Obter todos os usuários do banco de dados
                                List<UserModel> userModels = databaseController.getAllUsers();

                                error = false;
                                message = "Sucesso";

                                // Criar o JSON de resposta
                                ListUsersResponseController.send(action, error, message, userModels, socket);
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";

                                // Criar o JSON de resposta
                                ListUsersResponseController.send(action, error, message, null, socket);
                            }

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "pedido-proprio-usuario":
                            if (JWTController.isValid(token)) {
                                error = false;
                                message = "Sucesso";
                                // Criar o JSON de resposta
                                ListDataResponseController.send(action, error, message, token, socket);
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";

                                // Criar o JSON de resposta
                                ListDataResponseController.send(action, error, message,null, socket);
                            }

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "autoedicao-usuario":
                            if(JWTController.isValid(token)) {
                                user = null;
                                user = databaseController.getUserById(id);

                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Usuário atualizado com sucesso!";
                                    databaseController.editUser(name, email, password, id);
                                }
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";
                            }
                            EditResponseController.send(action, error, message, null, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "excluir-proprio-usuario":
                            if(JWTController.isValid(token)) {
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Usuário removido com sucesso!";
                                    databaseController.deleteUser(email);
                                }
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";
                            }
                            DeleteResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "pedido-edicao-usuario":
                            if(JWTController.isTokenAdmin(token)) {
                                user = null;
                                user = databaseController.getUserById(id);
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Sucesso";
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }

                            EditResponseController.send(action, error, message, user, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "edicao-usuario":
                            if(JWTController.isTokenAdmin(token)) {
                                user = null;
                                user = databaseController.getUserById(id);
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Sucesso";
                                    databaseController.editUser(name, email, password, id);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }

                            EditResponseController.send(action, error, message, null, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "excluir-usuario":
                            if(JWTController.isTokenAdmin(token)) {
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Usuário removido com sucesso!";
                                    databaseController.deleteUser(email);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }
                            DeleteResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        default:
                            System.out.println("Opção inválida");
                            break;
                    }
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally { socket.close(); }
    }
}