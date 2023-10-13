import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecureServer {
    // ... (código anterior)

    public static void main(String[] args) {
        // ... (código anterior)
        while (true) {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            System.out.println("Client connected.");

            Runnable worker = new RequestHandler(socket);
            executor.execute(worker);
        }
    }
}

class RequestHandler implements Runnable {
    private SSLSocket socket;

    public RequestHandler(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String request = in.readLine();
            String[] parts = request.split(" ");
            String method = parts[0];
            String filename = parts[1];

            if (method.equals("GET")) {
                sendFile(filename, out);
            } else if (method.equals("PUT")) {
                receiveFile(filename, in, out);
            } else {
                out.println("ERROR");
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendFile(String filename, PrintWriter out) throws IOException {
        try {
            FileInputStream fileInputStream = new FileInputStream(filename);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileInputStream));
            out.println("OK");

            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
            }

            fileReader.close();
        } catch (FileNotFoundException e) {
            out.println("ERROR");
        }
    }

    private void receiveFile(String filename, BufferedReader in, PrintWriter out) throws IOException {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            PrintWriter fileWriter = new PrintWriter(fileOutputStream);
            String response = in.readLine();

            if (response.equals("OK")) {
                String line;
                while ((line = in.readLine()) != null) {
                    fileWriter.println(line);
                }

                fileWriter.close();
                out.println("OK");
            } else {
                out.println("ERROR");
            }
        } catch (IOException e) {
            out.println("ERROR");
        }
    }
}