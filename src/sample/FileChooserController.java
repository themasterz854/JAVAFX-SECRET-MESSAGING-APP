package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.List;


public class FileChooserController {
    @FXML
    private Button select_one;
    @FXML
    private Button select_multiple;
    @FXML
    private ListView send_list;

    @FXML
    private Button sendfiles;

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

    public void sendthefiles() throws IOException {
        int i;
        File file;
        FileInputStream fis;
        DataOutputStream dout = new DataOutputStream(IntroController.s.getOutputStream());
        for(i=0;i<send_list.getItems().size();i++)
        {
            file = new File((String) send_list.getItems().get(i));
            fis = new FileInputStream(file);
            byte[] sendData = new byte[(int)file.length()];
            fis.read(sendData);
            dout.writeUTF("file");
            dout.flush();
            dout.writeUTF(file.getName());
            dout.flush();
            dout.writeUTF(Integer.toString(sendData.length));
            dout.flush();
            dout.write(sendData,0,sendData.length);
            dout.flush();
        }
    }

}
