package server_package;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;  

public class MsgClient {

    private final Socket msgClientSocket;
    private final DataOutputStream msgWriter;

    public MsgClient(Socket msgClientSocket) throws IOException{
        this.msgClientSocket = msgClientSocket;
        this.msgWriter = new DataOutputStream(this.msgClientSocket.getOutputStream());
    }

    public void sendMessage(String username, String message, Boolean isBroadcast){

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String formattedMessage = "";

            if(isBroadcast){
                formattedMessage = String.format("[BROADCAST] <%s> From %s: %s", dtf.format(now), username, message);
            }else{
                formattedMessage = String.format("<%s> From %s: %s", dtf.format(now), username, message);
            }
            
            this.msgWriter.writeUTF(formattedMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close(){
        try {
            if(msgClientSocket != null && !msgClientSocket.isClosed()) {
                msgClientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 

}
