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
    private CookiesStorage cookieStorage;

    private final static int WordLength = 5;
    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);

    public WordleConnection(Socket clientSocket) {
        super();
        this.clientSocket = clientSocket;
        try {
            this.out = clientSocket.getOutputStream();
            this.in = clientSocket.getInputStream();
            this.writer = new PrintWriter(out, true);
            this.reader = new BufferedReader(new InputStreamReader(in));
            this.cookieStorage = new CookiesStorage();
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

    private void GETReply(String request, String cookieReceived) throws IOException {
        if (request.equals("GET / HTTP/1.1") || request.equals("GET HTTP/1.1")) {
            handlePageRedirection("/play.html");
            return;
        }
        if (request.equals("GET /play.html")) {
            // Generate a WordleGameState and a cookie for this Game
            WordleGameState game = new WordleGameState();
            String cookie = cookieStorage.createCookie(game);

            // TO CONTINUE

        }

    }

    private void POSTReply(String request, String cookie) throws IOException {
    }

    private String wordleComputePattern(String guess) {
        char[] returned = { 'B', 'B', 'B', 'B', 'B' };
        char[] guessTab = guess.toCharArray();
        char[] answerTab = answer.toCharArray();
        int[] tab1 = { 1, 1, 1, 1, 1 };
        int[] tab2 = { 1, 1, 1, 1, 1 };
        for (int i = 0; i < WordLength; i++) {
            if (guessTab[i] == answerTab[i]) {
                tab1[i] = 0;
                tab2[i] = 0;
                returned[i] = 'G';
            }
        }
        for (int i = 0; i < WordLength; i++) {
            for (int j = 0; j < WordLength; j++) {
                if (guessTab[i] == answerTab[j] && i != j && tab1[j] == 1 && tab2[i] == 1) {
                    returned[i] = 'Y';
                    tab1[j] = 0;
                    tab2[i] = 0;
                    break;
                }
            }
        }
        return new String(returned);
    }

}
