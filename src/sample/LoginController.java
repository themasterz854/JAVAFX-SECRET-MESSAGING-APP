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
import java.util.regex.Pattern;


public class LoginController extends Controller{
    private String usernamestr;
    @FXML
    private Label status,status2;
    @FXML
    private TextField username,newusername,serverip;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField newpassword,newpassword1;

    private int closingflag = 0;
    public Socket getSocket()
    {
        return s;
    }
    public String getusername()
    {
        return usernamestr;
    }
    public void createnewaccount() {

        try {
            Pattern P1 = Pattern.compile("([$-/:-?{-~!\"^_`\\[\\]])[A-Za-z0-9]*");
            Pattern P2 = Pattern.compile("[A-Za-z0-9]*[$-/:-?{-~!\"^_`\\[\\]]");
            Pattern P3 = Pattern.compile("[A-Za-z0-9]*[$-/:-?{-~!\"^_`\\[\\]][A-Za-z0-9]*");

            if (newpassword.getText().equals("") || newusername.getText().equals("") || newpassword1.getText().equals("")) {
                status2.setText("All fields must be non empty");
                return;
            }
            String newusernamestr = newusername.getText().trim();
            String newpasswordstr = newpassword.getText().trim();
            if (P1.matcher(newusernamestr).matches() || P2.matcher(newusernamestr).matches() || P3.matcher(newusernamestr).matches()) {
                status2.setText("no special characters allowed");
                return;
            }
            if (P1.matcher(newpasswordstr).matches() || P2.matcher(newpasswordstr).matches() || P3.matcher(newpasswordstr).matches()) {
                status2.setText("no special characters allowed");
                return;
            }
            if (newpassword.getText().equals(newpassword1.getText())) {

                dout.writeUTF("newaccount");
                dout.flush();

                dout.writeUTF(newusernamestr);
                dout.writeUTF(newpasswordstr);
                dout.flush();
                newusername.clear();
                newpassword.clear();
                newpassword1.clear();
                status2.setText("Account creation successful. Close this and login :)");
                s.close();
                closingflag = 1;
            } else
                status2.setText("Passwords do not match");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public void newuser() {
        Stage newaccountcreator = new Stage();

        try {
            s = new Socket(serverip.getText().trim().split(":")[0], Integer.parseInt(serverip.getText().trim().split(":")[1]));
        }catch(Exception e)
        {
            status2.setText("Server not running at that ip");
            return;
        }
        try {
            dout = new DataOutputStream(s.getOutputStream());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newaccountdialog.fxml"));
            Parent root = loader.load();
            Scene newaccount_scene = new Scene(root);
            newaccountcreator.setScene(newaccount_scene);
            newaccountcreator.showAndWait();
            if (closingflag == 0) {
                dout.writeUTF("%exit%");
                dout.flush();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }

    }
    public void  Login() throws IOException {
        if(serverip.getText().equals(""))
        {
            status.setText("pls enter the ip address");
            return;
        }
        try {
            s = new Socket(serverip.getText().trim().split(":")[0], Integer.parseInt(serverip.getText().trim().split(":")[1]));
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
            usernamestr = username.getText().trim();
            dout.writeUTF(usernamestr + " " + password.getText().trim());
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
            System.exit(-1);
        }
    }

}
