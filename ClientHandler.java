import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private OutputStream out;
    private InputStream in;
    private String answer;
    private int tries;
    private Integer number;

    private final static int WordLength = 5;
    private final static List<String> listWords = new ArrayList<>(WordleWordSet.WORD_SET);

    public ClientHandler(Socket clientSocket) {
        super();
        this.clientSocket = clientSocket;
        try {
            this.out = clientSocket.getOutputStream();
            this.in = clientSocket.getInputStream();
        } catch (IOException ex) {
            System.out.println("Server exception:" + ex.getMessage());
        }
        this.tries = 0;
        this.answer = listWords.get(new Random().nextInt(listWords.size()));
    }

    public void run() {
        PrintWriter writer = new PrintWriter(out, true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while (true) {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    // System.out.println(line);
                    // Deal with GET method
                    if (line.startsWith("GET"))
                        GETReply(line, writer);
                }
            } catch (Exception e) {
            }
        }
    }

    private void GETReply(String query, PrintWriter writer) throws IOException {
        if (query.equals("GET / HTTP/1.1") || query.equals("GET HTTP/1.1")) {
            handlePageRedirection(writer, "/test.html");
            return;
        }
        String queryTest = query;
        query = query.replace("GET /", "");
        System.out.println(queryTest);
        URL url = new URL("http://localhost:2234" + queryTest.replace("GET ", "").replace(" HTTP/1.1", ""));
        System.out.println(url.toString());
        System.out.println(url.getQuery());
        if (url.getQuery() == null) {
            Path filePath = Paths.get(query.replace(" HTTP/1.1", ""));
            if (!Files.exists(filePath)) {
                writer.println("HTTP/1.1 404 Not Found");
                writer.println();
                return;
            }

            String type = null;

            if (query.contains(".png")) {
                File file = new File(filePath.toString());
                BufferedImage image = ImageIO.read(file);
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);
                String s = Base64.getEncoder().encodeToString(os.toByteArray());
                String htmlImage = "<img src=\"data:image/png;base64," + s + "\"/>";
                System.out.println(htmlImage);
                writer.println("HTTP/1.1 200 OK");
                writer.println("Content-Type: " + "text/html");
                writer.println("Content-Length: " + htmlImage.length());
                writer.println();
                writer.println(htmlImage);
                return;
            }

            if (query.contains(".html"))
                type = "text/html";
            if (query.contains(".css"))
                type = "text/css";
            if (query.contains(".js"))
                type = "script/js";

            System.out.println(query);
            byte[] fileData = Files.readAllBytes(filePath);

            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: " + type);
            writer.println("Transfer-Encoding: chunked");
            writer.println(); // Empty line to indicate the end of headers

            int chunkSize = 128; // Maximum ChunckSize
            for (int i = 0; i < fileData.length; i += chunkSize) {
                int chunkLength = Math.min(chunkSize, fileData.length - i);
                writer.println(Integer.toHexString(chunkLength)); // Chunk size in hexadecimal
                writer.println(new String(fileData, i, chunkLength));
            }

            writer.println("0");
            writer.println(); // Empty line to signal the end of chunks
        } else {
            System.out.println("ok");
            String request = url.getQuery();
            System.out.println(request);
            String returned = manageRequest(request.replace("=", " "));
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: " + "text/plain");
            writer.println("Content-Length: " + returned.length());
            writer.println();

            System.out.println(returned);
            writer.println(returned);
        }
    }

    private void handlePageRedirection(PrintWriter writer, String path) {
        writer.println("HTTP/1.1 303 See Other");
        writer.println("Location: " + path);
        writer.println();
    }

    public String manageRequest(String query) throws IOException {
        System.out.println(query);
        String returned = "WRONG";
        if (query.equals("CHEAT")) {
            returned = answer.toUpperCase();
        } else if (query.startsWith("TRY")) {
            String guess = query.substring("TRY ".length());
            System.out.println(guess);
            if (guess.length() == WordLength) {
                // if (guess.matches("[A-Z]+") && guess.length() == WordLength) {
                if (!listWords.contains(guess.toLowerCase())) {
                    returned = "NONEXISTENT";
                } else {
                    tries++;
                    returned = wordleComputePattern(guess.toLowerCase());
                }
                if (returned.equals("GGGGG") || tries == 6)
                    returned = returned.concat(" GAMEOVER");
            }
        } else if (query.equals("QUIT")) {
            this.clientSocket.close();
            return null;
        }
        return returned;
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