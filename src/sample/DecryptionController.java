package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static sample.Main.aes;

public class DecryptionController extends Controller {
    @FXML
    private TextArea ta, ota;

    public void transferdata(Socket s) {
        this.s = s;

    }

    public void decryptrequest() {
        String str;
        try {
            str = ta.getText();
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(aes.encrypt("%decrypt%"));
            dout.writeUTF(aes.encrypt(str));
            dout.flush();
            ta.clear();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void checkandwrite2() {
        try {
            din = new DataInputStream(s.getInputStream());
            String str;
            while (din.available() > 0) {
                str = aes.decrypt(din.readUTF());
                ota.appendText(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void run_task() {
        try {
            Task<Thread> task = new Task<>() {
                @Override
                protected Thread call() throws Exception {
                    Stage stage;
                    stage = (Stage) ta.getScene().getWindow();
                    while (true) {
                        while (!stage.isFocused()) {
                            Thread.sleep(300);
                        }
                        checkandwrite2();
                        if (!stage.isShowing()) {
                            break;
                        }
                        Thread.sleep(100);
                    }
                    return null;
                }
            };
            new Thread(task).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}