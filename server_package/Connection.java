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
            System.out.println("[CONNECTION]: IN");
            String welcomeMsg = "Successfully connected!";
            dos.writeUTF(welcomeMsg);
            
            // This checks whether the string that was sent from
            // the client side is the terminal "END" else we
            // send the string back to the client
            while (true) {
                try {
                    String data = dis.readUTF(); // this reads command from client
                    String[] command = data.split(" ");
                    this.dos.writeUTF("Server: User wants to execute " + data);
                    boolean isUserConnected = processComand(command);
                    if(!isUserConnected){
                        break;
                    }

                    
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

    private boolean processComand(String[] command) throws IOException{

        if(command[0].compareTo("/leave") == 0){
            // If invalid parameters
            if(command.length != 1){
                this.dos.writeUTF("Error: Command parameters do not match or is not allowed.");
            }else if(this.clientSocket != null && this.clientSocket.isConnected() == true){
                this.dos.writeUTF("Client connection successfully closed.");
                this.clientSocket.close(); 
                return false; 
            }
        }
        return true;
    }




}