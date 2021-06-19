package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import sample.workerclass.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Receiver extends Thread {
    Socket s;
    public Receiver(Socket s){
        this.s = s;
    }
    public void run() {

        try {
            String str ;
            int i;
            DataInputStream din = new DataInputStream(s.getInputStream());
            i =0;
            while (true) {
                str = din.readUTF();
                if(str.equals("sending list"))
                {   


                    System.out.println("Client list created");
                }
                if(str.equals("exit"))
                {
                    break;
                }
                System.out.println("server says: " + str);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}