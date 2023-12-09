package client_package;


import java.io.*;
import java.net.*;
import java.util.*; // For Scanner

public class Client {

    private String host;
    private int port;

    private Socket socket;
    private Socket msgSocket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private Boolean isUserConnected;
    private Boolean isUserRegistered;
    private String username = new String();


    private MessageThread messageThread;



    public Client(){
        this.isUserConnected = false;
        this.isUserRegistered = false;
        this.username = null;
    }


        

    // Getters

    public Boolean getUserConnectStatus(){
        return this.isUserConnected;
    }


    public Boolean getUserRegistrationStatus(){
        return this.isUserRegistered;
    }

    public String getUsername(){
        return this.username;
    }

    // Setters
    public void setUserConnectStatus(Boolean status){
        this.isUserConnected = status;
    }


    public void setUserRegistrationStatus(Boolean status){
        this.isUserRegistered = status;
    }

    public void setUsername(String username){
        this.username = username;
    }


    // Client Functions


    public void handleCommand(String command){

        if(command.length() > 0){
            String[] commandParts = command.split(" ");
            String commandType = commandParts[0];

            // If command is invalid
            if(commandType.compareTo("/quit") == 0){
                System.out.println("Error: Incorrect parameters. Use /leave instead");
            }
            else if(isCommandValid(commandType) == false){
                System.out.println("Error: Command not found.");
            }else if(commandType.compareTo("/?") == 0 && this.isUserConnected == false){
                if(commandParts.length == 1){
                    printCommands();
                }else{
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }
            }
            else if(commandType.compareTo("/join") != 0 && this.isUserConnected == false){
                // If client is not connected and attempts to use other commands besides /join
                System.out.println("Client: You must join a server before you can use the " + commandType + " command.");

        
            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == true){
                // If client is already connected to a server and attempts to join one.
                System.out.println("Error: Disconnection failed. Please connect to the server first.");

            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == false){
                // If client is not connected and attempts to join
                if(commandParts.length == 3){
                    this.host = commandParts[1];
                    this.port = Integer.parseInt(commandParts[2]);
                
                    joinServer(host, port);

                }else{
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }
            }else if(commandType.compareTo("/leave") == 0){
                try{
                    this.writer.writeUTF(command);

                    // if user is registered
                    if(this.isUserRegistered){
                        this.writer.writeUTF(getUsername());
                    }
                    System.out.println(this.reader.readUTF()); // for User wants to execute UTF

                    String status = this.reader.readUTF();

                    if(status.compareTo("disconnect") == 0){
                        System.out.println("Connection closed. Thank you!");
                        disconnect();
                    }else if(status.compareTo("Parameter mismatch") == 0){
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }

                }catch(IOException e){
                    // Force disconnect client
                    System.out.println("Server has shut down unexpectedly. Disconnecting...");
                    disconnect();
                    
                }
            }else if(commandType.compareTo("/?") == 0){
                try{
                    this.writer.writeUTF(command);
                    this.writer.flush();
                    System.out.println(this.reader.readUTF()); // for 'User wants to execute' UTF

                    String response = this.reader.readUTF();

                    if(response.compareTo("Parameter mismatch") == 0){
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }else{
                        System.out.println(response);
                    }


                }catch(IOException e){
                    // Force disconnect client
                    System.out.println("Server has shut down unexpectedly. Disconnecting...");
                    disconnect();
                }
            }else if(commandType.compareTo("/register") == 0){
                try{
                    //checks if the 
                    if(commandParts.length != 2){
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }else{
                        this.writer.writeUTF(command);
                        this.writer.flush();
                        System.out.println(this.reader.readUTF());
                        String response = this.reader.readUTF();
                        if(response.startsWith("Welcome ")){
                            this.isUserRegistered = true;
                            this.username = commandParts[1];
                            this.messageThread = new MessageThread(msgSocket);
                            this.messageThread.start();
                        }
                        System.out.println(response);
                    }
                
                }catch(IOException e){
                        // Force disconnect client
                        System.out.println("Server has shut down unexpectedly. Disconnecting...");
                        disconnect();
                    }
                
            }else if(commandType.compareTo("/store") == 0){
                try{
                    if(commandParts.length == 2){
                        if(this.isUserRegistered){
                            this.writer.writeUTF(command); 
                            System.out.println(this.reader.readUTF()); // For 'User wants to execute' UTF

                            String response = this.reader.readUTF();

                            if(response.compareTo("file not found") == 0){
                                System.out.println("Error: File not found.");
                            }else if(response.compareTo("file found") == 0){

                                File file = new File("clients_files/" + commandParts[1]);

                                // Send file size
                                this.writer.writeLong(file.length());


                                byte[] buffer = new byte[1024 * 4];
                                FileInputStream fis = new FileInputStream("clients_files/" + commandParts[1]);
                                int bytesRead = 0;
                                
                                while((bytesRead = fis.read(buffer)) != -1){
                                    this.writer.write(buffer, 0, bytesRead);
                                    this.writer.flush();
                                }
                                
                                System.out.println(this.reader.readUTF()); // Receive response

                                fis.close();


                
                            }
                        }else{
                            System.out.println("Error: You must be registered to use this command.");
                        }
                    }else{
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }

                }catch(IOException e){
                    // Force disconnect client
                    System.out.println("Server has shut down unexpectedly. Disconnecting...");
                    disconnect();
                }                
            }else if(commandType.compareTo("/dir") == 0){
                try{
                    if(this.isUserRegistered){
                        this.writer.writeUTF(command);
                        System.out.println(this.reader.readUTF());
                        System.out.println(this.reader.readUTF());
                    }else{
                        System.out.println("Error: You must be registered to use this command.");
                    }
                }catch(IOException e){
                    // Force disconnect client
                    System.out.println("Server has shut down unexpectedly. Disconnecting...");
                    disconnect();
                }
             
            }else if(commandType.compareTo("/get") == 0){
                if(commandParts.length == 2){
                    if(this.isUserRegistered){
                        try{
                            this.writer.writeUTF(command); 
                            System.out.println(this.reader.readUTF()); // For 'User wants to execute' UTF
                            
                            String response = this.reader.readUTF();
                            

                            if(response.compareTo("file not found") == 0){
                                System.out.println("Error: File not found.");
                            }else if(response.compareTo("file found") == 0){
                                
                                long fileSize = this.reader.readLong();

                                FileOutputStream fos = new FileOutputStream("clients_files" + "/" + commandParts[1]);
                
                                byte[] buffer = new byte[4 * 1024];
                                int bytesReceived = 0;

                
                                while(fileSize > 0 && (bytesReceived = this.reader.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1){
                                    fos.write(buffer, 0, bytesReceived);
                                    fileSize -= bytesReceived;
                                }
                        

                                System.out.println(this.reader.readUTF()); // Receive response
                
                                fos.close();
   
                            }

                        }catch(IOException e){
                            // Force disconnect client
                            System.out.println("Server has shut down unexpectedly. Disconnecting...");
                            disconnect();                            
                        }



                    }else{
                        System.out.println("Error: You must be registered to use this command.");
                    }
                }else{
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }
            }else if(commandType.compareTo("/broadcast") == 0){
                if(commandParts.length == 1){
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }else{
                    if(this.isUserRegistered){
                        try {
                            this.writer.writeUTF(command); 
                            System.out.println(this.reader.readUTF()); // For 'User wants to execute' UTF

                            StringBuilder broadcastMsg = new StringBuilder();
                            for(int i = 1; i < commandParts.length; i++){
                                broadcastMsg.append(commandParts[i] + " ");
                            }

                            this.writer.writeUTF(broadcastMsg.toString());

                            String response = this.reader.readUTF();

                            if(response.compareTo("broadcast success") == 0){
                                System.out.println("Your broadcast message was successfully sent!");
                            }else{
                                System.out.println("Failed to send broadcast message. An unexpected error has occurred.");
                            }

                        } catch (IOException e) {
                            // Force disconnect client
                            System.out.println("Server has shut down unexpectedly. Disconnecting...");
                            disconnect();
                        }
                    }else{
                        System.out.println("Error: You must be registered to use this command.");
                    }

                }


            }else if(commandType.compareTo("/message") == 0){
                if(commandParts.length > 2){
                    if(isUserRegistered){
                        try {
                            this.writer.writeUTF(command); 
                            System.out.println(this.reader.readUTF()); // For 'User wants to execute' UTF

                            this.writer.writeUTF(commandParts[1]); // Send username to server for checking
                            
                            StringBuilder directMsg = new StringBuilder();
                            for(int i = 2; i < commandParts.length; i++){
                                directMsg.append(commandParts[i] + " ");
                            }

                            String response = this.reader.readUTF();

                            if(response.compareTo("can't message own self") == 0){
                                System.out.println("Error: You can't directly message your own self.");
                            }else if(response.compareTo("user not found") == 0){
                                System.out.println("Error: User not found");
                            }else if(response.compareTo("user found") == 0){
                                this.writer.writeUTF(directMsg.toString());
                                System.out.println("Successfully sent message to " + commandParts[1]);
                            }

                            
                            
                        } catch (IOException e) {
                            // Force disconnect client
                            System.out.println("Server has shut down unexpectedly. Disconnecting...");
                            disconnect();
                        }
                    }else{
                        System.out.println("Error: You must be registered to use this command.");
                    }

                }else{
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }
            }else{
                System.out.println("Error: Menu Command not found. Please enter a valid command from the list of commands [/?].");
            }
        }else{
            System.out.println("Error: You have entered an empty input.");
        }

    }

    public void joinServer(String host, int port){
        try {
            this.socket = new Socket(host, port);
            this.msgSocket = new Socket(host, port + 1);
            this.reader = new DataInputStream(socket.getInputStream());
            this.writer = new DataOutputStream(socket.getOutputStream());

            this.isUserConnected = true;

            System.out.println("FROM SERVER: " + this.reader.readUTF());   

        }catch(IOException e) {
            // If connection fails due to server not running or incorrect IP and Port combination
            System.out.println("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
        }
    }

    public void disconnect(){
        if(this.socket != null){
            try {
                this.isUserConnected = false;
                this.isUserRegistered = false;
                this.username = null;
                socket.close();
                socket = null;
                this.writer.close();
                this.writer = null;
                this.reader.close();
                this.reader = null;

                if(this.messageThread != null){
                    this.messageThread.interrupt();
                    this.messageThread = null;
                }
            } catch (IOException e) {
                System.out.println("Error: You aren't connected to any server");
            }

        }
    }

    public void printCommands(){
        System.out.println("\nCommands List:\n-----------------------");
        System.out.println("Connect to the server application: /join <server_ip_add> <port>");
        System.out.println("Disconnect from the server application: /leave");
        System.out.println("Register a unique handle or alias: /register <handle>");
        System.out.println("Send file to server: /store <filename>");
        System.out.println("Request directory file list from a server: /dir");
        System.out.println("Fetch a file from a server: /get <filename>");
        System.out.println("Broadcast a message to all registered clients: /broadcast <message>");
        System.out.println("Message a registered client directly: /message <user> <message>");
        System.out.println("Request command help: /?");

    }

    public Boolean isCommandValid(String command){
        ArrayList<String> validCommandsList = new ArrayList<>(List.of("/join", "/leave", "/register", "/store", "/dir", "/get", "/?", "/exit", "/broadcast", "/message"));

        return validCommandsList.contains(command);
    }



    public void printStrArrayElements(String[] strArray){


        for(int i = 0; i < strArray.length; i++){
            System.out.println(strArray[i]);
        }

    }



    // For Message Thread
    public class MessageThread extends Thread {

        private final Socket msgSocket;
        private final DataInputStream dis;

        public MessageThread(Socket msgSocket) throws IOException {
            this.msgSocket = msgSocket;
            this.dis = new DataInputStream(msgSocket.getInputStream());
        }

        @Override
        public void run() {
            while (isUserRegistered) {
                try {
                    String msg =dis.readUTF();
                    System.out.println("\n" + msg);
                    System.out.printf("Enter command: ");
                } catch (IOException e) {
                    interrupt();
                    break;
                }
            }
        }
    }

}