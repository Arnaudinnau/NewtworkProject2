import java.io.*;
import java.net.*;

public class WordleConnection extends Thread {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private PrintWriter writer;
    private BufferedReader reader;
    private WordleGameState gameState;
    private CookiesStorage cookiesStorage;
    private String cookieWordle;

    public WordleConnection(Socket clientSocket, CookiesStorage cookiesStorage) {
        super();
        this.clientSocket = clientSocket;
        this.cookiesStorage = cookiesStorage;
        this.cookieWordle = null;
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
                Integer contentLength = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("HTTP")) {
                        if (!line.contains("HTTP/1.1")) {
                            writer.println("HTTP/1.1 505 HTTP Version Not Supported");
                            writer.println();
                            break;
                        } else if (line.startsWith("GET") || line.startsWith("POST")) {

                            while (!(header = reader.readLine()).isEmpty()) {
                                System.out.println(header);
                                if (header.startsWith("Cookie:")) {
                                    String cookieListReceived = header.replace("Cookie: ", "");
                                    if (cookieListReceived.contains("_SessionWordle=")) {
                                        String[] cookies = cookieListReceived.split("; ");
                                        for (String word : cookies) {
                                            if (word.startsWith("_SessionWordle=")) {
                                                try {
                                                    this.cookieWordle = word;
                                                    this.gameState = cookiesStorage.getState(cookieWordle);
                                                } catch (CookiesNotInListException e) {
                                                    this.cookieWordle = null;
                                                    this.gameState = null;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (header.startsWith("Content-Length: ")) {
                                    contentLength = Integer.parseInt(header.replace("Content-Length: ", ""));
                                }
                            }
                            if (line.startsWith("GET")) {
                                GETReply(line);
                            } else {
                                char[] buffer = new char[contentLength - 2];
                                reader.read(buffer, 0, contentLength - 2);
                                String input = new String(buffer);
                                POSTReply(input);
                            }

                        } else if (line.startsWith("PUT") || line.startsWith("HEAD") || line.startsWith("DELETE"))
                            writer.println("HTTP/1.1 405 Method Not Allowed");
                        else
                            writer.println("HTTP/1.1 400 Bad Request");
                        writer.println();
                    }
                    clientSocket.close();
                    return;
                }
            }

        } catch (Exception ex) {
            System.out.println("Connection Exception: " + ex.getMessage());
        }
    }

    private void handlePageRedirection(String path) {
        writer.println("HTTP/1.1 303 See Other");
        writer.println("Location: " + path);
        writer.println();
    }

    private void GETReply(String request) throws IOException {
        if (request.equals("GET / HTTP/1.1") || request.equals("GET HTTP/1.1")) {
            handlePageRedirection("/play.html");

        } else if (request.equals("GET /play.html HTTP/1.1")) {
            if (cookieWordle == null) {
                // Generate a WordleGameState and a cookie for this Game
                this.gameState = new WordleGameState();
                this.cookieWordle = cookiesStorage.createCookie(gameState);
            }
            replyHTML("/play.html");

        } else if (request.contains("?TRY=")) {
            request = request.replace("GET /play.html?TRY=", "TRY ").replace(" HTTP/1.1", "");
            dealWithQuery(request);
        } else if (request.contains("?CHEAT")) {
            request = request.replace("GET /play.html?CHEAT", "CHEAT").replace(" HTTP/1.1", "");
            dealWithQuery(request);
        } else {
            writer.println("HTTP/1.1 400 Bad Request");
            writer.println();
        }
    }

    private void POSTReply(String payload) throws IOException {
        if (payload.contains("TRY")) {
            payload = payload.replace("=", " ");
            String answer = gameState.answerToQuery(payload);
            if (answer.contains("GAMEOVER")) {
                gameState.NextStatus();
                gameState.NextStatus();
            }

            replyHTML("/play.html");
        }
    }

    private void replyHTML(String path) {
        if (path != "/play.html") {
            writer.println("HTTP/1.1 404 Not Found");
            writer.println();
        }
        byte[] data = gameState.getData(path);

        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/html");
        writer.println("Transfer-Encoding: chunked");
        if (gameState.GetStatus() == GameStatus.TOCLOSE) {
            cookiesStorage.removeSpecificCookie(cookieWordle);
            cookieWordle = null;
            writer.println("Set-Cookie: _SessionWordle=deleted; path =/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        } else
            writer.println("Set-Cookie: " + cookieWordle);
        writer.println("Connection: close");
        writer.println(); // Empty line to indicate the end of headers

        int chunkSize = 128; // Maximum ChunckSize
        for (int i = 0; i < data.length; i += chunkSize) {
            int chunkLength = Math.min(chunkSize, data.length - i);
            writer.println(Integer.toHexString(chunkLength)); // Chunk size in hexadecimal
            writer.println(new String(data, i, chunkLength));
        }
        writer.println("0");
        writer.println();

    }

    private void replyWord(String word) {
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + word.length());
        if (gameState.GetStatus() == GameStatus.TOCLOSE) {
            cookiesStorage.removeSpecificCookie(cookieWordle);
            cookieWordle = null;
            writer.println("Set-Cookie: _SessionWordle=deleted; path =/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        } else
            writer.println("Set-Cookie: " + cookieWordle);
        writer.println("Connection: close");
        writer.println(); // Empty line to indicate the end of headers
        writer.println(word);
    }

    private void dealWithQuery(String query) throws IOException {
        String answer = gameState.answerToQuery(query);
        if (gameState.GetStatus() == GameStatus.LASTQUERY) {
            gameState.NextStatus();
        }
        if (answer.contains("GAMEOVER") || answer.contains("QUIT")) {
            gameState.NextStatus();
        }
        replyWord(answer);
    }
}
