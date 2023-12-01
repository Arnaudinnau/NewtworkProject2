import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class WordleConnection {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private PrintWriter writer;
    private BufferedReader reader;
    private WordleGameState gameState;
    private CookiesStorage cookiesStorage;
    private String cookieWordle;

    private final static int WordLength = 5;
    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);

    public WordleConnection(Socket clientSocket, CookiesStorage cookiesStorage) {
        super();
        this.clientSocket = clientSocket;
        this.cookiesStorage = cookiesStorage;
        try {
            this.out = clientSocket.getOutputStream();
            this.in = clientSocket.getInputStream();
            this.writer = new PrintWriter(out, true);
            this.reader = new BufferedReader(new InputStreamReader(in));
        } catch (IOException ex) {
            System.out.println("Server exception:" + ex.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                String line, header;
                String cookie = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("HTTP")) {
                        if (!line.contains("HTTP/1.1")) {
                            writer.println("HTTP/1.1 505 HTTP Version Not Supported");
                            break;
                        } else if (line.startsWith("GET") || line.startsWith("POST")) {
                            while ((header = reader.readLine()) != "") {
                                System.out.println(header);
                                if (header.startsWith("Cookie:")) {
                                    cookie = header.replace("Cookie: ", "");
                                    break;
                                }
                            }
                            if (line.startsWith("GET"))
                                GETReply(line, cookie);
                            else
                                POSTReply(line, cookie);
                        } else if (line.startsWith("PUT") || line.startsWith("HEAD") || line.startsWith("DELETE"))
                            writer.println("HTTP/1.1 405 Method Not Allowed");
                        else
                            writer.println("HTTP/1.1 400 Bad Request");
                        writer.println();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void handlePageRedirection(String path) {
        writer.println("HTTP/1.1 303 See Other");
        writer.println("Location: " + path);
        writer.println();
    }

    private void GETReply(String request, String cookieListReceived) throws IOException {
        cookieWordle = null;
        if (cookieListReceived.contains("_SessionWordle=")) {
            String[] cookies = cookieListReceived.split("; ");
            for (String word : cookies) {
                if (word.startsWith("_SessionWordle=")) {
                    cookieWordle = word;
                    break;
                }
            }

        } else if (request.equals("GET / HTTP/1.1") || request.equals("GET HTTP/1.1")) {
            handlePageRedirection("/play.html");
            return;

        } else if (request.equals("GET /play.html HTTP/1.1")) {
            if (cookieWordle != null) {
                gameState = cookiesStorage.getState(cookieWordle);
                reply("/play.html");
            } else {
                // Generate a WordleGameState and a cookie for this Game
                gameState = new WordleGameState();
                cookieWordle = cookiesStorage.createCookie(gameState);
                reply("/play.html");
            }

        } else if (request.contains(".png") || request.contains(".css") || request.contains(".js")) {
            request.replace("GET ", "").replace(" HTTP/1.1", "");
            reply(request);

        } else if (request.contains("?TRY=")) {
            request.replace("GET /test.html?TRY=", "").replace(" HTTP/1.1", "");
            dealWithQuery(request);
        } else {
            writer.println("HTTP/1.1 400 Bad Request");
            writer.println();
        }

    }

    private void POSTReply(String request, String cookie) throws IOException {
    }

    private void reply(String path) {
    }

    private void dealWithQuery(String query) {
    }
}