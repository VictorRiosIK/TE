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
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Servidor esperando conexiones en el puerto " + serverPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Abre los flujos de entrada y salida
                InputStream inFromClient = clientSocket.getInputStream();
                DataInputStream dataIn = new DataInputStream(inFromClient);

                String request = dataIn.readUTF();
                System.out.println("Solicitud "+request +" recibida");
                if (request.equals("PUT")) {
                    String fileName = dataIn.readUTF();
                    long fileLength = dataIn.readLong();

                    // Lee el contenido del archivo y lo guarda en el servidor
                    byte[] buffer = new byte[1024];
                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);

                    int bytesRead;
                    long totalBytesRead = 0;
                    while (totalBytesRead < fileLength) {
                        bytesRead = inFromClient.read(buffer);
                        fileOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                    }

                    fileOutputStream.close();
                    System.out.println("Archivo '" + fileName + "' recibido con éxito.");
                }else if (request.equals("GET")) {
                    
                    String fileName = dataIn.readUTF();
                    File file = new File(fileName);
                    boolean fileExists = file.exists() && file.isFile();
                    DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
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

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}