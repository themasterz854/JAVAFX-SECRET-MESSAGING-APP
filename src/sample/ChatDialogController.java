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
import java.net.Socket;


public class ChatDialogController {
    @FXML
    private Button send_file_button,decryptbutton,dirchoose;
    @FXML
    private TextField message;
    @FXML
    private TextArea myta,ta;
    @FXML
    private ToggleButton togglebutton;
    int id;
    DataOutputStream dout ;
    DataInputStream din;
    String str;
    String[] data = new String[4];
    String[] queue= new String[10];
    int front,rear;
    String FileName;
    int FileSize;
    byte[] ReceivedData;
    boolean encryptflag = false;
    File directory;
    DirectoryChooser dc = new DirectoryChooser();
    FileOutputStream fos;
    private Socket s;
    public void transferdata(int cid, Socket s){
        id = cid;
        this.s = s;
    }

    public void direchooser()
    {
        directory = dc.showDialog(null);
    }
    public synchronized void checkandwrite() throws IOException{
        front = rear = -1;

        while(din.available()> 0) {

            str = din.readUTF();
            if(str.equals("%file%"))
            {
                FileName = din.readUTF();
                FileSize = Integer.parseInt(din.readUTF());
                ReceivedData = new byte[FileSize];
                din.readFully(ReceivedData);
                fos = new FileOutputStream(directory.getAbsolutePath() + "\\" + FileName);
                fos.write(ReceivedData, 0, FileSize);
                fos.close();
            } else {
                data = str.split(" ");
                if (Integer.parseInt(data[0]) == id) {
                    for (int i = 1; i < data.length; i++)
                        ta.appendText(data[i] + " ");
                    ta.appendText("\n");
                    myta.appendText("\n");
                } else {
                    if (front == -1) {
                        front = 0;
                    }
                    queue[++rear] = str;

                }

                while (front != rear + 1 && front != -1 && rear != -1) {
                    dout.writeUTF("%others%" + " " + queue[front++]);
            }
            }
        }
    }
    public void run_task(){
        Task task = new Task(){

            @Override
            protected Object call() throws Exception {

                Stage stage ;
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
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
        dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF("%chat% "+id);
        dout.flush();
    }
    public void encryption_toggle() throws IOException {
        String str;
        if(togglebutton.isSelected()) {
            str = "%enableencryption%";
            encryptflag = true;
            decryptbutton.setDisable(false);
            decryptbutton.setOpacity(1.0);
        }
        else
        {
            encryptflag = false;
            str = "%disableencryption%";
            decryptbutton.setDisable(true);
            decryptbutton.setOpacity(0.0);
        }
        dout.writeUTF(str);
        dout.flush();
        myta.clear();
        ta.clear();
    }

    public void decryptrequest() throws IOException {
        String str = ta.getText();
        ta.clear();
        dout.writeUTF("%decrypt%");
        dout.writeUTF(str);
        dout.flush();
    }
    public void send_message() throws IOException {
        dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF(message.getText());
        dout.flush();
        if(!encryptflag) {
            synchronized (myta) {
                ta.appendText("\n");
                myta.appendText(message.getText() + "\n");
            }
        }
        message.clear();
    }

    public void start_file_window() throws IOException {
        Stage file_chooser = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FileChooser.fxml"));

        Parent root = loader.load();
        FileChooserController fcc = loader.getController();
        fcc.transferdata(s);
        Scene list_scene = new Scene(root);
        file_chooser.setScene(list_scene);
        file_chooser.show();
    }
}
