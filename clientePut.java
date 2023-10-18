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
            // Configurar el sistema de confianza para el servidor (debe tener un
            // certificado)
            System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");

            // Crear un socket SSL
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
            Thread servidorThread = new Thread(new ServidorHandler(socket, fileName, file.length()));
            servidorThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ServidorHandler extends Thread {
        private final SSLSocket socket;
        private final String fileName;
        private final Long fileSize;

        public ServidorHandler(SSLSocket socket, String fileName, Long fileSize) {
            this.socket = socket;
            this.fileName = fileName;
            this.fileSize = fileSize;
        }

        @Override
        public void run() {
            try {
                // Abre los flujos de entrada y salida
                OutputStream outToServer = socket.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(fileName);

                // Enviar la solicitud PUT al servidor
                DataOutputStream dataOut = new DataOutputStream(outToServer);
                dataOut.writeUTF("PUT");
                dataOut.writeUTF(fileName);
                dataOut.writeLong(fileSize);
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

            }
        }
    }
}
