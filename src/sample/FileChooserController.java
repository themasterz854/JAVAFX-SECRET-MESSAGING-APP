package sample;


import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.List;

import static java.lang.Math.round;
import static sample.Main.aes;
import static sample.Main.filebuffer;


public class FileChooserController extends Controller {

    @FXML
    protected ProgressBar pb;
    @FXML
    protected Text sentprogress;
    @FXML
    protected Button select_one, select_multiple, sendfiles;
    @FXML
    protected ListView<String> sendlist;
    @FXML
    protected TextArea status;


    public void transferdata(Socket s, Socket us) {
        this.s = s;
        this.us = us;
    }

    public void selectafile() {
        sendlist.getItems().clear();
        sendlist.getItems().removeAll();

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*"), new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
        File selected_File = fc.showOpenDialog(null);

        if (selected_File != null) {
            sendlist.getItems().add(selected_File.getAbsolutePath());
        } else
            System.out.print("Not valid file");
    }

    public void selectmultiplefiles() {
        sendlist.getItems().clear();
        sendlist.getItems().removeAll();

        int i;
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All", "*"), new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
        List<File> selected_Files = fc.showOpenMultipleDialog(null);

        if (selected_Files != null) {
            for (i = 0; i < selected_Files.size(); i++) {
                sendlist.getItems().add(selected_Files.get(i).getAbsolutePath());
            }
        } else
            System.out.print("Not valid file");
    }

    public void send_thread(String mode) {
        Task<Thread> task = new Task<>() {

            @Override
            protected Thread call() throws Exception {
                dout = new DataOutputStream(s.getOutputStream());
                UploadDout = new DataOutputStream(us.getOutputStream());
                UploadDin = new DataInputStream(us.getInputStream());
                int i;

                File file;
                FileInputStream fis;
                updateProgress(0.0, 1);

                int n = sendlist.getItems().size();
                try {
                    long transferredsofar = 0;
                    long totalsize = 0;
                    for (i = 0; i < n; i++) {
                        totalsize += new File(sendlist.getItems().get(i)).length();
                    }
                    MessageDigest md = MessageDigest.getInstance("SHA-256");


                    StringBuilder hash;
                    dout.writeUTF(aes.encrypt(mode));
                    dout.flush();
                    UploadDout.writeUTF(aes.encrypt(Integer.toString(n)));
                    UploadDout.flush();
                    for (i = 0; i < n; i++) {
                        file = new File(sendlist.getItems().get(i));
                        status.appendText("Sending file " + file.getName() + "\n");
                        fis = new FileInputStream(file);
                        String response = aes.decrypt(UploadDin.readUTF());
                        System.out.println(response);
                        UploadDout.writeUTF(aes.encrypt(file.getName()));
                        UploadDout.flush();
                        byte[] sendData = new byte[filebuffer];
                        byte[] readbytes;
                        byte[] encryptedSendData;
                        int read;
                        while ((read = fis.read(sendData)) > 0) {
                            readbytes = new byte[read];
                            System.arraycopy(sendData, 0, readbytes, 0, read);
                            md.update(readbytes);
                            encryptedSendData = aes.encrypt(readbytes);
                            int encryptedsize = encryptedSendData.length;
                            UploadDout.writeUTF(aes.encrypt(Integer.toString(read)));
                            UploadDout.flush();
                            UploadDout.writeUTF(aes.encrypt(Integer.toString(encryptedsize)));
                            UploadDout.flush();
                            UploadDout.write(encryptedSendData, 0, encryptedsize);
                            UploadDout.flush();
                            System.out.println("sent bytes " + read);
                            System.out.println(aes.decrypt(UploadDin.readUTF()));
                            transferredsofar += read;
                            updateProgress(transferredsofar, totalsize);
                            sentprogress.setText(round(((double) transferredsofar / totalsize) * 100) + "%");
                            sendData = new byte[filebuffer];
                            System.gc();
                        }
                        UploadDout.writeUTF(aes.encrypt(Integer.toString(read)));
                        UploadDout.flush();
                        System.out.println("sent the file");
                        byte[] digest = md.digest();
                        hash = new StringBuilder();
                        for (byte x : digest) {
                            hash.append(String.format("%02x", x));
                        }
                        UploadDout.writeUTF(aes.encrypt(hash.toString()));
                        UploadDout.flush();
                        sendData = encryptedSendData = readbytes = null;
                        System.gc();
                    }

                    status.appendText("All Files uploaded\n");


                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                return null;
            }
        };
        pb.setProgress(0.0);

        new Thread(task).start();

        pb.progressProperty().bind(task.progressProperty());

    }
    public void sendthefiles() {
        status.appendText("\nUploading the files\n");
        send_thread("%file%");
    }

}
