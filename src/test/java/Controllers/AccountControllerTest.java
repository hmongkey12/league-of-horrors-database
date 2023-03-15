package Controllers;

import Repositories.AccountRepository;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.sql.SQLException;

import static org.mockito.Mockito.*;


public class AccountControllerTest {
    private static final String BASE_URL = "http://localhost:8000";
    private static final String TEST_USERNAME = "test_user";
    private static final String TEST_PASSWORD = "test_password";
    private static final String VALID_USER = "valid_user";
    private static final String VALID_PASSWORD = "valid_password";
    private static final String INVALID_USER = "invalid_user";
    private static final String INVALID_PASSWORD = "invalid_password";
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String QUERY_SEPARATOR = "?";

    @Test
    public void handlePost_shouldAddUserAndReturnHttpOk_whenRequestBodyIsValid() throws IOException, SQLException {
        // Setup: create mocks and configure the test environment
        String requestBody = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", TEST_USERNAME, TEST_PASSWORD);
        InputStream inputStream = new ByteArrayInputStream(requestBody.getBytes());
        HttpExchange httpExchange = mock(HttpExchange.class);
        when(httpExchange.getRequestBody()).thenReturn(inputStream);
        when(httpExchange.getRequestMethod()).thenReturn(POST_METHOD);
        Headers headers = new Headers();
        when(httpExchange.getResponseHeaders()).thenReturn(headers);
        AccountRepository accountRepository = mock(AccountRepository.class);
        AccountController postHandler = new AccountController(accountRepository);

        // Trigger: execute the code
        postHandler.handle(httpExchange);

        // Verify: check the expected behavior
        verify(accountRepository, times(1)).addUser(TEST_USERNAME, TEST_PASSWORD);
        verify(httpExchange, times(1)).sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }

    @Test
    public void handleGet_shouldReturnHttpOk_whenCredentialsAreValid() throws IOException, SQLException {
        // Setup: create mocks and configure the test environment
        AccountRepository accountRepository = mock(AccountRepository.class);
        when(accountRepository.getUser(VALID_USER, VALID_PASSWORD)).thenReturn(true);

        AccountController getHandler = new AccountController(accountRepository);

        HttpExchange validExchange = createMockHttpExchange(GET_METHOD, "username=" + VALID_USER + "&password=" + VALID_PASSWORD);

        // Trigger: execute the code
        getHandler.handle(validExchange);

        // Verify: check the expected behavior
        verify(validExchange, times(1)).sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }

    @Test
    public void handleGet_shouldReturnHttpUnauthorized_whenCredentialsAreInvalid() throws IOException, SQLException {
        // Setup: create mocks and configure the test environment
        AccountRepository accountRepository = mock(AccountRepository.class);
        when(accountRepository.getUser(VALID_USER, VALID_PASSWORD)).thenReturn(true);

        AccountController getHandler = new AccountController(accountRepository);

        HttpExchange invalidExchange = createMockHttpExchange(GET_METHOD, "username=" + INVALID_USER + "&password=" + INVALID_PASSWORD);

        // Trigger: execute the code under test
        getHandler.handle(invalidExchange);

        // Verify: Check the expected behavior and interactions
        verify(invalidExchange, times(1)).sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);
    }



    /**
     * Creates a mock HttpExchange object with the request method and query, also
     * configures the mock object
     *
     * @param requestMethod Represents the request method ("GET", "POST") for the mock HttpExchange
     * @param query Represents the query string to be added to the request URI
     * @return A mock HttpExchange object configured with the given request method and query
     * @throws IOException If an I/O error occurs during the creation of the mock object
     */
    private HttpExchange createMockHttpExchange(String requestMethod, String query) throws IOException {
        HttpExchange httpExchange = mock(HttpExchange.class);
        when(httpExchange.getRequestMethod()).thenReturn(requestMethod);
        when(httpExchange.getRequestURI()).thenReturn(URI.create(BASE_URL + QUERY_SEPARATOR + query));

        Headers headers = new Headers();
        when(httpExchange.getResponseHeaders()).thenReturn(headers);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(httpExchange.getResponseBody()).thenReturn(outputStream);

        return httpExchange;
    }
}
