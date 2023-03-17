import Repositories.AccountRepository;
import com.sun.net.httpserver.HttpServer;
import Controllers.AccountController;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class DatabaseServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8089), 0);
        server.createContext("/", new AccountController(new AccountRepository()));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }
}
