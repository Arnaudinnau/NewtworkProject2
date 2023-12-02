import java.io.*;
import java.net.*;

public class WordleServer {
    private ServerSocket serverSocket;
    private final static int port = 8020;
    private Integer n = 5;
    private Integer maxThread;
    private CookiesStorage cookiesStorage;

    public WordleServer() throws IOException {
        this.serverSocket = new ServerSocket(port);
        // this.n = 0;
        // this.maxThread = Integer.parseInt(arg);
        this.cookiesStorage = new CookiesStorage();
        System.out.println("The server is listening on port " + port);
    }

    public static void main(String[] args) {
        try {
            WordleServer wordleServer = new WordleServer();
            wordleServer.startServer();
        } catch (IOException ex) {
            System.out.println("Server Exception: " + ex.getMessage());
        }
    }

    private void startServer() {
        try {
            while (true) {
                // Il faut implémenter le thread pool
                Socket clientSocket = serverSocket.accept();
                Thread WordleConnection = new WordleConnection(clientSocket, cookiesStorage);
                WordleConnection.start();
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
