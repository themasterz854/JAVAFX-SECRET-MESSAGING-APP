package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MainController {
    @FXML
    private Label status;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    public void  Login(ActionEvent event){
        if(username.getText().equals("user") && password.getText().equals("1234"))
        {
            status.setText("LOGIN SUCCESSFUL");

        }
        else
        {
            status.setText("Login failed");
        }
        username.clear();
        password.clear();


    }

}
