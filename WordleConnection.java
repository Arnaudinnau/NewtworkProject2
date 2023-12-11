import java.io.*;
import java.net.*;

/**
 * This class is instantiated when a new connection is accepted on the server
 * Socket.
 * It responds to user queries, handles exceptions, and manages HTTP error
 * codes.
 * 
 * @author Arnaud Innaurato, Sophia Donato
 * @since 2023-12-10
 */

public class WordleConnection extends Thread {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private PrintWriter writer;
    private BufferedReader reader;
    private WordleGameState gameState;
    private CookiesStorage cookiesStorage;
    private String cookieWordle;
    private Integer threadIdentifier;

    /**
     * Constructor of the WordleConnection class
     * 
     * @param clientSocket     socket used to communicate to the client
     * @param cookiesStorage   list of correspondance between cookies and gameState
     * @param threadIdentifier thread number
     */
    public WordleConnection(Socket clientSocket, CookiesStorage cookiesStorage, Integer threadIdentifier) {
        super();
        this.clientSocket = clientSocket;
        this.cookiesStorage = cookiesStorage;
        this.cookieWordle = null;
        this.threadIdentifier = threadIdentifier;
        try {
            this.out = clientSocket.getOutputStream();
            this.in = clientSocket.getInputStream();
            this.writer = new PrintWriter(out, true);
            this.reader = new BufferedReader(new InputStreamReader(in));
        } catch (IOException ex) {
            System.out.println("Thread " + this.threadIdentifier + " exception:" + ex.getMessage());
        }
    }

    /**
     * Method that reads on the incoming request from the client.
     * It redirects to the right method to deal properly with specific requests and
     * manages the HTTP error codes.
     */
    public void run() {
        try {
            while (true) {
                String line, header;
                Integer contentLength = -1;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Thread " + this.threadIdentifier + ": " + line);
                    if (line.contains("HTTP")) {
                        if (!line.contains("HTTP/1.1")) {
                            writer.println("HTTP/1.1 505 HTTP Version Not Supported");
                            writer.println();
                            break;
                        } else if (line.startsWith("GET") || line.startsWith("POST")) {

                            while (!(header = reader.readLine()).isEmpty()) {
                                // Cookie in the request
                                if (header.startsWith("Cookie:")) {
                                    String cookieListReceived = header.replace("Cookie: ", "");
                                    if (cookieListReceived.contains("_SessionWordle=")) {
                                        String[] cookies = cookieListReceived.split("; ");
                                        for (String word : cookies) {
                                            if (word.startsWith("_SessionWordle=")) {
                                                try {
                                                    this.cookieWordle = word;
                                                    this.gameState = cookiesStorage.getState(cookieWordle);
                                                    System.out.println("Thread " + this.threadIdentifier + ": Cookie: "
                                                            + cookieWordle);
                                                } catch (CookiesNotInListException e) {
                                                    this.cookieWordle = null;
                                                    this.gameState = null;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                                // Content-Length of the payload
                                if (header.startsWith("Content-Length: "))
                                    contentLength = Integer.parseInt(header.replace("Content-Length: ", ""));

                            }

                            if (line.startsWith("GET"))
                                GETReply(line);
                            if (line.startsWith("POST")) {
                                if (!line.equals("POST /play.html HTTP/1.1")) {
                                    writer.println("404 Not Found HTTP/1.1");
                                    writer.println();
                                } else {
                                    if (contentLength == -1) {
                                        writer.println("HTTP/1.1 411 Length Required");
                                        writer.println();
                                    }
                                    // Reading the payload of the request
                                    char[] buffer = new char[contentLength - 2];
                                    try {
                                        reader.read(buffer, 0, contentLength - 2);
                                    } catch (NullPointerException ex) {
                                        writer.println("400 Bad Request HTTP/1.1");
                                        writer.println();
                                    }
                                    String input = new String(buffer);
                                    POSTReply(input);
                                }
                            }

                        } else if (line.startsWith("PUT") || line.startsWith("HEAD") || line.startsWith("DELETE"))
                            writer.println("HTTP/1.1 405 Method Not Allowed");
                        else
                            writer.println("HTTP/1.1 501 Not Implement");
                        writer.println();
                    }
                    clientSocket.close();
                    System.out.println("Thread " + this.threadIdentifier + " : closed");
                    return;
                }
            }
        } catch (Exception ex) {
            System.out.println("Thread " + this.threadIdentifier + " exception:" + ex.getMessage());
        }
    }

    /**
     * Handle the redirection to a specified path
     * 
     * @param path
     */
    private void handlePageRedirection(String path) {
        writer.println("HTTP/1.1 303 See Other");
        writer.println("Location: " + path);
        writer.println();
    }

    /**
     * Deal with GET requests
     * 
     * @param request
     * @throws IOException
     */
    private void GETReply(String request) throws IOException {
        if (request.equals("GET / HTTP/1.1") || request.equals("GET HTTP/1.1")) {
            handlePageRedirection("/play.html");

        } else if (request.equals("GET /play.html HTTP/1.1")) {
            if (cookieWordle == null) {
                // Generate a WordleGameState and a cookie for this Game
                this.gameState = new WordleGameState();
                this.cookieWordle = cookiesStorage.createCookie(gameState);
                System.out.println("Thread " + this.threadIdentifier + ": Cookie: " + cookieWordle);
            }
            replyHTML("/play.html");

        } else if (request.contains("play.html?TRY=") && cookieWordle != null) {
            request = request.replace("GET /play.html?TRY=", "TRY ").replace(" HTTP/1.1", "");
            replyWord(request);
        } else if (request.contains("play.html?CHEAT") && cookieWordle != null) {
            request = request.replace("GET /play.html?CHEAT", "CHEAT").replace(" HTTP/1.1", "");
            replyWord(request);
        } else {
            if (request.contains("/play.html"))
                writer.println("HTTP/1.1 400 Bad Request");
            else
                writer.println("HTTP/1.1 404 Not Found");
            writer.println();
        }
    }

    /**
     * Deal with POST requests
     * 
     * @param payload
     * @throws IOException
     */
    private void POSTReply(String payload) throws IOException {
        if (payload.contains("TRY=")) {
            payload = payload.replace("=", " ");
            String answer = gameState.answerToQuery(payload);
            if (answer.contains("GAMEOVER")) {
                gameState.NextStatus();
                gameState.NextStatus();
            }

            handlePageRedirection("/play.html");
        } else {
            writer.println("HTTP/1.1 400 Bad Request");
            writer.println();
        }
    }

    /**
     * Reply with the complete html file
     * 
     * @param path
     * @throws IOException
     */
    private void replyHTML(String path) throws IOException {
        if (path != "/play.html") {
            writer.println("HTTP/1.1 404 Not Found");
            writer.println();
        }
        byte[] data = gameState.getData(path);

        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        writer.println("Transfer-Encoding: chunked");
        if (gameState.GetStatus() == GameStatus.TOCLOSE) {
            System.out.println("Game with cookie " + cookieWordle + " is finished");
            cookiesStorage.removeSpecificCookie(cookieWordle);
            cookieWordle = null;
            writer.println("Set-Cookie: _SessionWordle=deleted; path =/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        } else
            writer.println("Set-Cookie: " + cookieWordle);
        writer.println("Connection: close");

        writer.println(); // Empty line to indicate the end of headers
        int chunkSize = 128; // Maximum ChunckSize
        for (int i = 0; i < data.length; i += chunkSize) {
            Integer chunkLength = Math.min(chunkSize, data.length - i);
            writer.println(Integer.toHexString(chunkLength));
            writer.println(new String(data, i, chunkLength));

        }
        writer.println("0");
        writer.println();
    }

    /**
     * Reply to GET method with TRY or CHEAT query
     * 
     * @param query
     */
    private void replyWord(String query) {
        String answer = gameState.answerToQuery(query);

        if (gameState.GetStatus() == GameStatus.LASTQUERY) {
            gameState.NextStatus();
        }
        if (answer.contains("GAMEOVER") || answer.contains("QUIT")) {
            gameState.NextStatus();
        }

        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + answer.length());
        if (gameState.GetStatus() == GameStatus.TOCLOSE) {
            System.out.println("Game with cookie " + cookieWordle + " is finished");
            cookiesStorage.removeSpecificCookie(cookieWordle);
            cookieWordle = null;
            writer.println("Set-Cookie: _SessionWordle=deleted; path =/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        } else
            writer.println("Set-Cookie: " + cookieWordle);
        writer.println("Connection: close");
        writer.println(); // Empty line to indicate the end of headers
        writer.println(answer);
    }
}