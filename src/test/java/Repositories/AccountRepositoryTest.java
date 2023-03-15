package Repositories;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.*;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AccountRepositoryTest {
    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_SALT = "testSalt";
    private static final String INCORRECT_HASHED_PASSWORD = "incorrectHashedPassword";

    private Connection databaseConnection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    private AccountRepository accountRepository;

    @Before
    public void setUp() throws SQLException {
        // Setup
        databaseConnection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);
        when(databaseConnection.createStatement()).thenReturn(statement);
        preparedStatement = Mockito.mock(PreparedStatement.class);
        resultSet = Mockito.mock(ResultSet.class);
        accountRepository = new AccountRepository(databaseConnection);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0);
    }

    @Test
    public void getUser_shouldFail_whenIncorrectPassword() throws SQLException {
        // Setup
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("password")).thenReturn(INCORRECT_HASHED_PASSWORD);
        when(resultSet.getString("salt")).thenReturn(TEST_SALT);

        // Trigger
        boolean isAuthenticated = accountRepository.getUser(TEST_USERNAME, TEST_PASSWORD);

        // Verify
        assertFalse(isAuthenticated);
    }

    @Test
    public void getUser_shouldSucceed_whenCredentialsAreCorrect() throws SQLException {
        // Setup - generate correct hashed password
        // database query for getUser
        String correctHashedPassword = AccountRepository.hashSaltAndPassword(TEST_PASSWORD, TEST_SALT);
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("username")).thenReturn(TEST_USERNAME);
        when(resultSet.getString("password")).thenReturn(correctHashedPassword);
        when(resultSet.getString("salt")).thenReturn(TEST_SALT);

        // Trigger
        boolean isAuthenticated = accountRepository.getUser(TEST_USERNAME, TEST_PASSWORD);

        // Verify
        Assert.assertTrue(isAuthenticated);
    }

    @Test
    public void getUser_shouldFail_whenUserNotFound() throws SQLException {
        // Setup
        when(databaseConnection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Trigger
        boolean isAuthenticated = accountRepository.getUser(TEST_USERNAME, TEST_PASSWORD);

        // Verify
        assertFalse(isAuthenticated);
    }
}


