package sample;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class DecryptionController {
    @FXML
    private TextArea ta,ota;
    private Socket s;
    DataInputStream din ;
    DataOutputStream dout ;
    public void transferdata(Socket s)
    {
        this.s = s;
    }
    public void decryptrequest(){
        String str;
        try {
            str = ta.getText();
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("%decrypt%");
            dout.writeUTF(str);
            dout.flush();
            ta.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    public void checkandwrite2(){
        try {
            din = new DataInputStream(s.getInputStream());
            String str;
            while (din.available() > 0) {
                str = din.readUTF();
                ota.appendText(str);
                ota.appendText("\n");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}