package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DecryptionController {

    @FXML
    private TextArea ta,ota;
    private Socket s;
    DataInputStream din ;
    DataOutputStream dout ;
    public void transferdata(Socket s)
    {
        this.s = s;
    }
    public void decryptrequest() throws IOException {
        String str;
        str = ta.getText();
        dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF("%decrypt%");
        dout.writeUTF(str);
        dout.flush();
        ta.clear();

    }
    public void checkandwrite2() throws IOException {
        din = new DataInputStream(s.getInputStream());
        String str;
        while(din.available() > 0) {
            str = din.readUTF();
            ota.appendText(str);
            ota.appendText("\n");
        }

    }
    void run_task(){
        Task task = new Task(){

            @Override
            protected Object call() throws Exception {

                Stage stage ;

                stage = (Stage) ta.getScene().getWindow();

                while(true)
                {
                    while(!stage.isFocused())
                    {
                        Thread.sleep(300);
                    }
                    checkandwrite2();
                    if(!stage.isShowing())
                    {
                        break;
                    }
                    Thread.sleep(100);

                }
                return null;
            }
        };
        new Thread(task).start();
    }

}
