package server_package;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class Connection extends Thread {

    private final Socket clientSocket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private static String[] clientUsernames = new String[100];

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
                    
                    this.dos.writeUTF("Server: User wants to execute " + command[0]);
                    
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
       
        String username = null;
        if(command[0].compareTo("/leave") == 0){
            username = dis.readUTF();
            // If invalid parameters
            if(command.length != 1){
                this.dos.writeUTF("Parameter mismatch");
            }else if(this.clientSocket != null && this.clientSocket.isConnected() == true){
                //finds the username and deletes it in the array
                for(int i = 0; i < clientUsernames.length; i++){
                    if(clientUsernames[i] != null && clientUsernames[i].equalsIgnoreCase(username)){
                        //shifts the remaining element to fill the gap
                        for (int j = i; j < clientUsernames.length - 1; j++) {
                            clientUsernames[j] = clientUsernames[j + 1];
                        }
                        //sets the last element to null
                        clientUsernames[clientUsernames.length - 1] = null;
                        break; //break the loop
                    }
                }
                this.dos.writeUTF("disconnect");
                this.clientSocket.close(); 
                return false; 
            }else{
                this.dos.writeUTF("User not connected.");
            }
            this.dos.flush();
        }else if(command[0].compareTo("/register") == 0){
            //variable for checking if username exist
            boolean username_exists = false;
            //gets the username from the command
            username = command[1];
            username = username.trim();
            //checks if the array already contains the username
            for(int i = 0; i < clientUsernames.length; i++){
                if(clientUsernames[i] != null && clientUsernames[i].equalsIgnoreCase(username)){
                    username_exists = true;
                    break;
                }
            }
            //if it already contains the username, error message is sent
            if(username_exists){
                this.dos.writeUTF("Error: Registration failed. Handle or alias already exists.");
            }
            //else user is registered
            else{
                for(int i = 0; i < clientUsernames.length; i++){
                    if(clientUsernames[i] == null){
                        clientUsernames[i] = username;
                        System.out.println("Username Saved: " + username);
                        break;
                    }
                }
                this.dos.writeUTF("Welcome "+ username + "!");
            }
            this.dos.flush();
            
        }
        else if(command[0].compareTo("/?") == 0){

            
            // If invalid parameters
            if(command.length != 1){
                this.dos.writeUTF("Parameter mismatch");
            }else{
                System.out.println("HERE");
                String helpMsg = """
                Connect to the server application: /join <server_ip_add> <port>
                Disconnect from the server application: /leave
                Register a unique handle or alias: /register <handle>
                Send file to server: /store <filename>
                Request directory file list from a server: /dir
                Fetch a file from a server: /get <filename>
                Request command help:""";

                dos.writeUTF(helpMsg);

            }            




                    
           
        }
        return true;
    }

}