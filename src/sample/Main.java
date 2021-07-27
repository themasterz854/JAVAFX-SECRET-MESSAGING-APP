package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class Main extends Application {
    public static int flag=0;
    DataOutputStream dout;
    private Socket s;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String username;
        FXMLLoader loader;
        Stage logindialog = new Stage();
        logindialog.setTitle("Login");
        loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        logindialog.setScene(scene);
        logindialog.showAndWait();
        if(flag == 1) {
            LoginController lc = loader.getController();
            s = lc.getSocket();
            username = lc.getusername();
            Stage introdialog = new Stage();
            introdialog.setTitle("Intro");
            loader = new FXMLLoader(getClass().getResource("Intro.fxml"));
            root = loader.load();
            scene = new Scene(root);
            introdialog.setScene(scene);
            introdialog.showAndWait();
            primaryStage.setTitle("Application");
            primaryStage.setOnCloseRequest(windowEvent -> {
                try {
                    dout = new DataOutputStream(s.getOutputStream());
                    dout.writeUTF("%exit%");
                    dout.flush();
                    primaryStage.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            loader = new FXMLLoader(getClass().getResource("app.fxml"));
            root = loader.load();
            AppController ac = loader.getController();
            ac.transferdata(s,username);
            scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
