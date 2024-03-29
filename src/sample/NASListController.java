package sample;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;

import static java.lang.Math.round;
import static sample.Main.aes;


public class NASListController extends FileChooserController {
    @FXML
    private ListView<String> FileList;

    @FXML
    private ProgressBar receivepb;
    @FXML
    private TextArea receivestatus;

    @FXML
    private Text receivedprogress;

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
                updateProgress(0.0, 1);

                ObservableList<String> selectedarray = FileList.getSelectionModel().getSelectedItems();
                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream());
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    long totalsize = Long.parseLong(aes.decrypt(din.readUTF()));
                    long receivedsofar = 0;
                    FileOutputStream fos = null;
                    byte[] receivedData;
                    for (String ignored : selectedarray) {
                        String str = aes.decrypt(din.readUTF());
                        if (str.equals("%NASFile%")) {
                            int actualreceived, received;
                            String fileName = aes.decrypt(din.readUTF());
                            receivestatus.appendText("Receiving file " + fileName + "\n");
                            fos = new FileOutputStream(directory.getAbsolutePath() + "/" + fileName);
                            while (true) {
                                actualreceived = Integer.parseInt(aes.decrypt(din.readUTF()));
                                if (actualreceived < 0) {
                                    break;
                                }
                                receivedsofar += actualreceived;
                                received = Integer.parseInt(aes.decrypt(din.readUTF()));
                                receivedData = new byte[received];
                                System.gc();
                                din.readFully(receivedData);
                                receivedData = aes.decrypt(receivedData);
                                fos.write(receivedData, 0, receivedData.length);
                                dout.writeUTF(aes.encrypt("Client ACK"));
                                dout.flush();
                                updateProgress(receivedsofar, totalsize);
                                receivedprogress.setText(round(((double) receivedsofar / totalsize) * 100) + "%");
                            }
                            StringBuilder hash = new StringBuilder(aes.decrypt(din.readUTF()));
                            System.out.println("receiving hash " + hash);
                            fos.close();
                        }
                        receivedData = null;
                        System.gc();

                    }

                    receivestatus.appendText("All files received\n");

                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                return null;
            }
        };
        receivepb.setProgress(0.0);
        new Thread(task).start();
        receivepb.progressProperty().bind(task.progressProperty());

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
        for (String ignored : selectedarray) {
            receivestatus.appendText(aes.decrypt(din.readUTF()));
        }
        receivestatus.appendText(aes.decrypt(din.readUTF()));
        refresh();
    }

    public void uploadthefiles() {
        status.appendText("\nUploading the files to NAS Server\n");
        send_thread("%NASupload%");
    }

}