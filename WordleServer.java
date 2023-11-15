import java.io.*;
import java.net.*;

public class WordleServer {
    private ServerSocket serverSocket;
    private final static int port = 2234;
    private Integer n;
    private Integer maxThread;

    public WordleServer(String arg) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.n = 0;
        this.maxThread = Integer.parseInt(arg);
        ;
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
            while (true) {
                if (maxThread == 0) {
                    wait();
                }
                synchronized (maxThread) {
                    maxThread--;
                    notifyAll();
                }
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected (" + n.toString() + ") : " + clientSocket.getInetAddress());
                Thread clientHandler = new ClientHandler(n++, clientSocket);
                clientHandler.start();
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
