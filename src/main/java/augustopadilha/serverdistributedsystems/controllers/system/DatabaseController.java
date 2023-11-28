package augustopadilha.serverdistributedsystems.controllers.system;

import augustopadilha.serverdistributedsystems.models.Point;
import augustopadilha.serverdistributedsystems.models.Segment;
import augustopadilha.serverdistributedsystems.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    private Connection connection;

    public DatabaseController() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*----------------------------------------------------------- USER -----------------------------------------------------------*/
    public boolean userExistsWithEmail(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Se count for maior que zero, o usuário existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Em caso de erro ou se o usuário não existir
    }

    public void registerUser(String name, String email, String password, String type) {
        // Verifique se um usuário com o mesmo nome ou e-mail já existe no banco de dados.
        if (userExistsWithEmail(email)) {
            return;
        }

        String insertUserQuery = "INSERT INTO users (name, email, password, type) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertUserQuery);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, type);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");

                return new User(name, email, password, type, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null se nenhum usuário com o email fornecido for encontrado
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String type = resultSet.getString("type");

                return new User(name, email, password, type, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null se nenhum usuário com o email fornecido for encontrado
    }

    public User getUserByToken(String token) {
        return getUserById(JWTController.getId(token));
    }

    public int getIdByEmail(String token) {
        String query = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retorna -1 se nenhum usuário com o token fornecido for encontrado
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {

            // Execute a consulta SQL para obter todos os usuários
            String query = "SELECT * FROM users";
            ResultSet resultSet = statement.executeQuery(query);

            // Processar os resultados e criar objetos de usuário
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");

                User user = new User(name, email, password, type, id);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public void editUser(User user) {
        if (user != null) {
            StringBuilder queryBuilder = new StringBuilder("UPDATE users SET");
            List<Object> parameters = new ArrayList<>();

            if (user.getName() != null) {
                queryBuilder.append(" name = ?,");
                parameters.add(user.getName());
            }
            if (user.getEmail() != null) {
                queryBuilder.append(" email = ?,");
                parameters.add(user.getEmail());
            }
            if (user.getPassword() != null) {
                queryBuilder.append(" password = ?,");
                parameters.add(user.getPassword());
            }

            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(" WHERE id = ?");
            parameters.add(user.getId());

            String query = queryBuilder.toString();
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                for (int i = 0; i < parameters.size(); i++) {
                    preparedStatement.setObject(i + 1, parameters.get(i));
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public void deleteUser(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            String query = "DELETE FROM users WHERE email = ?";
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*---------------------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------- POINT ----------------------------------------------------------*/
    public Point getPointById(int id) {
        String query = "SELECT * FROM points WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String obs = resultSet.getString("obs");

                return new Point(name, obs, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null se nenhum usuário com o email fornecido for encontrado
    }

    public void registerPoint(String name, String obs) {
        String insertPointQuery = "INSERT INTO points (name, obs) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertPointQuery);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, obs);

            int rowsInserted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Point> getAllPoints() {
        List<Point> points = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {

            // Execute a consulta SQL para obter todos os usuários
            String query = "SELECT * FROM points";
            ResultSet resultSet = statement.executeQuery(query);

            // Processar os resultados e criar objetos de usuário
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String obs = resultSet.getString("obs");
                int id = resultSet.getInt("id");

                Point point = new Point(name, obs, id);
                points.add(point);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return points;
    }

    public void editPoint(Point point) {
        if (point != null) {
            StringBuilder queryBuilder = new StringBuilder("UPDATE points SET");
            List<Object> parameters = new ArrayList<>();

            if (point.getName() != null) {
                queryBuilder.append(" name = ?,");
                parameters.add(point.getName());
            }
            if (point.getObs() != null) {
                queryBuilder.append(" obs = ?,");
                parameters.add(point.getObs());
            }

            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(" WHERE id = ?");
            parameters.add(point.getId());

            String query = queryBuilder.toString();
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                for (int i = 0; i < parameters.size(); i++) {
                    preparedStatement.setObject(i + 1, parameters.get(i));
                }
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deletePoint(int id) {
        Point point = getPointById(id);
        if (point != null) {
            String query = "DELETE FROM points WHERE id = ?";
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*---------------------------------------------------------------------------------------------------------------------------*/

    /*--------------------------------------------------------- SEGMENT ---------------------------------------------------------*/
    public void registerSegment(String direction, String distance, String obs, int originPointId, int destinyPointId) {
        String insertSegmentQuery = "INSERT INTO segments (direction, distance, obs, origin_point_id, destiny_point_id) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertSegmentQuery);

            preparedStatement.setString(1, direction);
            preparedStatement.setString(2, distance);
            preparedStatement.setString(3, obs);
            preparedStatement.setInt(4, originPointId);
            preparedStatement.setInt(5, destinyPointId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editSegment(String direction, String distance, String obs, int originPointId, int destinyPointId) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE segments SET");
        List<Object> parameters = new ArrayList<>();

        if (direction != null) {
            queryBuilder.append(" direction = ?,");
            parameters.add(direction);
        }
        if (distance != null) {
            queryBuilder.append(" distance = ?,");
            parameters.add(distance);
        }
        if (obs != null) {
            queryBuilder.append(" obs = ?,");
            parameters.add(obs);
        }
        if (originPointId != -1) {
            queryBuilder.append(" origin_point_id = ?,");
            parameters.add(originPointId);
        }
        if (destinyPointId != -1) {
            queryBuilder.append(" destiny_point_id = ?,");
            parameters.add(destinyPointId);
        }

        queryBuilder.deleteCharAt(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE origin_point_id = ? AND destiny_point_id = ?");
        parameters.add(originPointId);
        parameters.add(destinyPointId);

        String query = queryBuilder.toString();
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Segment> getAllSegments() {
        List<Segment> segments = new ArrayList<>();

        try (Statement statement = this.connection.createStatement()) {

            // Execute a consulta SQL para obter todos os usuários
            String query = "SELECT * FROM segments";
            ResultSet resultSet = statement.executeQuery(query);

            // Processar os resultados e criar objetos de usuário
            while (resultSet.next()) {
                int originPointId = resultSet.getInt("origin_point_id");
                int destinyPointId = resultSet.getInt("destiny_point_id");
                String direction = resultSet.getString("direction");
                String distance = resultSet.getString("distance");
                String obs = resultSet.getString("obs");

                Segment segment = new Segment(direction, distance, obs, originPointId, destinyPointId);
                segments.add(segment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return segments;
    }

    public void deleteSegment(int id) {
        Segment segment = getSegmentById(id);
        if (segment != null) {
            String query = "DELETE FROM segments WHERE id = ?";
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Segment getSegmentById(int id) {
        String query = "SELECT * FROM segments WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String direction = resultSet.getString("direction");
                String distance = resultSet.getString("distance");
                String obs = resultSet.getString("obs");
                int originPointId = resultSet.getInt("origin_point_id");
                int destinyPointId = resultSet.getInt("destiny_point_id");

                return new Segment(direction, distance, obs, originPointId, destinyPointId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*---------------------------------------------------------------------------------------------------------------------------*/
}

