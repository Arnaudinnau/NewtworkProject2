
import java.io.*;
import java.net.*;

public class JavaSimpleHTTP {
    private ServerSocket serverSocket;
    private final static int port = 2234;
    private Integer n = 5;
    private Integer maxThread;

    public JavaSimpleHTTP() throws IOException {
        this.serverSocket = new ServerSocket(port);
        // this.n = 0;
        // this.maxThread = Integer.parseInt(arg);
        System.out.println("The server is listening on port " + port);
    }

    public static void main(String[] args) {
        try {
            JavaSimpleHTTP wordleServer = new JavaSimpleHTTP();
            wordleServer.startServer();
        } catch (IOException ex) {
            System.out.println("Server Exception: " + ex.getMessage());
        }
    }

    private void startServer() {
        try {
            while (true) {
                /**
                if (maxThread == 0) {
                    wait();
                }
                synchronized (maxThread) {
                    maxThread--;
                    notifyAll();
                }
                */
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client connected (" + n.toString() + ") : " + clientSocket.getInetAddress());
                Thread SimpleHTTPServer = new SimpleHTTPServer(clientSocket);
                SimpleHTTPServer.start();
            }
        } catch (Exception ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }
}
