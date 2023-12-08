package server_package;
import java.net.*;
import java.util.*; // For Scanner



class Server {

    public static void main(String[] args) {
        int port; // server port number

        Scanner sc = new Scanner(System.in);

        // Input Server Port
        System.out.printf("Enter server port number: ");
        port = sc.nextInt();


        try {
            ServerSocket ss = new ServerSocket(port);

            System.out.println("Sever: Listening to port " + port);

            while (true) {
                // Waits for a client to connect
                Socket endpoint = ss.accept();

                System.out.println("Server: Client at " + endpoint.getRemoteSocketAddress() + " has connected");

                // Make the Thread Object
                Connection connect = new Connection(endpoint);
                // Start the thread
                connect.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}