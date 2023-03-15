import Repositories.AccountRepository;
import com.sun.net.httpserver.HttpServer;
import Controllers.AccountController;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.*;

public class DatabaseServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8089), 0);
        try {
            server.createContext("/", new AccountController(new AccountRepository()));
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
