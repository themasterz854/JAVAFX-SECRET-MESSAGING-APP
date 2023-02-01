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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sample.Main.aes;

public class clientlistcontroller extends Controller {
    private int cid = 0;
    @FXML
    private Button button1, button2, button3, button4, button5, button6, button7, button8, button9, button10, showbutton;

    private Stage stage;
    private String currentchat;
    private final List<Button> ButtonList = new ArrayList<>();

    public void transferdata(Socket s) {
        this.s = s;
    }

    public void show() throws IOException {
        String str;
        din = new DataInputStream(s.getInputStream());
        Collections.addAll(ButtonList, button1, button2, button3, button4, button5, button6, button7, button8, button9, button10);

        int i;
        try {
            for (i = 0; i < 10; i++) {
                ButtonList.get(i).setOnAction(new EventHandler<>() {
                    String str1;
                    String[] data;
                    Button testbutton;

                    int i;

                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            testbutton = (Button) event.getSource();
                            for (i = 0; i < 10; i++) {
                                if (testbutton == ButtonList.get(i)) {
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
            str = aes.decrypt(din.readUTF());
            i = 0;
            while (!str.equals("end of list")) {

                ButtonList.get(i).setText(str);
                ButtonList.get(i).setDisable(false);
                ButtonList.get(i).setOpacity(1.0);
                i++;
                str = aes.decrypt(din.readUTF());
            }
            stage = (Stage) button1.getScene().getWindow();
            stage.close();
            showbutton.setDisable(true);
            showbutton.setOpacity(0.0);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void chat() throws IOException {
        try {
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(aes.encrypt(("%chat% " + cid)));
            Stage Chatscreen = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatdialog.fxml"));
            Parent root = loader.load();
            Scene ChatScene = new Scene(root);
            Chatscreen.setScene(ChatScene);
            Chatscreen.setTitle("Chatting with " + currentchat);
            ChatDialogController cdc = loader.getController();
            cdc.transferdata(cid, s, cs, ds, us);
            cdc.run_task();
            Chatscreen.setResizable(false);
            Chatscreen.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
