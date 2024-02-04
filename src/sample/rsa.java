package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class rsa {
    KeyPairGenerator generator;
    KeyPair pair;
    PrivateKey privateKey;
    PublicKey publicKey;
    KeyFactory keyFactory;


    rsa() {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        generator.initialize(2048);
        pair = generator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream("public.key");
            fos.write(publicKey.getEncoded());
            fos.close();
            fos = new FileOutputStream("private.key");
            fos.write(privateKey.getEncoded());
            fos.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        privateKey = null;
        publicKey = null;
        System.out.println("Keys generated");
    }

    public void getPublickey() {
        File publicKeyFile = new File("public.key");
        byte[] publicKeyBytes;
        try {
            publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (IOException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }


    }

    public void getPrivatekey() {
        File privateKeyFile = new File("private.key");
        byte[] privateKeyBytes;
        try {
            privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());

            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);

        } catch (IOException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String encrypt(String message) {
        Cipher encryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] secretMessageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

            return Base64.getEncoder().encodeToString(encryptedMessageBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public String decrypt(String encryptedmessage) {
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

}
