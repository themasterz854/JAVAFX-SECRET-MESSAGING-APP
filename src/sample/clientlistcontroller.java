package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class clientlistcontroller {
     int cid =0;
    @FXML
    private Button button1,button2,button3,button4,button5,button6,button7,button8,button9,button10;


    Button[] button = new Button[10];
    Stage stage;
    String currentchat;
    private Socket s;

    public void transferdata(Socket s)
    {
        this.s = s;
    }
    public clientlistcontroller() {
    }

    public  void show() throws IOException {
        String str;
        DataInputStream din = new DataInputStream(s.getInputStream());
        button[0] = button1;
        button[1] = button2;
        button[2] = button3;
        button[3] = button4;
        button[4] = button5;
        button[5] = button6;
        button[6] = button7;
        button[7] = button8;
        button[8] = button9;
        button[9] = button10;
        int i ;
        for(i=0;i<10;i++) {
            button[i].setOnAction(new EventHandler<ActionEvent>() {
                String str1;
                String[] data;
                Button testbutton;

                int i;
                @Override
                public void handle(ActionEvent event) {
                    try {
                        testbutton = (Button) event.getSource();
                        for(i=0;i<10;i++)
                        {
                            if(testbutton ==button[i])
                            {
                                str1 = testbutton.getText();
                                data = str1.split(" ");
                                cid = Integer.parseInt(data[1]);
                                currentchat = data[0];
                                chat();
                                stage = (Stage) testbutton.getScene().getWindow();
                                stage.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        str = din.readUTF();
        i =0;
        while (!str.equals("end of list")) {

            button[i].setText(str);
            button[i].setOpacity(1.0);
            button[i].setDisable(false);
            i++;
            str = din.readUTF();
        }
        stage = (Stage) button1.getScene().getWindow();
        stage.close();
        stage.show();
        }
    public void chat() throws IOException {
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        dout.writeUTF("%chat% "+cid);
        Stage Chatscreen = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatdialog.fxml"));
        Parent root = loader.load();
        Scene ChatScene = new Scene(root);
        Chatscreen.setScene(ChatScene);
        Chatscreen.setTitle("Chatting with "+ currentchat);
        ChatDialogController cdc = loader.getController();
        cdc.transferdata(cid,s);
        cdc.run_task();
        Chatscreen.show();
    }
    }
