package server_package;


import java.io.*;
import java.net.*;

public class Connection extends Thread {

    private final Socket clientSocket;
    private final DataInputStream dis;
    private final DataOutputStream dos;

    public Connection(Socket s) throws IOException  {
        this.clientSocket = s;
        this.dis = new DataInputStream(this.clientSocket.getInputStream());
        this.dos = new DataOutputStream(this.clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            String welcomeMsg = "Successfully connected!";
            dos.writeUTF(welcomeMsg);
            
            // This checks whether the string that was sent from
            // the client side is the terminal "END" else we
            // send the string back to the client
            while (true) {
                try {
                    String data = dis.readUTF(); // this reads command from client
                    System.out.println("User wants to execute " + data);
                } catch (IOException e) {
                    System.out.println("Client disconnected unexpectedly");
                    break;                
                }
            }

            //s.close();
        } catch (Exception e) {
            e.printStackTrace(); // Uncomment this if you want to look at the error thrown
        } finally {
            System.out.println("Server: Client " + clientSocket.getRemoteSocketAddress() + " has disconnected");
        }
    }

}