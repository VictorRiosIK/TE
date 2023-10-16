import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class clientePut {
    // ... (código anterior)

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso: java Cliente <IP del servidor> <Puerto del servidor> <Nombre del archivo>");
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        try {
            File file = new File(fileName);
            if (!file.exists() || !file.isFile()) {
                System.out.println("Error: El archivo especificado no existe o no es un archivo válido.");
                System.exit(1);
            }
            System.out.println("Archivo leido con exito");
            Socket socket = new Socket(serverIP, serverPort);

            // Abre los flujos de entrada y salida
            OutputStream outToServer = socket.getOutputStream();
            FileInputStream fileInputStream = new FileInputStream(fileName);

            // Enviar la solicitud PUT al servidor
            DataOutputStream dataOut = new DataOutputStream(outToServer);
            dataOut.writeUTF("PUT");
            dataOut.writeUTF(fileName);
            dataOut.writeLong(file.length());
            // Enviar el contenido del archivo al servidor
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outToServer.write(buffer, 0, bytesRead);
            }

            // Cierre los flujos y el socket
            fileInputStream.close();
            outToServer.close();
            socket.close();

            System.out.println("Archivo enviado con éxito.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
