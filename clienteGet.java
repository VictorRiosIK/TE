import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class SecureClientGet {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final String TRUSTSTORE_PATH = "client.truststore";
    private static final String TRUSTSTORE_PASSWORD = "password";

    public static void main(String[] args) {
        try {
            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream(TRUSTSTORE_PATH), TRUSTSTORE_PASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(truststore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) socketFactory.createSocket(SERVER_IP, SERVER_PORT);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream));

            // Specify the file you want to GET from the server
            String filename = "file-to-receive.txt";

            // Send GET request to the server
            out.println("GET " + filename);

            String response = in.readLine();
            if (response.equals("OK")) {
                // Read the content and write it to a local file
                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                PrintWriter fileWriter = new PrintWriter(fileOutputStream);

                String line;
                while ((line = in.readLine()) != null) {
                    fileWriter.println(line);
                }

                fileWriter.close();
                System.out.println("File received successfully.");
            } else {
                System.out.println("Error on the server side.");
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
