import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * This class initializes the Wordle Server and
 * handles all the connections (threads) using a FixedThreadPool.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */

public class WordleServer {
    private ServerSocket serverSocket;
    private Integer maxThread;
    private CookiesStorage cookiesStorage;
    private Integer threadIdentifier;

    private final static int port = 8020;

    /**
     * Constructor of the WordleServer
     * 
     * @param maxThread Size of the ThreadPool
     * @throws IOException
     */
    public WordleServer(String maxThread) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.threadIdentifier = 0;
        this.maxThread = Integer.parseInt(maxThread);
        this.cookiesStorage = new CookiesStorage();
        System.out.println("The server is currently listening on port " + port);
    }

    /**
     * Main method of the server side
     * 
     * @param args Parameters given when lauching the program
     */
    public static void main(String[] args) {
        try {
            WordleServer wordleServer = new WordleServer(args[0]);
            wordleServer.startServer();
        } catch (IOException ex) {
            System.out.println("Server Exception: " + ex.getMessage());
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Serveur Exception: you must specific a size for the thread pool");
        }
    }

    /**
     * Accept the communication and create the thread to deal with by using a Thread
     * Pool
     */
    private void startServer() {
        try {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (this.threadIdentifier) {
                    threadIdentifier++;
                    Thread WordleConnection = new WordleConnection(clientSocket, cookiesStorage, threadIdentifier);
                    executor.execute(WordleConnection);
                }
                cookiesStorage.removeExpiredCookie();
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
