package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.regex.Pattern;

import static sample.Main.aes;


public class LoginController extends Controller {

    private String usernamestr;
    @FXML
    private Label status, status2;
    @FXML
    private TextField username, newusername, serverip;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField newpassword, newpassword1;

    public void transferdatatonew(Socket s, Socket cs, Socket ds, Socket us, DataOutputStream dout, DataInputStream din, Label status) {
        this.s = s; //server socket
        this.dout = dout;
        this.din = din;
        this.status = status;
        this.cs = cs; // chat socket
        this.ds = ds; // download socket
        this.us = us; // upload socket
    }

    public String encrypt(String message, PublicKey publicKey) {
        Cipher encryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
            System.out.println(encodedMessage);
            return encodedMessage;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String encryptedmessage, Key privateKey) {
        Cipher decryptCipher;
        String decryptedMessage;
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedmessage);

        try {
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
            decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return decryptedMessage;
    }

    public byte[] decrypt(byte[] encryptedmessage, Key privateKey) {
        Cipher decryptCipher;
        String decryptedMessage;
        byte[] encryptedMessageBytes = Base64.getDecoder().decode(encryptedmessage);
        byte[] decryptedMessageBytes;
        try {
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return decryptedMessageBytes;
    }
    public Socket getSocket() {
        return s;
    }

    public Socket getChatSocket() {
        return cs;
    }

    public Socket getDownloadSocket() {
        return ds;
    }

    public Socket getUploadSocket() {
        return us;
    }

    public String getusername() {
        return usernamestr;
    }

    public void createnewaccount() {
        Stage stage = (Stage) status2.getScene().getWindow();
        try {
            Pattern P1 = Pattern.compile("([$-/:-?{-~!\"^_`\\[\\]])[A-Za-z\\d]*");
            Pattern P2 = Pattern.compile("[A-Za-z\\d]*[$-/:-?{-~!\"^_`\\[\\]]");
            Pattern P3 = Pattern.compile("[A-Za-z\\d]*[$-/:-?{-~!\"^_`\\[\\]][A-Za-z\\d]*");

            if (newpassword.getText().equals("") || newusername.getText().equals("") || newpassword1.getText().equals("")) {
                status.setText("All fields must be non empty");
                return;
            }
            String newusernamestr = newusername.getText().trim();
            String newpasswordstr = newpassword.getText().trim();
            if (P1.matcher(newusernamestr).matches() || P2.matcher(newusernamestr).matches() || P3.matcher(newusernamestr).matches()) {
                status.setText("no special characters allowed");
                return;
            }
            if (P1.matcher(newpasswordstr).matches() || P2.matcher(newpasswordstr).matches() || P3.matcher(newpasswordstr).matches()) {
                status.setText("no special characters allowed");
                return;
            }
            if (newpassword.getText().equals(newpassword1.getText())) {

                dout.writeUTF(aes.encrypt("%newaccount%"));
                dout.flush();

                dout.writeUTF(aes.encrypt(newusernamestr));
                dout.writeUTF(aes.encrypt(newpasswordstr));
                dout.flush();
                newusername.clear();
                newpassword.clear();
                newpassword1.clear();
                String res = aes.decrypt(din.readUTF());
                if (res.equals("exists")) {
                    status.setText("Account creation failed, username already exists :(");
                    stage.close();
                    return;
                } else if (res.equals("account created"))
                    status.setText("Account creation successful.");
            } else
                status.setText("Passwords do not match");
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void digitalsignature(rsa rsaobj, Key Publickey) throws IOException {
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        DataInputStream din = new DataInputStream(s.getInputStream());
        int numberofhashes = Integer.parseInt(decrypt(din.readUTF(), rsaobj.privateKey));
        int i, j;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringBuilder hash;
        byte[][] publicencryptedbytesarray = new byte[numberofhashes][];
        for (i = 0; i < numberofhashes; i++) {
            publicencryptedbytesarray[i] = new byte[din.readInt()];
            System.out.println(publicencryptedbytesarray[i].length);
            dout.writeUTF("ACK");

            din.readFully(publicencryptedbytesarray[i]);
            dout.writeUTF("ACK");
            hash = new StringBuilder();
            for (byte x : publicencryptedbytesarray[i]) {
                hash.append(String.format("%02x", x));
            }
            System.out.println(hash);
            outputStream.write(decrypt(publicencryptedbytesarray[i], rsaobj.privateKey));
            System.out.println(i);
        }
        byte[] c = outputStream.toByteArray();

        String nope = new String(c, StandardCharsets.UTF_8);
        nope = decrypt(nope, Publickey);
        System.out.println(nope);
        byte[] hashfinal = decrypt(c, Publickey);
        System.out.println(new String(hashfinal, StandardCharsets.UTF_8));

    }
    public void newuser() {
        Stage newaccountcreator = new Stage();

        try {
            String server_ip_string = serverip.getText().trim().split(":")[0];
            int server_port = Integer.parseInt(serverip.getText().trim().split(":")[1]);
            s = new Socket(server_ip_string, server_port);
            System.out.println(s.getLocalPort());

            cs = new Socket(server_ip_string, server_port);
            System.out.println(cs.getLocalPort());

            ds = new Socket(server_ip_string, server_port);
            System.out.println(ds.getLocalPort());

            us = new Socket(server_ip_string, server_port);
            System.out.println(us.getLocalPort());

        } catch (Exception e) {
            status2.setText("Server not running at that ip");
            return;
        }


        try {
            dout = new DataOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newaccountdialog.fxml"));

            Parent root = loader.load();
            Scene newaccount_scene = new Scene(root);
            newaccountcreator.setScene(newaccount_scene);
            LoginController newlc = loader.getController();
            newlc.transferdatatonew(s, cs, ds, us, dout, din, status);
            newaccountcreator.setOnCloseRequest(windowEvent -> {
                newaccountcreator.close();
                System.gc();
            });
            newaccountcreator.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public void Login() throws IOException {
        if (serverip.getText().equals("")) {
            status.setText("pls enter the ip address");
            return;
        }
        try {
            String server_ip_string = serverip.getText().trim().split(":")[0];
            int server_port = Integer.parseInt(serverip.getText().trim().split(":")[1]);

            System.out.println(server_ip_string);
            System.out.println(server_port);
            s = new Socket(server_ip_string, server_port);
            System.out.println(s.getLocalPort());
            cs = new Socket(server_ip_string, server_port);
            System.out.println(cs.getLocalPort());
            ds = new Socket(server_ip_string, server_port);
            System.out.println(ds.getLocalPort());
            us = new Socket(server_ip_string, server_port);
            System.out.println(us.getLocalPort());
        } catch (SocketException e) {
            status.setText("Server not running at that ip");
            return;
        }
        String response;
        try {

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            DataInputStream din = new DataInputStream(s.getInputStream());
            rsa rsaobj = new rsa();
            rsaobj.getPublickey();
            rsaobj.getPrivatekey();
            File publicKeyFile = new File("public.key");
            byte[] publicKeyBytes;

            int keylength;
            keylength = din.readInt();
            byte[] publickeyBytes = new byte[keylength];
            din.readFully(publickeyBytes);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publickeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey serverpublickey = keyFactory.generatePublic(publicKeySpec);
            System.out.println("public key received");
            publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            dout.writeInt(publicKeyBytes.length);
            dout.flush();
            dout.write(publicKeyBytes);
            System.out.println("sent public key");
            dout.flush();
            response = decrypt(din.readUTF(), rsaobj.privateKey);
            System.out.println(response);
            System.out.println(response.length());

            aes.encryptionKey = response;
            System.out.println("received aes key");
            digitalsignature(rsaobj, serverpublickey);
            if (username.getText().equals("") || password.getText().equals("")) {
                status.setText("Username,Password should be non empty");
                dout.writeUTF(aes.encrypt("%exit%"));
                s = null;
                return;
            }
            usernamestr = username.getText().trim();
            dout.writeUTF(aes.encrypt((usernamestr + " " + password.getText().trim())));
            dout.flush();

            System.out.println("sent user pass");
            response = aes.decrypt(din.readUTF());
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
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
