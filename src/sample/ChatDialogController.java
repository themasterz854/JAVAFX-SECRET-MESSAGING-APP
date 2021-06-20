package sample;

import com.sun.javafx.geom.AreaOp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static sample.clientlistcontroller.cid;

public class ChatDialogController {
    @FXML
    private TextField message;
    @FXML
    private Button send;
    @FXML
    private TextArea TA;
    int id;
    int focusedflag=0;
    public void transferdata(){
        id = cid;
    }
DataOutputStream dout ;

    public void receive_message() throws IOException {
        String s;
        DataInputStream din = new DataInputStream(IntroController.s.getInputStream());
        while(din.available() > 0) {

            s = din.readUTF();
            TA.setText(s);
        }
    }
    public void changereceiver() throws IOException {
        dout = new DataOutputStream(IntroController.s.getOutputStream());
        dout.writeUTF("chat "+id);
        dout.flush();
    }
    public void send_message() throws IOException {
        dout = new DataOutputStream(IntroController.s.getOutputStream());
        dout.writeUTF(message.getText());
        dout.flush();

    }
}
