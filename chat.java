import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class WordServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            System.out.println("Server listening on port 8080...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream output = clientSocket.getOutputStream();

                // Read the request data sent by the client
                StringBuilder requestData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    requestData.append(line).append("\r\n");
                }

                // Extract the word from the query parameters
                String receivedWord = extractWordFromQueryParams(requestData.toString());

                // Customize the response based on the received word
                String responseWord = "Hello, you sent: " + receivedWord;

                // Send the response back to the client
                String response = "HTTP/1.1 200 OK\r\n\r\n" + responseWord;
                output.write(response.getBytes());

                // Close the streams and the socket
                reader.close();
                output.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractWordFromQueryParams(String requestData) {
        String[] queryParams = requestData.split("\\s+");
        String word = "";
        for (String param : queryParams) {
            if (param.startsWith("word=")) {
                word = param.substring(5);
                break;
            }
        }
        return URLDecoder.decode(word);
    }
}