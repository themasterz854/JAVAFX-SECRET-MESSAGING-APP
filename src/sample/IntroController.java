package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.Socket;

import javafx.stage.Stage;

public class IntroController {
    @FXML
    private Button button;


    public static Socket s;
    public void start_chatting(ActionEvent event){
         s = null;
        try {
            s = new Socket("localhost", 4848);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage1 = (Stage) button.getScene().getWindow();
        stage1.close();
}


}







