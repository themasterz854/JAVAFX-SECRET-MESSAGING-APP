package sample;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


public class AES {
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_IV_LENGTH = 16;
    public static final int GCM_TAG_LENGTH = 16;
    protected String encryptionKey;

    public AES() {
        encryptionKey = "";
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public byte[] encrypt(byte[] plaintext) {
        System.out.println("encrypt byte executing");
        //Generate IV
        try {
            byte[] IV = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();                                                        // Get Cipher Instance
            random.nextBytes(IV);

            System.out.println("IV generated : " + Base64.getEncoder().withoutPadding().encodeToString(IV));
            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            // Initialize Cipher for ENCRYPT_MODE
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            // Perform Encryption
            byte[] CipherText = cipher.doFinal(plaintext);
            byte[] CipherTextFinal = new byte[GCM_IV_LENGTH + CipherText.length];
            System.arraycopy(IV, 0, CipherTextFinal, 0, GCM_TAG_LENGTH);
            System.arraycopy(CipherText, 0, CipherTextFinal, GCM_IV_LENGTH, CipherText.length);
            System.out.println("Cipher text generated " + Base64.getEncoder().withoutPadding().encodeToString(CipherTextFinal));
            return CipherTextFinal;
        } catch (Exception E) {
            System.err.println("byte Encrypt exception" + E.getMessage());
        }
        System.out.println("NULL");
        return null;
    }

    public String encrypt(String plaintext) throws Exception {
        System.out.println("encrypt String executing");
        //Generate IV
        try {
            byte[] IV = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();                                                        // Get Cipher Instance
            random.nextBytes(IV);

            System.out.println("IV generated : " + Base64.getEncoder().withoutPadding().encodeToString(IV));
            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");
            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);
            // Initialize Cipher for ENCRYPT_MODE
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);
            // Perform Encryption
            byte[] CipherText = cipher.doFinal(plaintext.getBytes());
            byte[] CipherTextFinal = new byte[GCM_IV_LENGTH + CipherText.length];
            System.arraycopy(IV, 0, CipherTextFinal, 0, GCM_TAG_LENGTH);
            System.arraycopy(CipherText, 0, CipherTextFinal, GCM_IV_LENGTH, CipherText.length);
            System.out.println("Plain text " + plaintext);
            System.out.println("Cipher text generated " + Base64.getEncoder().withoutPadding().encodeToString(CipherTextFinal));
            System.out.println("decrypt in encrypt " + this.decrypt(Base64.getEncoder().withoutPadding().encodeToString(CipherTextFinal)));
            System.out.println("decrypt done");
            return Base64.getEncoder().withoutPadding().encodeToString(CipherTextFinal);
        } catch (Exception E) {
            System.err.println("String Encrypt Exception : " + E.getMessage());
        }
        System.out.println("NULL");
        return null;
    }

    public byte[] decrypt(byte[] cipherTextString) {
        System.out.println("decrypt byte executing");
        try {
            byte[] IV = new byte[GCM_IV_LENGTH];
            System.arraycopy(cipherTextString, 0, IV, 0, GCM_IV_LENGTH);
            System.out.println("IV " + Base64.getEncoder().withoutPadding().encodeToString(IV));
            System.out.println("cipher text received : " + Base64.getEncoder().withoutPadding().encodeToString(cipherTextString));

            byte[] ciphertextonly = new byte[cipherTextString.length - GCM_IV_LENGTH];
            System.arraycopy(cipherTextString, GCM_IV_LENGTH, ciphertextonly, 0, cipherTextString.length - GCM_IV_LENGTH);
            // Create SecretKeySpec

            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for DECRYPT_MODE
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);
            return cipher.doFinal(ciphertextonly);
        } catch (Exception E) {
            System.err.println("byte Decrypt Exception : " + E.getMessage());
        }
        return null;
    }


    public String decrypt(String cipherTextString) {
        // Get Cipher Instance
        System.out.println("decrypt String executing");
        byte[] decryptedText;
        try {
            byte[] IV = new byte[GCM_IV_LENGTH];
            byte[] cipherTextStringbytes = Base64.getDecoder().decode(cipherTextString);
            System.out.println("cipher byte length " + cipherTextStringbytes.length);
            System.arraycopy(cipherTextStringbytes, 0, IV, 0, GCM_IV_LENGTH);
            System.out.println("IV " + Base64.getEncoder().withoutPadding().encodeToString(IV));
            System.out.println("cipher text received : " + cipherTextString);

            byte[] ciphertextonly = new byte[cipherTextStringbytes.length - GCM_IV_LENGTH];
            System.arraycopy(cipherTextStringbytes, GCM_IV_LENGTH, ciphertextonly, 0, cipherTextStringbytes.length - GCM_IV_LENGTH);
            // Create SecretKeySpec

            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(encryptionKey), "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, IV);

            // Initialize Cipher for DECRYPT_MODE
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Decryption
            decryptedText = cipher.doFinal(ciphertextonly);
            System.out.println("Decrypted text " + new String(decryptedText));
            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception E) {
            System.err.println("string Decrypt Exception : " + E.getMessage());
        }
        System.out.println("NULL");
        return null;
    }
}
