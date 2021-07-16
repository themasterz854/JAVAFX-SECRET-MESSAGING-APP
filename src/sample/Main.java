package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;


public class Main extends Application {
    public static int flag=0;
    DataOutputStream dout;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        Stage logindialog = new Stage();
        logindialog.setTitle("Login");
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root);
        logindialog.setScene(scene);
        logindialog.showAndWait();
        if(flag == 1) {
            Stage introdialog = new Stage();
            introdialog.setTitle("Intro");
            root = FXMLLoader.load(getClass().getResource("intro.fxml"));
            scene = new Scene(root);
            introdialog.setScene(scene);
            introdialog.showAndWait();
            primaryStage.setTitle("Application");
            primaryStage.setOnCloseRequest(windowEvent -> {
                try {
                    dout = new DataOutputStream(IntroController.s.getOutputStream());
                    dout.writeUTF("exit");
                    dout.flush();
                    primaryStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            root = FXMLLoader.load(getClass().getResource("app.fxml"));
            scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
