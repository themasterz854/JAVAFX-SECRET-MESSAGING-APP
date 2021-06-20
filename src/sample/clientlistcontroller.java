package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class clientlistcontroller {
    public static int cid =0;
    @FXML
    private Button button1;
    @FXML
    private Button button2;
    @FXML
    private Button button3;
    @FXML
    private Button button4;
    @FXML
    private Button button5;
    @FXML
    private Button button6;
    @FXML
    private Button button7;
    @FXML
    private Button button8;
    @FXML
    private Button button9;
    @FXML
    private Button button10;

    Button[] button = new Button[10];

    public void show() throws IOException {
        String str;
        DataInputStream din = new DataInputStream(IntroController.s.getInputStream());
        DataOutputStream dout = new DataOutputStream(IntroController.s.getOutputStream());
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
                String str;
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
                                str = testbutton.getText();
                                data = str.split(" ");
                                cid = Integer.parseInt(data[1]);
                                chat();
                                Stage stage = (Stage) testbutton.getScene().getWindow();
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
            button[i].setText("Client " + str);
            button[i].setOpacity(1.0);
            button[i].setDisable(false);
            i++;
            str = din.readUTF();
        }
        }
    public void chat() throws IOException {
        DataOutputStream dout = new DataOutputStream(IntroController.s.getOutputStream());
        dout.writeUTF("chat "+cid);
        Stage Chatscreen = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("chatdialog.fxml"));
        Parent root = loader.load();
        Scene ChatScene = new Scene(root);
        Chatscreen.setScene(ChatScene);
        Chatscreen.setTitle("Chatting with "+ cid);
        ChatDialogController cdc = loader.getController();
        cdc.transferdata();
        Chatscreen.show();
    }
    }
