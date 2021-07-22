package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class LoginController {
    @FXML
    private Label status,status2;
    @FXML
    private TextField username,newusername,serverip;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField newpassword,newpassword1;
    private Socket s;
    public Socket getSocket()
    {
        return s;
    }
    public void createnewaccount() throws IOException {
        DataOutputStream dout;
        if(newpassword.getText().equals("") || newusername.getText().equals("") || newpassword1.getText().equals(""))
        {
            status2.setText("All fields must be non empty");
            return;
        }
        if(newpassword.getText().equals(newpassword1.getText()))
        {
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF("newaccount");
            dout.flush();
            dout.writeUTF(newusername.getText());
            dout.writeUTF(newpassword.getText());
            dout.flush();
            newusername.clear();
            newpassword.clear();
            newpassword1.clear();
            status2.setText("Account creation successful. close this and login :)");
            s.close();
        }
        else
            status2.setText("Passwords do not match");
    }
    public void newuser() throws IOException {
        Stage newaccountcreator = new Stage();
        try {
            s = new Socket(serverip.getText(), 4949);
        }catch(SocketException e)
        {
            status2.setText("Server not running at that ip");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("newaccountdialog.fxml"));
        Parent root = loader.load();
        Scene newaccount_scene = new Scene(root);
        newaccountcreator.setScene(newaccount_scene);
        newaccountcreator.show();
    }
    public void  Login() throws IOException {
        if(serverip.getText().equals(""))
        {
            status.setText("pls enter the ip address");
            return;
        }
        try {
            s = new Socket(serverip.getText(), 4949);
        }catch(SocketException e)
        {
            status.setText("Server not running at that ip");
            return;
        }
        String response;
        try {
            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());
            if (username.getText().equals("") || password.getText().equals("")) {
                status.setText("Username,Password should be non empty");
                dout.writeUTF("%exit%");
                s = null;
                return;
            }
            dout.writeUTF(username.getText() + " " + password.getText());
            dout.flush();
            response = din.readUTF();
            if (response.equals("ok")) {
                status.setText("LOGIN SUCCESSFUL");
                username.clear();
                password.clear();
                serverip.clear();
                Stage stage = (Stage) status.getScene().getWindow();
                Main.flag = 1;
                stage.close();
            } else {
                status.setText(response);
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
