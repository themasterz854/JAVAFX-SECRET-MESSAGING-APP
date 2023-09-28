package sample;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

import static sample.Main.aes;


public class ChatDialogController extends Controller {
    @FXML
    private AnchorPane ap;
    private final String[] queue = new String[10];
    @FXML
    private TextField message;
    @FXML
    private Button send_file_button, dirchoose;
    @FXML
    private ToggleButton togglebutton;
    @FXML
    private Label encryplabel;
    private int id;
    @FXML
    private TextArea ta, myta;
    private boolean encryptflag = false;
    private File directory = new File(String.format("%s/Downloads", System.getProperty("user.home").replace('\\', '/')));
    private final DirectoryChooser dc = new DirectoryChooser();

    public void transferdata(int cid, Socket s, Socket cs, Socket ds, Socket us) {
        id = cid;
        this.s = s;
        this.cs = cs;

        this.ds = ds;
        this.us = us;
    }

    public void direchooser() {
        directory = dc.showDialog(null);
    }

    public synchronized void checkandwrite() throws Exception {

        int front, rear;
        front = rear = -1;
        String fileName;
        String hash;
        byte[] receivedData;
        FileOutputStream fos = null;
        din = new DataInputStream(cs.getInputStream());
        dout = new DataOutputStream(cs.getOutputStream());
        while (din.available() > 0) {
            try {
                String str = aes.decrypt(din.readUTF());
                if (str.equals("%file%")) {
                    int actualreceived, received;
                    fileName = aes.decrypt(din.readUTF());
                    while (true) {
                        actualreceived = Integer.parseInt(aes.decrypt(din.readUTF()));
                        if (actualreceived < 0) {
                            break;
                        }
                        received = Integer.parseInt(aes.decrypt(din.readUTF()));
                        receivedData = new byte[received];
                        din.readFully(receivedData);
                        receivedData = aes.decrypt(receivedData);
                        System.gc();
                        fos = new FileOutputStream(directory.getAbsolutePath() + "/" + fileName);
                        fos.write(receivedData, 0, receivedData.length);

                    }
                    hash = aes.decrypt(din.readUTF());

                    fos.close();
                    myta.appendText("Receiving hash for file " + fileName + "\n" + hash + "\n");
                    ta.appendText("\n\n");
                    receivedData = null;
                    System.gc();
                } else {
                    String[] data = str.split(" ");
                    if (Integer.parseInt(data[0]) == id) {
                        for (int i = 1; i < data.length; i++)
                            ta.appendText(data[i] + " ");
                        ta.appendText("\n");
                        myta.appendText("\n");
                    } else {
                        if (front == -1) {
                            front = 0;
                        }
                        queue[++rear] = str;

                    }

                    while (front != rear + 1 && front != -1) {
                        dout.writeUTF(aes.encrypt(("%others%" + " " + queue[front++])));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }

    }

    void run_task() {
        Task<Thread> task = new Task<>() {

            @Override
            protected Thread call() throws Exception {

                Stage stage;
                din = new DataInputStream(cs.getInputStream());
                dout = new DataOutputStream(cs.getOutputStream());
                stage = (Stage) message.getScene().getWindow();

                while (true) {
                    while (!stage.isFocused()) {
                        Thread.sleep(200);
                    }
                    checkandwrite();
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

    public void changereceiver() {
        try {
            dout = new DataOutputStream(cs.getOutputStream());
            dout.writeUTF(aes.encrypt(("%chat% " + id)));
            dout.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    public void encryption_toggle() {
        String str;
        try {
            if (togglebutton.isSelected()) {
                ap.getStyleClass().remove("chatbg");
                ap.getStyleClass().add("encryptchatbg");
                str = "%enableencryption%";
                dirchoose.setOpacity(0.0);
                dirchoose.setDisable(true);
                send_file_button.setOpacity(0.0);
                send_file_button.setDisable(true);
                encryplabel.setText("Encryption is ON");
                encryptflag = true;
            } else {
                ap.getStyleClass().remove("encryptchatbg");
                ap.getStyleClass().add("chatbg");
                encryptflag = false;
                dirchoose.setDisable(false);
                dirchoose.setOpacity(1.0);
                send_file_button.setOpacity(1.0);
                send_file_button.setDisable(false);
                encryplabel.setText("Encryption is OFF");
                str = "%disableencryption%";
            }
            dout.writeUTF(aes.encrypt(str));
            dout.flush();
            myta.clear();
            ta.clear();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void send_message() {
        try {
            dout = new DataOutputStream(cs.getOutputStream());
            dout.writeUTF(aes.encrypt(message.getText()));
            dout.flush();
            if (!encryptflag) {
                synchronized (myta) {
                    ta.appendText("\n");
                    myta.appendText(message.getText() + "\n");
                }
            }
            message.clear();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void start_file_window() {
        try {
            Stage file_chooser = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FileChooser.fxml"));
            Parent root = loader.load();
            FileChooserController fcc = loader.getController();
            fcc.transferdata(s, us);
            Scene list_scene = new Scene(root);
            file_chooser.setScene(list_scene);
            file_chooser.setResizable(false);
            file_chooser.setTitle("File sender");
            file_chooser.setOnCloseRequest(windowEvent -> {
                file_chooser.close();
                System.gc();
            });
            file_chooser.show();
            myta.appendText("Sending File");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
