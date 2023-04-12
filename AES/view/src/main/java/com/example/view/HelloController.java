package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HelloController {

    AES dupa = new AES();

    final byte[] keyInBytes = dupa.generateKey(128);
    @FXML
    private Label welcomeText;

    @FXML
    private TextField keyField;

    @FXML
    private TextArea normalText;

    @FXML
    private TextArea cipheredText;

    public HelloController() throws NoSuchAlgorithmException {
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @FXML
    protected void generateKey128() throws NoSuchAlgorithmException {
        byte[] temp = dupa.generateKey(128);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }
    @FXML
    protected void generateKey192() throws NoSuchAlgorithmException {
        byte[] temp = dupa.generateKey(192);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }
    @FXML
    protected void generateKey256() throws NoSuchAlgorithmException {
        byte[] temp = dupa.generateKey(256);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }

    @FXML
    protected void encrypt() throws Exception {
        String toEncrypt = normalText.getText();
        String key = keyField.getText();
        byte[] messageInBytes = toEncrypt.getBytes(StandardCharsets.UTF_8);
        //byte[] keyInBytes = Base64.getDecoder().decode(key);
        byte[] encryptedMessageInBytes = dupa.encryptMessage(messageInBytes,keyInBytes);
        String encryptedMessage = new String(encryptedMessageInBytes,StandardCharsets.UTF_8);
        cipheredText.setText(encryptedMessage);
    }

    @FXML
    protected void decrypt() throws Exception {
        String toDecrypt = cipheredText.getText();
        String key = keyField.getText();
        byte[] messageInBytes = toDecrypt.getBytes(StandardCharsets.UTF_8);
        //byte[] keyInBytes = Base64.getDecoder().decode(key);
        byte[] decryptedMessageInbytes = dupa.decryptMessage(messageInBytes,keyInBytes);
        String decryptedMessage = new String(decryptedMessageInbytes,StandardCharsets.UTF_8);
        normalText.setText(decryptedMessage);
    }


}