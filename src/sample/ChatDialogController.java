package sample;


import javafx.concurrent.Task;
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
    private TextArea ta;
    @FXML
    private TextArea myta;
    int id;

    public void transferdata(){
        id = cid;
    }
    DataOutputStream dout ;
    DataInputStream din;
    String s;
    String[] data = new String[4];
    String[] queue= new String[10];
    int front,rear;
    public synchronized void checkandwrite() throws IOException{
        front = rear = -1;
        while(din.available()> 0)
        {
            s = din.readUTF();
            data = s.split(" ");
            if(Integer.parseInt(data[0]) == id) {
                for(int i =0;i<data.length;i++)
                ta.appendText(data[i]+" ");
                ta.appendText("\n");
            }
            else
            {
                if(front == -1)
                {
                    front = 0;
                }
                queue[++rear] = s;

            }
        }
        while(front != rear + 1 && front != -1 && rear != -1)
        {
            dout.writeUTF("others"+" "+queue[front++]);
        }
    }
    public void run_task(){
        Task task = new Task(){

            @Override
            protected Object call() throws Exception {

                Stage stage ;
                din = new DataInputStream(IntroController.s.getInputStream());
                dout = new DataOutputStream(IntroController.s.getOutputStream());
                stage = (Stage) message.getScene().getWindow();

                while(true)
                {

                    checkandwrite();
                    if(stage.isShowing() == false)
                    {
                        break;
                    }
                    Thread.sleep(3000);

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
