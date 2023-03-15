package Repositories;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;

public class AccountRepository {
    private static final String HASH_ALGORITHM = "SHA-256";
    private String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private final Connection databaseConnection;
    String SCHEMA = "bob";
    String SCHEMA_PASSWORD = "1234";

    public AccountRepository(Connection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public AccountRepository() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            databaseConnection = createDatabaseConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load the Oracle JDBC driver.", e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to establish a connection to the database.", e);
        }
    }

    private Connection createDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(ORACLE_DATABASE_URL, SCHEMA, SCHEMA_PASSWORD);
    }


    public void addUser(String username, String userPassword) throws SQLException {
        int tableSize = getSizeOfTable();
        String salt = generateSalt();
        String hashedSaltedPassword = hashSaltAndPassword(userPassword, salt);
        String query = "INSERT INTO users (username, password, salt, userid) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, hashedSaltedPassword);
        preparedStatement.setString(3, salt);
        preparedStatement.setInt(4, tableSize + 1);
        preparedStatement.executeUpdate();
    }

    public boolean getUser(String username, String password) {
        if (authenticate(username, password)) {
            return true;
        } else {
            return false;
        }
    }

    public void getUsers() {
        try {
            Statement statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while(resultSet.next()) {
                int userid = resultSet.getInt("userid");
                String username = resultSet.getString("username");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    static String hashSaltAndPassword(String password, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedPassword = password + salt;
            byte[] hashedPassword = messageDigest.digest(saltedPassword.getBytes());
            return Hex.encodeHexString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private int getSizeOfTable() {
        try {
            Statement statement = databaseConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users");
            if (resultSet.next()) {
               return resultSet.getInt(1) + 1;
            } else {
                return 1;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private boolean authenticate(String username, String password) {
        try {
            PreparedStatement preparedStatement =
                    databaseConnection.prepareStatement("SELECT * FROM users WHERE username=?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            String hashedPasswordFromDatabase;
            String hashedPasswordFromUser;
            String salt;
            if (resultSet.next()) {
                hashedPasswordFromDatabase = resultSet.getString("password");
                salt = resultSet.getString("salt");
                hashedPasswordFromUser = hashSaltAndPassword(password, salt);
                if (hashedPasswordFromDatabase.equals(hashedPasswordFromUser)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
