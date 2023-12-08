import client_package.*;

import java.io.*;
import java.util.Scanner;

public class MainClient {
    public static void main(String[] args){


        Client client = new Client();
        Scanner sc = new Scanner(System.in);

        String command;


        try {
            while(true){
                System.out.println("File Exchange System\nEnter command: ");
                command = sc.nextLine();

                client.handleCommand(command);

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }    
}
