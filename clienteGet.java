import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class clienteGet {
    public static void main(String[] args) {
       if (args.length != 3) {
            System.out.println("Uso: java ClienteGet <IP del servidor> <Puerto del servidor> <Nombre del archivo>");
            System.exit(1);
        }
        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String fileName = args[2];

        try {
            // Configurar el sistema de confianza para el servidor (debe tener un certificado)
            System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");

            // Crear un socket SSL
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
            

            // Abre los flujos de entrada y salida
            OutputStream outToServer = socket.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outToServer);

            // Enviar la solicitud GET al servidor
            dataOut.writeUTF("GET");
            dataOut.writeUTF(fileName);
            

            // Abre el flujo de entrada para recibir el archivo desde el servidor
            InputStream inFromServer = socket.getInputStream();
            DataInputStream dataIn = new DataInputStream(inFromServer);

            // Verifica si el servidor tiene el archivo
            boolean fileExists = dataIn.readBoolean();
            // Recibir el tamaño del archivo del servidor
            
            if (fileExists) {
                long fileSize = dataIn.readLong();
                byte[] buffer = new byte[(int)fileSize];
                read(dataIn,buffer,0,fileSize);

                FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                fileOutputStream.write(buffer);
                fileOutputStream.close();

                System.out.println("Archivo '" + fileName + "' creado con éxito.");

                System.out.println("El archivo existe, se llama: " + fileName + " tiene un peso de: " + fileSize + " bytes");
            } else {
                System.out.println("El archivo '" + fileName + "' no existe en el servidor.");
            }

            // Cierra los flujos y el socket
            inFromServer.close();
            dataOut.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void read(DataInputStream f,byte[] b,int posicion,long longitud) throws Exception
    {
        while (longitud > 0)
        {
            int n = f.read(b,posicion,(int)longitud);
            posicion += n;
            longitud -= n;
        }
    }
}
