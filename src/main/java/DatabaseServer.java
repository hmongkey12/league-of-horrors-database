import com.sun.net.httpserver.HttpServer;
import handlers.AccountController;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.*;

public class DatabaseServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8089), 0);
        System.out.println(server.getAddress());
        server.createContext("/", new AccountController());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }
}
