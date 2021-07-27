package sample;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class AppController{
    @FXML
    private Label welcome;
    DataOutputStream dout ;

    private Socket s;

    public void transferdata(Socket s,String username){
        this.s = s;
        welcome.setText("WELCOME "+username);
    }
    public void get_list() throws IOException {
        dout = new DataOutputStream(s.getOutputStream());
        Stage client_list = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client_list.fxml"));
        Parent root = loader.load();
        clientlistcontroller clc = loader.getController();
        clc.transferdata(s);
        Scene list_scene = new Scene(root);
        client_list.setScene(list_scene);
        dout.writeUTF("%list%");
        client_list.show();
    }

    public void decryptservice() throws IOException {
      Stage decryptstage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("decryption.fxml"));
        Parent root = loader.load();
        DecryptionController dc = loader.getController();
        dc.transferdata(s);
        Scene decryptscene = new Scene(root);
        decryptstage.setScene(decryptscene);
        dc.run_task();
        decryptstage.show();
    }



}
