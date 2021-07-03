package sample;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;

import static sample.clientlistcontroller.cid;

public class ChatDialogController {
    @FXML
    private Button send_file_button;
    @FXML
    private TextField message;
    @FXML
    private TextArea ta;
    @FXML
    private TextArea myta;
    @FXML
    private Button dirchoose;
    int id;


    DataOutputStream dout ;
    DataInputStream din;
    String s;
    String[] data = new String[4];
    String[] queue= new String[10];
    int front,rear;
    String FileName;
    int FileSize;
    byte[] ReceivedData;
    File directory;
    DirectoryChooser dc = new DirectoryChooser();
    FileOutputStream fos;

    public void transferdata(){
        id = cid;
    }

    public void direchooser()
    {
        directory = dc.showDialog(null);
    }
    public synchronized void checkandwrite() throws IOException{
        front = rear = -1;

        while(din.available()> 0) {
            s = din.readUTF();
            if (s.equals("file")) {
                FileName = din.readUTF();
                FileSize = Integer.parseInt(din.readUTF());
                ReceivedData = new byte[FileSize];
                din.readFully(ReceivedData);
                fos = new FileOutputStream(directory.getAbsolutePath()+"\\"+FileName);
                fos.write(ReceivedData, 0, FileSize);
                fos.close();
            } else {
                data = s.split(" ");
                if (Integer.parseInt(data[0]) == id) {
                    for (int i = 1; i < data.length; i++)
                        ta.appendText(data[i] + " ");
                    ta.appendText("\n");
                } else {
                    if (front == -1) {
                        front = 0;
                    }
                    queue[++rear] = s;

                }

                while (front != rear + 1 && front != -1 && rear != -1) {
                    dout.writeUTF("others" + " " + queue[front++]);
                }
            }
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
                    while(!stage.isFocused())
                    {
                        Thread.sleep(1000);
                    }
                    checkandwrite();
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

    public void start_file_window() throws IOException {
        Stage file_chooser = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FileChooser.fxml"));
        Parent root = loader.load();
        Scene list_scene = new Scene(root);
        file_chooser.setScene(list_scene);
        file_chooser.show();
    }
}
