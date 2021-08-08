package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;


public class FileChooserController {
    @FXML
    private Button select_one,select_multiple,sendfiles;
    @FXML
    private ListView<String> send_list;
    private Socket s;

    public void transferdata(Socket s)
    {
        this.s = s;
    }
    public void selectafile(){

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All","*"),new FileChooser.ExtensionFilter("Images","*.png","*.jpg"));
        File selected_File = fc.showOpenDialog(null);

        if(selected_File != null)
        {
            send_list.getItems().add(selected_File.getAbsolutePath());
        }
        else
            System.out.print("Not valid file");
    }
    public void selectmultiplefiles(){
        int i;
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All","*"),new FileChooser.ExtensionFilter("Images","*.png","*.jpg"));
        List<File> selected_Files = fc.showOpenMultipleDialog(null);

        if(selected_Files != null)
        {
            for(i=0;i<selected_Files.size();i++)
            send_list.getItems().add(selected_Files.get(i).getAbsolutePath());
        }
        else
            System.out.print("Not valid file");
    }

    public void sendthefiles() throws Exception {
        int i;
        File file;
        FileInputStream fis;

        int n = send_list.getItems().size();
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        for(i=0;i<n;i++)
        {
            file = new File(send_list.getItems().get(i));
            fis = new FileInputStream(file);
            dout.writeUTF("Sending file "+file.getName());
            dout.flush();
            byte[] sendData = new byte[(int)file.length()];
            if(fis.read(sendData) != -1) {
                dout.writeUTF("%file%");
                dout.flush();
                dout.writeUTF(file.getName());
                dout.flush();
                dout.writeUTF(Integer.toString(sendData.length));
                dout.flush();
                dout.write(sendData, 0, sendData.length);
                dout.flush();
            }
        }
        Stage stage = (Stage) send_list.getScene().getWindow();
        stage.close();
    }

}
