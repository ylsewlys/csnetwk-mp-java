import java.io.*;
import java.net.*;
import java.util.*; // For Scanner

class Client {

    public static void main(String[] args) {

        Socket endpoint = new Socket(); // Initialize Socket
        DataInputStream reader;
        DataOutputStream writer;

        String host = "localhost"; // can be changed
        int port = 5555;

        Boolean isUserJoined = false;

        
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

            }

        }



        String msg;
        try {
            endpoint = new Socket(host, port);

            System.out.println("Client: Connected to server " + host + ":" + port);

            reader = new DataInputStream(endpoint.getInputStream());
            writer = new DataOutputStream(endpoint.getOutputStream());

            System.out.print("> ");
            // Let's try inputting a string in the console
            while (!(msg = sc.nextLine()).equals("END")) {
                // The message will be send to the server
                writer.writeUTF(msg);
                // The Server will append "Server: " so that
                // we know that the message really was accepted
                // by the server
                System.out.println(reader.readUTF());
                System.out.print("> ");
            }

            // Send the terminal String to the Server
            writer.writeUTF("END");

            System.out.println("Client: has terminated connection");

            endpoint.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Client Functions

    public static void handleCommand(Socket clientSocket, String command){

        if(command.length() > 0){
            String[] commandParts = readInputParts(command);
            String commandType = commandParts[0];

            if(commandType == "/join"){
                if(commandParts.length == 3){
                    System.out.println("Join");
                }else{
                    System.out.println("Error: Invalid command parameters. Please make sure you've entered the correct parameters: /join <server_ip_add> <port>");
                }

            }else if(commandType == "/leave"){
                System.out.println("Leave");
            }else{
                System.out.println("Error: Menu Command not found. Please enter a valid command from the list of commands [/?].");
            }
        }else{
            System.out.println("Error: You have entered an empty input.");
        }

    }

    public static void joinServer(Socket clientSocket, String command){
    
    }

    private static boolean isSocketConnected(Socket socket) {
        try {
            // Attempt to write a byte to the OutputStream
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(1);
            outputStream.flush();
            return true; // If successful, the socket is still connected
        } catch (IOException e) {
            // Handle the IOException (socket is not connected)
            return false;
        }
    }

    public static void printCommands(){
        System.out.println("\nCommands List:\n-----------------------");
        System.out.println("Connect to the server application: /join <server_ip_add> <port>");
        System.out.println("Disconnect from the server application: /leave");
        System.out.println("Register a unique handle or alias: /register <handle>");
        System.out.println("Send file to server: /store <filename>");
        System.out.println("Request directory file list from a server: /dir");
        System.out.println("Fetch a file from a server: /get <filename>");
        System.out.println("Request command help: /?");

    }

    public static Boolean isCommandValid(String command){
        ArrayList<String> validCommandsList = new ArrayList<>(List.of("/join", "/leave", "/register", "/store", "/dir", "/get", "/?", "/exit"));

        return validCommandsList.contains(command);
    }




    public static int countParts(String input){

        int count = 1;

        if(input.length() > 0){
            for(int i = 0; i < input.length(); i++){
                if(input.charAt(i) == ' '){
                    count++;
                }
            }
        }else{
            return 0;
        }

        return count;
    }

    public static String[] readInputParts(String input){
        int inputPartsCount = countParts(input);
        String[] inputParts = new String[inputPartsCount];
        int inputPartsIndex = 0;

        String inputPart = "";

        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == ' ' || i == input.length() - 1){

                if(i == input.length() - 1){
                    inputPart = inputPart + input.charAt(i);
                }

                inputParts[inputPartsIndex] = inputPart; // add input part to array
                inputPartsIndex++; // increment index
                inputPart = ""; // reset string input part to read next part
            }else{
                inputPart = inputPart + input.charAt(i);
            }
        }

        return inputParts;
    }

    public static void printStrArrayElements(String[] strArray){


        for(int i = 0; i < strArray.length; i++){
            System.out.println(strArray[i]);
        }

    }



}