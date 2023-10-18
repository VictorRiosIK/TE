import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class servidor {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Servidor <Puerto del servidor>");
            System.exit(1);
        }

        int serverPort = Integer.parseInt(args[0]);

        try {
            // Configurar el sistema de claves (debe tener un certificado y una clave
            // privada)
            System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

            // Crear un servidor SSL en el puerto 50000
            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
                    .getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort);
            System.out.println("Esperando conexiones...");
            System.out.println("Escuchando en el puerto: 50000");
            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                Thread clientThread = new Thread(new ClienteHandler(clientSocket));
                clientThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClienteHandler implements Runnable {
        private final SSLSocket socket;

        public ClienteHandler(SSLSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Abre los flujos de entrada y salida
                InputStream inFromClient = socket.getInputStream();
                DataInputStream dataIn = new DataInputStream(inFromClient);

                String request = dataIn.readUTF();
                System.out.println("Solicitud " + request + " recibida");
                if (request.equals("PUT")) {
                    String fileName = dataIn.readUTF();
                    long fileLength = dataIn.readLong();

                    byte[] buffer = new byte[(int) fileLength];
                    read(dataIn, buffer, 0, fileLength);
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                    fileOutputStream.write(buffer);
                    fileOutputStream.close();
                    System.out.println("Archivo '" + fileName + "' recibido con éxito.");
                } else if (request.equals("GET")) {

                    String fileName = dataIn.readUTF();
                    File file = new File(fileName);
                    boolean fileExists = file.exists() && file.isFile();
                    DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
                    dataOut.writeBoolean(fileExists);

                    if (fileExists) {
                        // Envía el tamaño del archivo
                        dataOut.writeLong(file.length());

                        // Envía el contenido del archivo al cliente
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            dataOut.write(buffer, 0, bytesRead);
                        }

                        fileInputStream.close();
                    }
                } else {
                    System.out.println("Solicitud no válida.");
                }

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    static void read(DataInputStream f, byte[] b, int posicion, long longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, (int) longitud);
            posicion += n;
            longitud -= n;
        }
    }
}