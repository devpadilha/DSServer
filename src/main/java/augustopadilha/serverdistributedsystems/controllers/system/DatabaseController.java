package augustopadilha.serverdistributedsystems.controllers.system;

import augustopadilha.serverdistributedsystems.models.UserModel;

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

    public boolean registerUser(String name, String email, String password, String type) {
        // Verifique se um usuário com o mesmo nome ou e-mail já existe no banco de dados.
        if (userExistsWithEmail(email)) {
            return false;
        }

        String insertUserQuery = "INSERT INTO users (name, email, password, type) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement(insertUserQuery);

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setString(4, type);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // O registro falhou
        }
    }

    public UserModel getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                String type = resultSet.getString("type");
                int id = resultSet.getInt("id");

                return new UserModel(name, email, password, type, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null se nenhum usuário com o email fornecido for encontrado
    }

    public UserModel getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {

            preparedStatement.setString(1, String.valueOf(id));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String type = resultSet.getString("type");

                return new UserModel(name, email, password, type, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retorna null se nenhum usuário com o email fornecido for encontrado
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

    public List<UserModel> getAllUsers() {
        List<UserModel> userModels = new ArrayList<>();

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

                UserModel userModel = new UserModel(name, email, password, type, id);
                userModels.add(userModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userModels;
    }

    public void editUser(String name, String email, String password, int id) {
        UserModel user = getUserById(id);
        if (user != null) {
            // Monta a consulta SQL base
            StringBuilder queryBuilder = new StringBuilder("UPDATE users SET");

            // Lista para armazenar os parâmetros da consulta SQL
            List<Object> parameters = new ArrayList<>();

            // Verifica e adiciona os campos que o usuário deseja atualizar
            if (name != null) {
                queryBuilder.append(" name = ?,");
                parameters.add(name);
            }
            if (email != null) {
                queryBuilder.append(" email = ?,");
                parameters.add(email);
            }
            if (password != null) {
                queryBuilder.append(" password = ?,");
                parameters.add(password);
            }

            // Remove a vírgula extra no final da consulta SQL
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);

            // Adiciona a cláusula WHERE com id
            queryBuilder.append(" WHERE id = ?");
            parameters.add(id); // Adicione id

            // Execute a consulta SQL
            String query = queryBuilder.toString();
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                // Define os parâmetros na consulta SQL dinamicamente
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
        UserModel user = getUserByEmail(email);
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

}

