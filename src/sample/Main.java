package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

abstract class Controller {
    protected Socket s, us, ds, cs;
    protected DataOutputStream dout, UploadDout, DownloadDout;
    protected DataInputStream din, UploadDin, DownloadDin;
}

public class Main extends Application {

    public static int filebuffer = 1024 * 1024 * 75;
    public static int flag = 0;
    public static AES aes;
    public static String serveripaddress;
    public static int serverport;

    static {
        aes = new AES();
    }

    private DataOutputStream dout;
    private Socket s, cs, ds, us;

    private String nas_status;
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) {
        try {
            String username;
            FXMLLoader loader;
            Stage logindialog = new Stage();
            logindialog.setTitle("Login");
            loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            logindialog.setScene(scene);
            logindialog.setResizable(false);
            logindialog.showAndWait();
            if (flag == 1) {
                LoginController lc = loader.getController();
                s = lc.getSocket();
                cs = lc.getChatSocket();
                ds = lc.getDownloadSocket();
                us = lc.getUploadSocket();
                nas_status = lc.getNAS_Status();
                serveripaddress = ((InetSocketAddress) s.getRemoteSocketAddress()).getAddress().getHostAddress();
                serverport = s.getPort();
                System.out.println("Server address " + serveripaddress + ":" + serverport);
                username = lc.getusername();
                Stage introdialog = new Stage();
                introdialog.setTitle("Intro");
                loader = new FXMLLoader(getClass().getResource("intro.fxml"));
                root = loader.load();
                scene = new Scene(root);
                introdialog.setScene(scene);
                introdialog.setResizable(false);
                introdialog.showAndWait();
                primaryStage.setTitle("Application");
                primaryStage.setOnCloseRequest(windowEvent -> {
                    try {
                        dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF(aes.encrypt("%exit%"));
                        dout.flush();
                        primaryStage.close();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                });
                loader = new FXMLLoader(getClass().getResource("app.fxml"));
                root = loader.load();
                AppController ac = loader.getController();

                ac.transferdata(s, cs, ds, us, username, nas_status);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
                System.gc();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

}
