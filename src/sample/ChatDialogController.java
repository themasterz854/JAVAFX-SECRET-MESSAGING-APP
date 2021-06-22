package sample;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static sample.clientlistcontroller.cid;

public class ChatDialogController {
    @FXML
    private TextField message;
    @FXML
    private TextArea ta;
    @FXML
    private TextArea myta;
    int id;

    public void transferdata(){
        id = cid;
    }
DataOutputStream dout ;

    public void run_task(){
        Task task = new Task(){

            @Override
            protected Object call() throws Exception {
                String s;
                Stage stage ;


                DataInputStream din = new DataInputStream(IntroController.s.getInputStream());
                stage = (Stage) message.getScene().getWindow();
                while(true)
                {
                    while(stage.isFocused() == false)
                    {
                        Thread.sleep(100);
                    }
                    while(din.available()> 0)
                    {
                        s = din.readUTF();
                        ta.appendText(s + "\n");
                    }
                    if(stage.isShowing() == false)
                    {
                        break;
                    }
                    Thread.sleep(500);
                }
                return null;
            }
        };

        new Thread(task).start();
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
        myta.appendText(message.getText() + "\n");
        message.clear();



    }
}
