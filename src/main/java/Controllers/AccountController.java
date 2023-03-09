package Controllers;

import Repositories.AccountRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AccountController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if ("GET".equals(requestMethod)) {
            try {
                handleGet(exchange);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
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

    private void handlePost(HttpExchange exchange) throws IOException, ParseException{
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        InputStream inputStream = exchange.getRequestBody();
        byte [] requestBodyData = inputStream.readAllBytes();
        JSONParser jsonParser = new JSONParser();
        JSONObject requestBodyJsonFormat = (JSONObject) jsonParser.parse(new String(requestBodyData));
        try {
            AccountRepository accountRepository = new AccountRepository();
            accountRepository.addUser("jon", "1234");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("ended");
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
    }

    private void handleGet(HttpExchange exchange) throws IOException, SQLException, ClassNotFoundException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> queries = new HashMap<>();
        String userName = null;
        String password = null;
        AccountRepository accountRepository = new AccountRepository();
        accountRepository.getUser("jon", "1234");
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
        }
        String response = "Hello" + userName + " Your password is " + password;
        exchange.sendResponseHeaders(200, response.length());
        System.out.println(response);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.flush();
        outputStream.close();
    }


}
