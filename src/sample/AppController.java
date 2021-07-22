package sample;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AppController {
    DataOutputStream dout ;
    private Socket s;
    public void transferdata(Socket s){
        this.s = s;
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

}
