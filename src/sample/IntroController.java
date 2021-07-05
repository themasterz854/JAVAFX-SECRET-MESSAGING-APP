package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.net.Socket;
import javafx.stage.Stage;

public class IntroController {
    @FXML
    private Button button;

    public static Socket s = null;
    public void start_chatting(ActionEvent event){

        Stage stage1 = (Stage) button.getScene().getWindow();
        stage1.close();
}


}







