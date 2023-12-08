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




    public Client(){
        this.isUserConnected = false;
        this.isUserRegistered = false;
    }



        /* 
        String userInput = "";
        Scanner sc = new Scanner(System.in);
        

        // While user does not exit the program
        while(!(userInput.compareTo("/exit") == 0)){
            System.out.printf("\nEnter command: ");
            userInput = sc.nextLine();

            String[] commandParts = readInputParts(userInput);
            String commandType = commandParts[0];

            // If command is not valid
            if(!(isCommandValid(commandType))){
                System.out.println("Error: Command not found.");
            }else if(userInput.compareTo("/?") == 0){
                // If user requests help for commands
             printCommands();
            }else if(isSocketConnected(endpoint) == false && commandType.compareTo("/join") != 0){
                // If client's socket is not connected and does not attempt to use /join command
                System.out.println("Client: You must join a server before you can use the " + commandType + " command.");
            }else if(isSocketConnected(endpoint) == false && commandType.compareTo("/join") == 0){
                // If client's socket is not connected and attempts to use /join command

                // If parameters are incorrect
                if(commandParts.length != 3){
                    System.out.println("Error: Command parameters do not match or is not allowed.");
                }else{
                    try {
                        host = commandParts[1];
                        port = Integer.parseInt(commandParts[2]);

                        endpoint = new Socket(host, port);

                        System.out.println("Client: Has connected to server " + host + ":" + port);

                        reader = new DataInputStream(endpoint.getInputStream());
                        writer = new DataOutputStream(endpoint.getOutputStream());

                    } catch (ConnectException e) {
                        // If connection fails due to server not running or incorrect IP and Port combination
                        System.out.println("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
                    } catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }else if(isSocketConnected(endpoint) == true && commandType.compareTo("/join") == 0){
                // If connected client attempts to connect to a server 
                System.out.println("Error: You are already connected to a server: " + host + ":" + port + ". Please disconnect [/leave] from your current server if you want to connect to another.");
            }
            */
        


    // Getters

    public Boolean getUserConnectStatus(){
        return this.isUserConnected;
    }


    public Boolean getUserRegistrationStatus(){
        return this.isUserRegistered;
    }



    // Client Functions


    public void handleCommand(String command){

        if(command.length() > 0){
            String[] commandParts = command.split(" ");
            String commandType = commandParts[0];

            // If client is not connected and attempts to use other commands besides /join
            if(commandType.compareTo("/join") != 0 && this.isUserConnected == false){
                System.out.println("Client: You must join a server before you can use the " + commandType + " command.");
            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == true){
                // If client is already connected to a server and attempts to join one.
                System.out.println("Error: You are already connected to a server: " + this.host + ":" + this.port + ". Please disconnect [/leave] from your current server if you want to connect to another.");
            }else if(commandType.compareTo("/join") == 0 && this.isUserConnected == false){
                if(commandParts.length == 3){
                    this.host = commandParts[1];
                    this.port = Integer.parseInt(commandParts[2]);

                    joinServer(host, port);

                }else{
                    System.out.println("Error: Invalid command parameters. Please make sure you've entered the correct parameters: /join <server_ip_add> <port>");
                }
            }else if(commandType.compareTo("/leave") == 0){
                System.out.println("Leave");
            }else if(commandType.compareTo("/?") == 0){
                System.out.println("/?");
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

        }catch(ConnectException e) {
            // If connection fails due to server not running or incorrect IP and Port combination
            System.out.println("Error: Connection to the Server has failed! Please check IP Address and Port Number.");
        }catch(IOException e){
            e.printStackTrace();
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


    // public int countParts(String input){

    //     int count = 1;

    //     if(input.length() > 0){
    //         for(int i = 0; i < input.length(); i++){
    //             if(input.charAt(i) == ' '){
    //                 count++;
    //             }
    //         }
    //     }else{
    //         return 0;
    //     }

    //     return count;
    // }

    // public String[] readInputParts(String input){
    //     int inputPartsCount = countParts(input);
    //     String[] inputParts = new String[inputPartsCount];
    //     int inputPartsIndex = 0;

    //     String inputPart = "";

    //     for(int i = 0; i < input.length(); i++){
    //         if(input.charAt(i) == ' ' || i == input.length() - 1){

    //             if(i == input.length() - 1){
    //                 inputPart = inputPart + input.charAt(i);
    //             }

    //             inputParts[inputPartsIndex] = inputPart; // add input part to array
    //             inputPartsIndex++; // increment index
    //             inputPart = ""; // reset string input part to read next part
    //         }else{
    //             inputPart = inputPart + input.charAt(i);
    //         }
    //     }

    //     return inputParts;
    // }

    public void printStrArrayElements(String[] strArray){


        for(int i = 0; i < strArray.length; i++){
            System.out.println(strArray[i]);
        }

    }



}