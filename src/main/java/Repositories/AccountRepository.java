package Repositories;

import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

public class AccountRepository {
    private String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private Connection databaseConnection;
    String SCHEMA = "bob";
    String SCHEMA_PASSWORD = "1234";

    public AccountRepository() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection databaseConnection = DriverManager.getConnection(ORACLE_DATABASE_URL, SCHEMA, SCHEMA_PASSWORD);
    }

    public void addUser(String username, String password) throws SQLException {
        String query = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        String salt = generateSalt();
        PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, salt);
        preparedStatement.executeUpdate();
    }

    public void getUser() throws SQLException {
        Statement statement = databaseConnection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM testdb WHERE name='dummy'");
        System.out.println(resultSet);
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

}
