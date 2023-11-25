import java.io.*;
import java.net.*;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        int port = 1989;
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Server started on port: " + port);

        // while (true) {
        Socket clientSocket = serverSocket.accept();
        System.err.println("New client connected");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        String s;
        while ((s = in.readLine()) != null) {
            System.out.println(s);
            if (s.isEmpty()) {
                break;
            }
        }

        File fileToSend = new File("test.html");
        if (fileToSend.exists()) {
            FileInputStream fileInputStream = new FileInputStream(fileToSend);
            byte[] fileData = new byte[(int) fileToSend.length()];
            fileInputStream.read(fileData);
            fileInputStream.close();

            out.write("HTTP/1.1 303 See Other\r\n");
            out.write("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n");
            out.write("Server: Apache/0.8.4\r\n");
            out.write("Content-Type: text/html\r\n");
            out.write("Content-Length: " + fileToSend.length() + "\r\n");
            out.write("Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n");
            out.write("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
            out.write("Location: /test.html\r\n");
            out.write("\r\n");
            out.flush(); // Ensure headers are sent before file content

        } else {
            out.write("HTTP/1.1 404 Not Found\r\n");
            out.write("\r\n");
            out.write("404 Not Found\r\n");
        }
        while (true) {
            while ((s = in.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    break;
                }
            }
        }

        // }
    }
}
