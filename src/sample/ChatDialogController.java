package sample;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;


public class ChatDialogController {
    @FXML
    private AnchorPane ap;
    @FXML
    private Button send_file_button,dirchoose;
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
    public synchronized void checkandwrite() throws Exception{
        front = rear = -1;

        while(din.available()> 0) {
            try {
                str = din.readUTF();
                if (str.equals("%file%")) {
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
            }catch (Exception e)
            {
                e.printStackTrace();

            }
        }
    }
     void run_task(){
        Task<Thread> task = new Task<>(){

            @Override
            protected Thread call() throws Exception {

                Stage stage ;
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                stage = (Stage) message.getScene().getWindow();

                while(true)
                {
                    while(!stage.isFocused())
                    {
                        Thread.sleep(200);
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

    public void changereceiver() throws Exception {
        dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF("%chat% "+id);
        dout.flush();
    }
    public void encryption_toggle() throws Exception {
        String str;
        ap.getStylesheets().add("sample/design.css");
        if(togglebutton.isSelected()) {
            ap.getStyleClass().remove("chatbg");
            ap.getStyleClass().add("encryptchatbg");
            str = "%enableencryption%";
            encryptflag = true;
        }
        else
        {
            ap.getStyleClass().remove("encryptchatbg");
            ap.getStyleClass().add("chatbg");
            encryptflag = false;
            str = "%disableencryption%";
        }
        dout.writeUTF(str);
        dout.flush();
        myta.clear();
        ta.clear();
    }
    public void send_message() throws Exception {
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
    public void start_file_window(){
        try {
            Stage file_chooser = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FileChooser.fxml"));
            Parent root = loader.load();
            FileChooserController fcc = loader.getController();
            fcc.transferdata(s);
            Scene list_scene = new Scene(root);
            file_chooser.setScene(list_scene);
            file_chooser.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
