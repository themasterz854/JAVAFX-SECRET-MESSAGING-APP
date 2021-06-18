package ClientCode;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receiver extends Thread{
    Socket s;
    public Receiver(Socket s){
        this.s = s;
    }
    public void run() {
        /*
        try {
            String str ;
            DataInputStream din = new DataInputStream(s.getInputStream());
            while (true) {
                str = din.readUTF();
                if(str.equals("exit"))
                {
                    break;
                }
                System.out.println("server says: " + str);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}