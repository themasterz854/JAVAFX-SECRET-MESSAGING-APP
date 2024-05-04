package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static sample.Main.aes;

public class AppController extends Controller {
    @FXML
    private Label welcome;
    @FXML
    private Label NAS_Status;
    @FXML
    private Button NAS_Button;

    public void transferdata(Socket s, Socket cs, Socket ds, Socket us, String username, String nas_status) {
        this.s = s;
        this.cs = cs;
        this.ds = ds;
        this.us = us;
        welcome.setText("WELCOME " + username);
        NAS_Status.setText(nas_status);
        if (NAS_Status.getText().equals("NAS_ONLINE")) {
            NAS_Button.setDisable(false);
        } else if (NAS_Status.getText().equals("NAS_OFFLINE")) {
            NAS_Button.setDisable(true);
        }
    }

    public void get_list() {
        try {
            dout = new DataOutputStream(s.getOutputStream());
            Stage client_list_stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("client_list.fxml"));
            Parent root = loader.load();
            clientlistcontroller clc = loader.getController();
            clc.transferdata(s, cs, ds, us);
            Scene list_scene = new Scene(root);
            client_list_stage.setScene(list_scene);
            dout.writeUTF(aes.encrypt("%list%"));
            client_list_stage.setResizable(false);
            client_list_stage.setOnCloseRequest(windowEvent -> {
                client_list_stage.close();
                System.gc();
            });
            client_list_stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void decryptservice() {
        try {
            Stage decryptstage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("decryption.fxml"));
            Parent root = loader.load();
            DecryptionController dc = loader.getController();
            dc.transferdata(s);
            Scene decryptscene = new Scene(root);
            decryptstage.setScene(decryptscene);
            dc.run_task();
            decryptstage.setResizable(false);
            decryptstage.setTitle("Decryption Service");
            decryptstage.setOnCloseRequest(windowEvent -> {
                decryptstage.close();
                System.gc();
            });
            decryptstage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void NAS() {
        try {
            dout = new DataOutputStream(s.getOutputStream());
            Stage NASList = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NAS.fxml"));
            Parent root = loader.load();
            NASListController naslc = loader.getController();
            naslc.transferdata(s, ds, us);
            Scene list_scene = new Scene(root);
            NASList.setScene(list_scene);
            NASList.setResizable(false);
            NASList.setOnCloseRequest(windowEvent -> {
                try {
                    DataOutputStream dout = new DataOutputStream(ds.getOutputStream());
                    dout.writeUTF(aes.encrypt("%exit%"));
                    dout = new DataOutputStream(us.getOutputStream());
                    dout.writeUTF(aes.encrypt("%exit%"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                NASList.close();
                System.gc();
            });
            NASList.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


}
