package client_package;


import java.io.*;
import java.net.*;
import java.util.*; // For Scanner

public class Client {

    private String host;
    private int port;

    private Socket socket;
    private DataInputStream reader;
    private DataOutputStream writer;

    private Boolean isUserConnected;
    private Boolean isUserRegistered;
    private String username = new String();




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
            if(isCommandValid(commandType) == false){
                System.out.println("Error: Command not found.");
            }
            else if(commandType.compareTo("/join") != 0 && this.isUserConnected == false){
                // If client is not connected and attempts to use other commands besides /join
                System.out.println("Client: You must join a server before you can use the " + commandType + " command.");

        
            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == true){
                // If client is already connected to a server and attempts to join one.
                System.out.println("Error: You are already connected to a server: " + this.host + ":" + this.port + ". Please disconnect [/leave] from your current server if you want to connect to another.");

            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == false){
                // If client is not connected and attempts to join
                if(commandParts.length == 3){
                    this.host = commandParts[1];
                    this.port = Integer.parseInt(commandParts[2]);
                
                    joinServer(host, port);

                }else{
                    System.out.println("Error: Invalid command parameters. Please make sure you've entered the correct parameters: /join <server_ip_add> <port>");
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
                        System.out.println("Client connection successfully closed.");
                        disconnect();
                    }else if(status.compareTo("Parameter mismatch") == 0){
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }

                }catch(IOException e){
                    e.printStackTrace();
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
                    e.printStackTrace();
                }
            }else if(commandType.compareTo("/register") == 0){
                try{
                    //checks if the 
                    if(commandParts.length != 2){
                    System.out.println("Error: Invalid command parameters. Please make sure you've entered the correct parameters: /register <username>");
                    }else{
                        this.writer.writeUTF(command);
                        this.writer.flush();
                        System.out.println(this.reader.readUTF());
                        String response = this.reader.readUTF();
                        if(response.startsWith("Welcome ")){
                            this.isUserRegistered = true;
                            this.username = commandParts[1];
                        }
                        System.out.println(response);
                    }
                
                }catch(IOException e){
                        e.printStackTrace();
                    }
                
            }else if(commandType.compareTo("/store") == 0){
                try{
                    this.writer.writeUTF(command);
                    System.out.println(this.reader.readUTF());
                }catch(IOException e){
                    e.printStackTrace();
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
                    e.printStackTrace();
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
        System.out.println("Request command help: /?");

    }

    public Boolean isCommandValid(String command){
        ArrayList<String> validCommandsList = new ArrayList<>(List.of("/join", "/leave", "/register", "/store", "/dir", "/get", "/?", "/exit"));

        return validCommandsList.contains(command);
    }



    public void printStrArrayElements(String[] strArray){


        for(int i = 0; i < strArray.length; i++){
            System.out.println(strArray[i]);
        }

    }



}