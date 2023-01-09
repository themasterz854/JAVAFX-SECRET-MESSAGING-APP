package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static sample.Main.aes;


public class NASListController extends Controller {
    @FXML
    private ListView<String> FileList;

    @FXML
    private TextArea selectedfiles;

    public void transferdata(Socket s) {
        this.s = s;

        FileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void refresh() {
        try {
            dout = new DataOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
            dout.writeUTF(aes.encrypt("%NAS%"));
            String files = aes.decrypt(din.readUTF());
            System.out.println(files);
            String[] filesarray = files.split("\n");
            FileList.getItems().removeAll();
            for (String s : filesarray) {
                FileList.getItems().add(s + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receivefile() throws IOException {
        String finallist = "";
        ObservableList<String> selectedarray = FileList.getSelectionModel().getSelectedItems();
        for (String s : selectedarray) {
            finallist += s;
        }
        selectedfiles.setText(finallist);

        dout.writeUTF(aes.encrypt(finallist));
    }
}
