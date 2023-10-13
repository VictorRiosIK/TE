import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class SecureClientPut {
    // ... (código anterior)

    public static void main(String[] args) {
        // ... (código anterior)
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream));

            File file = new File("file-to-send.txt");
            if (file.exists()) {
                out.println("PUT " + file.getName());
                out.println(file.length());

                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileInputStream));

                String line;
                while ((line = fileReader.readLine()) != null) {
                    out.println(line);
                }

                fileReader.close();

                String response = in.readLine();
                if (response.equals("OK")) {
                    System.out.println("File sent successfully.");
                } else {
                    System.out.println("Error on the server side.");
                }
            } else {
                System.out.println("File not found.");
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
