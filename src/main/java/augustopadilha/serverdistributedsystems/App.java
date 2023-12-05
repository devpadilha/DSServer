package augustopadilha.serverdistributedsystems;

import augustopadilha.serverdistributedsystems.controllers.responses.common.DeleteResponseController;
import augustopadilha.serverdistributedsystems.controllers.responses.common.RegisterResponseController;
import augustopadilha.serverdistributedsystems.controllers.responses.points.ListPointsResponseController;
import augustopadilha.serverdistributedsystems.controllers.responses.segments.EditSegmentResponseController;
import augustopadilha.serverdistributedsystems.controllers.responses.segments.ListSegmentsResponseSender;
import augustopadilha.serverdistributedsystems.controllers.responses.users.*;
import augustopadilha.serverdistributedsystems.controllers.system.DatabaseController;
import augustopadilha.serverdistributedsystems.controllers.system.JWTController;
import augustopadilha.serverdistributedsystems.controllers.system.SessionController;
import augustopadilha.serverdistributedsystems.controllers.system.UserCredentialsController;
import augustopadilha.serverdistributedsystems.models.ConnectionModel;
import augustopadilha.serverdistributedsystems.models.Point;
import augustopadilha.serverdistributedsystems.models.Segment;
import augustopadilha.serverdistributedsystems.views.ViewFactory;
import augustopadilha.serverdistributedsystems.controllers.system.Connection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import augustopadilha.serverdistributedsystems.models.User;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class App extends Application {
    private static Connection connection = new Connection();

    @Override
    public void start(Stage stage) {
        openConnectWindow();
    }

    public static void openConnectWindow() {
        ViewFactory.getInstance().showConnectWindow();
    }

    public static ConnectionModel openConnection(String ip, String port) {
        try {
            return new ConnectionModel(ip, port);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    /*-------------------------------------------------------------------------------------------------------------------------------------------*/
    static DatabaseController databaseController = new DatabaseController();

    static {
        databaseController.registerUser("administrador", "admin@admin.com", "0192023a7bbd73250516f069df18b500", "admin"); //senha: admin123
        databaseController.registerUser("comum", "comum@comum.com", "d2bd5f6696f1fa6aba4d72b49c161974", "user"); // senha: comum123
    }

    public static void main(String[] args) throws NumberFormatException, java.io.IOException {
        launch();

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

                    // Obtenha os dados do JSON recebido
                    JsonNode data = jsonNode.get("data");
                    String name = null;
                    String email = null;
                    String password = null;
                    String type = null;
                    String token = null;
                    int id = -1;
                    String direction = null;
                    String distance = null;
                    String obs = null;
                    int originPointId = -1;
                    int destinyPointId = -1;

                    if(data.get("name") != null)
                        name = data.get("name").asText();

                    if(data.get("email") != null)
                        email = data.get("email").asText();

                    if(data.get("password") != null)
                        password = data.get("password").asText();

                    if(data.get("type") != null)
                        type = data.get("type").asText();

                    if(data.get("token") != null)
                        token = data.get("token").asText();

                    if(data.get("id") != null)
                        id = data.get("id").asInt();

                    if(data.get("user_id") != null)
                        id = data.get("user_id").asInt();

                    if(data.get("ponto_id") != null)
                        id = data.get("ponto_id").asInt();

                    if(data.get("segmento_id") != null)
                        id = data.get("segmento_id").asInt();

                    JsonNode originPointNode = null;
                    if(data.has("ponto_origem")) {
                        originPointNode = data.path("ponto_origem");
                        originPointId = originPointNode.path("id").asInt();
                    }

                    JsonNode destinyPointNode = null;
                    if(data.has("ponto_origem")) {
                        destinyPointNode = data.path("ponto_destino");
                        destinyPointId = originPointNode.path("id").asInt();
                    }

                    Segment segment = null;
                    JsonNode segmentNode = null;
                    if(data.has("segmento")) {
                        segmentNode = data.path("segmento");
                        segment = Segment.fromJsonString(segmentNode.toString());
                    }

                    boolean error = false;
                    String message = "";

                    User user = databaseController.getUserByEmail(email);
                    Point point = databaseController.getPointById(id);


                    switch (action) {
                        /*---------------------------------------------------------- USERS ----------------------------------------------------------*/
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
                                List<User> users = databaseController.getAllUsers();

                                error = false;
                                message = "Sucesso";

                                // Criar o JSON de resposta
                                ListUsersResponseController.send(action, error, message, users, socket);
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
                                if(databaseController.getUserByToken(token) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    User editedUser = databaseController.getUserByToken(token);
                                    if(name != editedUser.getName()) editedUser.setName(name);
                                    if(email != editedUser.getEmail()) editedUser.setEmail(email);
                                    if(password != editedUser.getPassword()) editedUser.setPassword(password);
                                    error = false;
                                    message = "Usuário atualizado com sucesso!";
                                    databaseController.editUser(editedUser);
                                }
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";
                            }
                            EditUserResponseController.send(action, error, message, null, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "excluir-proprio-usuario":
                            if(JWTController.isValid(token)) {
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    if(user.getEmail() != email) {
                                        error = true;
                                        message = "Erro: Email incorreto";
                                    }

                                    else if(!user.getPassword().equals(password)) {
                                        error = true;
                                        message = "Erro: Senha incorreta";
                                    }

                                    else {
                                        error = false;
                                        message = "Usuário removido com sucesso!";
                                        databaseController.deleteUser(email);
                                    }
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

                            EditUserResponseController.send(action, error, message, user, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "edicao-usuario":
                            if(JWTController.isTokenAdmin(token)) {
                                if(user == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Sucesso";
                                    databaseController.editUser(databaseController.getUserById(id));
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }

                            EditUserResponseController.send(action, error, message, null, socket);

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
                        /*---------------------------------------------------------------------------------------------------------------------------*/
                        /*---------------------------------------------------------- POINTS ----------------------------------------------------------*/
                        case "cadastro-ponto":
                            if(JWTController.isValid(token)) {
                                if(databaseController.getUserByToken(token) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    if(!JWTController.isTokenAdmin(token)) {
                                        error = true;
                                        message = "Erro: O token não possui privilégios de administrador.";
                                    } else {
                                        error = false;
                                        message = "Ponto cadastrado com sucesso!";
                                        databaseController.registerPoint(name, obs);
                                    }
                                }
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";
                            }
                            RegisterResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                            case "pedido-edicao-ponto":
                                if(JWTController.isTokenAdmin(token)) {
                                    segment = databaseController.getSegmentById(id);
                                    if(segment == null) {
                                        error = true;
                                        message = "Erro: Segmento não encontrado";
                                    } else {
                                        if(JWTController.isTokenAdmin(token)) {
                                            error = true;
                                            message = "Erro: O token não possui privilégios de administrador.";
                                        } else {
                                            error = false;
                                            message = "Sucesso";
                                        }
                                    }
                                } else {
                                    error = true;
                                    message = "Erro: O token não possui privilégios de administrador.";
                                }
                                EditSegmentResponseController.send(action, error, message, segment, socket);

                                // Limpe o StringBuilder para a próxima solicitação
                                jsonBuilder.setLength(0);
                                break;

                        case "listar-pontos":
                            // Valide o token JWT usando a função isValidToken de JWTManager
                            if (JWTController.isTokenAdmin(token)) {
                                // Obter todos os pontos do banco de dados
                                List<Point> points = databaseController.getAllPoints();
                                error = false;
                                message = "Sucesso";

                                // Criar o JSON de resposta
                                ListPointsResponseController.send(action, error, message, points, socket);
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";

                                // Criar o JSON de resposta
                                ListPointsResponseController.send(action, error, message, null, socket);
                            }

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "edicao-ponto":
                            if(JWTController.isTokenAdmin(token)) {
                                if(databaseController.getUserById(JWTController.getId(token)) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Ponto editado com sucesso!";
                                    databaseController.editPoint(databaseController.getPointById(id), name, obs);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }

                            EditUserResponseController.send(action, error, message, null, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                            case "excluir-ponto":
                            if(JWTController.isTokenAdmin(token)) {;
                                if(databaseController.getUserById(JWTController.getId(token)) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Ponto removido com sucesso!";
                                    databaseController.deletePoint(id);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }
                            DeleteResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;
                        /*---------------------------------------------------------------------------------------------------------------------------*/

                        /*-------------------------------------------------------- SEGMENTS --------------------------------------------------------*/
                        case "cadastro-segmento":
                            if(JWTController.isValid(token)) {
                                if(databaseController.getUserByToken(token) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    if(!JWTController.isTokenAdmin(token)) {
                                        error = true;
                                        message = "Erro: O token não possui privilégios de administrador.";
                                    } else {
                                        error = false;
                                        message = "Segmento cadastrado com sucesso!";
                                        databaseController.registerSegment(segment.getDirection(), segment.getDistance(), segment.getObs(), segment.getOriginPoint(), segment.getDestinyPoint());
                                    }
                                }
                            } else {
                                error = true;
                                message = "Erro: Usuário não logado";
                            }
                            RegisterResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                            case "pedido-edicao-segmento":
                                if(JWTController.isTokenAdmin(token)) {
                                    segment = databaseController.getSegmentById(id);
                                    if(databaseController.getUserById(JWTController.getId(token)) == null) {
                                        error = true;
                                        message = "Erro: Usuário não encontrado";
                                    } else {
                                        if (segment == null) {
                                            error = true;
                                            message = "Erro: Segmento não encontrado";
                                        } else {
                                            message = "Sucesso";
                                        }
                                    }
                                } else {
                                    error = true;
                                    message = "Erro: O token não possui privilégios de administrador.";
                                }

                                EditSegmentResponseController.send(action, error, message, segment, socket);

                                // Limpe o StringBuilder para a próxima solicitação
                                jsonBuilder.setLength(0);
                                break;

                        case "listar-segmentos":
                            // Valide o token JWT usando a função isValidToken de JWTManager
                            if (JWTController.isTokenAdmin(token)) {
                                // Obter todos os usuários do banco de dados
                                    List<Segment> segments = databaseController.getAllSegments();

                                error = false;
                                message = "Sucesso";

                                // Criar o JSON de resposta
                                ListSegmentsResponseSender.send(action, error, message, segments, socket);
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";

                                // Criar o JSON de resposta
                                ListSegmentsResponseSender.send(action, error, message, null, socket);
                            }

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "edicao-segmento":
                            if(JWTController.isTokenAdmin(token)) {
                                if(databaseController.getUserById(JWTController.getId(token)) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Sucesso";
                                    databaseController.editSegment(databaseController.getSegmentById(id), segment.getDirection(), segment.getDistance(), segment.getObs(), segment.getOriginPoint(), segment.getDestinyPoint(), id);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }

                            EditUserResponseController.send(action, error, message, null, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "excluir-segmento":
                            if(JWTController.isTokenAdmin(token)) {
                                if(databaseController.getUserById(JWTController.getId(token)) == null) {
                                    error = true;
                                    message = "Erro: Usuário não encontrado";
                                } else {
                                    error = false;
                                    message = "Segmento removido com sucesso!";
                                    databaseController.deleteSegment(id);
                                }
                            } else {
                                error = true;
                                message = "Erro: O token não possui privilégios de administrador.";
                            }
                            DeleteResponseController.send(action, error, message, socket);

                            // Limpe o StringBuilder para a próxima solicitação
                            jsonBuilder.setLength(0);
                            break;

                        case "pedido-rotas":
                            if(originPointId == -1 || destinyPointId == -1) {
                                error = true;
                                message = "Erro: Ponto de origem ou ponto de destino não encontrado";
                            } else {
                                error = false;
                                message = "Sucesso";
                            }

                        /*---------------------------------------------------------------------------------------------------------------------------*/
                        default:
                            System.out.println("Opção inválida");
                            break;
                    }
                }
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally { socket.close(); }
    }
}