package sample;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class AppController {


    DataOutputStream dout ;

    public void get_list() throws IOException {
        dout = new DataOutputStream(IntroController.s.getOutputStream());
        Stage client_list = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client_list.fxml"));
        Parent root = loader.load();
        Scene list_scene = new Scene(root);
        client_list.setScene(list_scene);
        dout.writeUTF("list");
        client_list.show();
    }

}
