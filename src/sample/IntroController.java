package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

import ClientCode.*;
import javafx.stage.Stage;

public class IntroController {
    @FXML
    private Button button;



    public void start_chatting(ActionEvent event){
        Socket s = null;
        try {
            s = new Socket("localhost", 4545);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sender sen = new Sender(s);
        Receiver res = new Receiver(s);
        sen.start();
        res.start();
        Stage stage1 = (Stage) button.getScene().getWindow();
        stage1.close();
}


}







