package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController {
    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void  Login(){
        if(username.getText().equals("user") && password.getText().equals("1234"))
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
