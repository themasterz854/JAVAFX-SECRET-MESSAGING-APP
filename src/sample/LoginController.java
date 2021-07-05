package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LoginController {
    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField newusername;
    @FXML
    private PasswordField newpassword;
    @FXML
    private PasswordField newpassword1;
    @FXML
    private Label status2;

    public void createnewaccount() throws IOException {
        DataOutputStream dout;
        if(newpassword.getText().equals(newpassword1.getText()))
        {
            dout = new DataOutputStream(IntroController.s.getOutputStream());
            dout.writeUTF("newaccount");
            dout.flush();
            dout.writeUTF(newusername.getText());
            dout.writeUTF(newpassword.getText());
            dout.flush();
            newusername.clear();
            newpassword.clear();
            newpassword1.clear();
            status2.setText("Account creation successful. close this and login :)");
            IntroController.s.close();
        }
        else
            status2.setText("Passwords do not match");



    }
    public void newuser() throws IOException {
        Stage newaccountcreator = new Stage();
        IntroController.s = new Socket("192.168.0.105",4949);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newaccountdialog.fxml"));
        Parent root = loader.load();
        Scene newaccount_scene = new Scene(root);
        newaccountcreator.setScene(newaccount_scene);
        newaccountcreator.show();
    }
    public void  Login() throws IOException {
        IntroController.s = new Socket("192.168.0.105", 4949);
        String response;
        DataOutputStream dout = new DataOutputStream(IntroController.s.getOutputStream());
        DataInputStream din = new DataInputStream(IntroController.s.getInputStream());
        dout.writeUTF(username.getText()+" "+ password.getText());
        dout.flush();
        response = din.readUTF();
        if(response.equals("ok"))
        {
            status.setText("LOGIN SUCCESSFUL");
            username.clear();
            password.clear();
            Stage stage = (Stage) status.getScene().getWindow();
            Main.flag = 1;
            stage.close();
        }
        else
        {
            status.setText("Login failed");
        }

    }
}
