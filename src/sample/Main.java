package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AES {
    private static final String encryptionKey           = "ABCDEFGHIJKLMNOP";
    private static final String characterEncoding       = "UTF-8";
    private static final String cipherTransformation    = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithm = "AES";

    public String encrypt(String plainText) {
        String encryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(cipherText);

        } catch (Exception E) {
            System.err.println("Encrypt Exception : " + E.getMessage());
        }
        return encryptedText;
    }

    public byte[] encrypt(byte[] plainText) {
        byte[] encryptedBytes = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText);
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedBytes = encoder.encode(cipherText);

        } catch (Exception E) {
            System.err.println("Encrypt Exception : " + E.getMessage());
        }
        return encryptedBytes;
    }

    public String decrypt(String encryptedText) {
        String decryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText);
            decryptedText = new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);

        } catch (Exception E) {
            System.err.println("decrypt Exception : " + E.getMessage());
        }
        return decryptedText;
    }

    public byte[] decrypt(byte[] encryptedText) {
        byte[] decryptedText = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithm);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText);
            decryptedText = cipher.doFinal(cipherText);

        } catch (Exception E) {
            System.err.println("decrypt Exception : " + E.getMessage());
        }
        return decryptedText;
    }


}

abstract class Controller {
    protected Socket s;
    protected DataOutputStream dout;
    protected DataInputStream din;
}
public class Main extends Application {
    public static int flag=0;
    public static AES aes = new AES();
    private DataOutputStream dout;
    private Socket s;
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
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                        System.exit(0);
                    }
                });
                loader = new FXMLLoader(getClass().getResource("app.fxml"));
                root = loader.load();
                AppController ac = loader.getController();
                ac.transferdata(s, username);
                scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
            }
        }
          catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
    }

}
