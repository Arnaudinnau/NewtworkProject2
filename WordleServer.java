import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class WordleServer {
    private ServerSocket serverSocket;
    private final static int port = 8020;
    private Integer maxThread;
    private CookiesStorage cookiesStorage;

    public WordleServer(String maxThread) throws IOException {
        this.serverSocket = new ServerSocket(port);
        // this.n = 0;
        this.maxThread = Integer.parseInt(maxThread);
        this.cookiesStorage = new CookiesStorage();
        System.out.println("The server is listening on port " + port);
    }

    public static void main(String[] args) {
        try {
            WordleServer wordleServer = new WordleServer(args[0]);
            wordleServer.startServer();
        } catch (IOException ex) {
            System.out.println("Server Exception: " + ex.getMessage());
        }
    }

    private void startServer() {
        try {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
            while (true) {
                // Il faut implémenter le thread pool
                Socket clientSocket = serverSocket.accept();
                Thread WordleConnection = new WordleConnection(clientSocket, cookiesStorage);
                executor.execute(WordleConnection);
                cookiesStorage.removeExpiredCookie();
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
