package server_package;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.ArrayList;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;  

public class Connection extends Thread {

    private final Socket clientSocket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private static String[] clientUsernames = new String[100];
    private static ArrayList<String> serverFileList = new ArrayList<String>();
    private static ArrayList<String> clientFileList = new ArrayList<String>();
    private boolean isUserRegistered = false;
    private static final String clientFileDirectory = "clients_files";
    private String alias = "";

    public Connection(Socket s) throws IOException  {
        this.clientSocket = s;
        this.dis = new DataInputStream(this.clientSocket.getInputStream());
        this.dos = new DataOutputStream(this.clientSocket.getOutputStream());

        File directory = new File("ServerDirectory");
        File[] files = directory.listFiles();

        if(files != null){
            for (File file : files){
                serverFileList.add(file.getName());
            }
        }

        File clientDirectory = new File(clientFileDirectory);
        File[] clientFiles = clientDirectory.listFiles();

        if(clientFiles != null){
            for (File file : clientFiles){
                System.out.println(file.getName());
                clientFileList.add(file.getName());
            }
        }

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
                    String serverDirectory = "ServerDirectory";
                    String data = dis.readUTF(); // this reads command from client
                    String[] command = data.split(" ");
                    
                    
                    boolean isUserConnected = processComand(command, serverDirectory);
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

    private boolean processComand(String[] command, String serverDirectory) throws IOException{
       
        String username = null;
        if(command[0].compareTo("/leave") == 0){

            // Check if user is registered
            if(this.isUserRegistered){
                username = dis.readUTF();
            }
            // If invalid parameters
            if(command.length != 1){
                this.dos.writeUTF("Parameter mismatch");
            }else if(this.clientSocket != null && this.clientSocket.isConnected() == true){

                //finds the username and deletes it in the array
                if(this.isUserRegistered){
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

                    this.alias = "";
                }

                this.isUserRegistered = false;
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
                        this.isUserRegistered = true;
                        this.alias = username;
                        break;
                    }
                }
                this.dos.writeUTF("Welcome "+ username + "!");
            }
            this.dos.flush();
            
        }else if(command[0].compareTo("/dir") == 0){

            File directory = new File(serverDirectory);
            System.out.println("Absolute Path to ServerDirectory: " + directory.getAbsolutePath());
            File[] files = directory.listFiles();
            StringBuilder fileList = new StringBuilder();

            if(files != null){
                for (File file : files){
                    fileList.append(file.getName()).append("\n");
                }
            }

            String finalDirectory = "Server Directory \n" + fileList.toString() + "\n";
            this.dos.writeUTF(finalDirectory);
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
           
        }else if(command[0].compareTo("/store") == 0){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            if(!(clientFileList.contains(command[1]))){
                this.dos.writeUTF("file not found");
            }else{
                this.dos.writeUTF("file found"); // send response to client

                long fileSize = this.dis.readLong();

                FileOutputStream fos = new FileOutputStream(serverDirectory + "/" + command[1]);

                byte[] buffer = new byte[4 * 1024];
                int bytesReceived = 0;

                while(fileSize > 0 && (bytesReceived = this.dis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1){
                    fos.write(buffer, 0, bytesReceived);
                    fileSize -= bytesReceived;
                }


                // If stored file is new
                if(serverFileList.contains(command[1]) == false){
                    serverFileList.add(command[1]);
                    this.dos.writeUTF(this.alias + "<" + dtf.format(now) + "> Uploaded " + command[1]);
                    System.out.println(this.alias + "<" + dtf.format(now) + "> Uploaded " + command[1]);
                }else{
                    // Indicate overwrite
                    this.dos.writeUTF(this.alias + "<" + dtf.format(now) + "> Overwritten " + command[1]);
                    System.out.println(this.alias + "<" + dtf.format(now) + "> Overwritten " + command[1]);
                }

                fos.close();
            }
            
        }else if(command[0].compareTo("/get") == 0){

            if(!(serverFileList.contains(command[1]))){
                this.dos.writeUTF("file not found");
            }else{
                this.dos.writeUTF("file found");
                File file = new File(serverDirectory +"/" + command[1]);

                // Send file size
                this.dos.writeLong(file.length());

                byte[] buffer = new byte[1024 * 4];
                FileInputStream fis = new FileInputStream(serverDirectory +"/" + command[1]);
                int bytesRead = 0;
                
                while((bytesRead = fis.read(buffer)) != -1){
                    this.dos.write(buffer, 0, bytesRead);
                    this.dos.flush();
                }

                if(clientFileList.contains(command[1]) == false){
                    this.dos.writeUTF("File received from Server: " + command[1]);
                    clientFileList.add(command[1]); // add to client files list
                }else{
                    this.dos.writeUTF("Overwritten File received from Server: " + command[1]);
                }
                
                System.out.println(this.alias + " successfully received the following file: " + command[1]); 

                fis.close();

        }

    }
    return true;
}

}