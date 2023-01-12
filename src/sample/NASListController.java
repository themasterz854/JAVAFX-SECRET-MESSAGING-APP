package sample;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

import static sample.Main.aes;


public class NASListController extends FileChooserController {
    @FXML
    private ListView<String> FileList;

    @FXML
    private TextArea receivestatus;

    private File directory = new File(String.format("%s/Downloads", System.getProperty("user.home").replace('\\', '/')));

    public void transferdata(Socket s) {
        this.s = s;

        FileList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void refresh() {
        FileList.getItems().clear();
        try {
            dout = new DataOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
            dout.writeUTF(aes.encrypt("%NAS%"));
            String files = aes.decrypt(din.readUTF());

            String[] filesarray = files.split("\n");
            FileList.getItems().removeAll();
            for (String s : filesarray) {
                FileList.getItems().add(s + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void receive_thread() {
        Task<Thread> task = new Task<>() {

            @Override
            protected Thread call() throws Exception {
                ObservableList<String> selectedarray = FileList.getSelectionModel().getSelectedItems();
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");

                    for (String ignored : selectedarray) {

                        String str = aes.decrypt(din.readUTF());
                        if (str.equals("%file%")) {
                            String hash = aes.decrypt(din.readUTF());
                            String fileName = aes.decrypt(din.readUTF());
                            receivestatus.appendText("Receiving file " + fileName + "\n");
                            int fileSize = Integer.parseInt(aes.decrypt(din.readUTF()));
                            byte[] receivedData = new byte[fileSize];
                            din.readFully(receivedData);
                            receivedData = aes.decrypt(receivedData);

                            FileOutputStream fos = new FileOutputStream(directory.getAbsolutePath() + "/" + fileName);
                            fos.write(receivedData, 0, receivedData.length);
                            fos.close();
                            receivedData = null;
                            System.gc();
                        }
                    }
                    receivestatus.appendText("All files received\n");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    public void receivefile() throws IOException {
        StringBuilder finallist = new StringBuilder();
        ObservableList<String> selectedarray = FileList.getSelectionModel().getSelectedItems();
        for (String s : selectedarray) {
            finallist.append(s);
        }
        dout.writeUTF(aes.encrypt(finallist.toString()));
        dout.flush();
        dout.writeUTF(aes.encrypt("%receive%"));
        dout.flush();
        receivestatus.appendText("Receiving files from NAS Server\n");
        receive_thread();
    }

    public void deletethefiles() throws IOException {
        StringBuilder finallist = new StringBuilder();
        ObservableList<String> selectedarray = FileList.getSelectionModel().getSelectedItems();
        for (String s : selectedarray) {
            finallist.append(s);
        }
        dout.writeUTF(aes.encrypt(finallist.toString()));
        dout.flush();
        dout.writeUTF(aes.encrypt("%delete%"));
        dout.flush();
        receivestatus.appendText("Deleting files on NAS Server\n");
        receivestatus.appendText(aes.decrypt(din.readUTF()));
        refresh();
    }

    public void uploadthefiles() {
        status.appendText("\nUploading the files to NAS Server\n");
        send_thread("%NASupload%");
    }

}