package Controllers;

import Repositories.AccountRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * AccountController handles the HTTPRequests based on their type
 */
public class AccountController implements HttpHandler {

    private AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * method to handle incoming requests.
     * @param exchange the HttpExchange object containing request and response data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if ("GET".equals(requestMethod)) {
            try {
                handleGet(exchange);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if ("POST".equals(requestMethod)) {
            try {
                handlePost(exchange);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * handlePost handles the POST requests to add a user
     * @param exchange the HttpExchange object containing request and response data
     * @throws IOException if an I/O error occurs
     * @throws ParseException if an error occurs while parsing JSON data
     */
    private void handlePost(HttpExchange exchange) throws IOException, ParseException{
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        InputStream inputStream = exchange.getRequestBody();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byte[] requestBodyData = byteArrayOutputStream.toByteArray();
        JSONParser jsonParser = new JSONParser();
        JSONObject requestBodyJsonFormat = (JSONObject) jsonParser.parse(new String(requestBodyData));
        try {
            String username = (String) requestBodyJsonFormat.get("username");
            String password = (String) requestBodyJsonFormat.get("password");
            accountRepository.addUser(username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }


    /**
     * handleGet handles the GET requests to check user credentials
     * @param exchange the HttpExchange object containing request and response data
     * @throws IOException if an I/O error occurs
     * @throws SQLException if a database access error occurs
     * @throws ClassNotFoundException if the database driver class is not found
     */
    private void handleGet(HttpExchange exchange) throws IOException, SQLException, ClassNotFoundException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> queries = new HashMap<>();
        String userName = null;
        String password = null;
        int responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;
        String response = "";
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] entry = pair.split("=");
                if (entry.length > 1) {
                    if (entry[0].equals("username")) {
                        userName = entry[1];
                    } else if (entry[0].equals("password")) {
                        password = entry[1];
                    }
                }
            }
            if (accountRepository.getUser(userName, password)) {
                responseCode = HttpURLConnection.HTTP_OK;
            }
        }
        exchange.sendResponseHeaders(responseCode, 0);
        System.out.println(responseCode);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
