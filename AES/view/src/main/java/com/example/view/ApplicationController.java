package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ApplicationController {

    private AES aes = new AES();
    private FileChooser fileChooser = new FileChooser();



    @FXML
    private TextArea keyField;

    public ApplicationController() throws NoSuchAlgorithmException {
    }




    @FXML
    protected void generateKey128() throws NoSuchAlgorithmException {
        byte[] temp = aes.generateKey(128);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }
    @FXML
    protected void generateKey192() throws NoSuchAlgorithmException {
        byte[] temp = aes.generateKey(192);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }
    @FXML
    protected void generateKey256() throws NoSuchAlgorithmException {
        byte[] temp = aes.generateKey(256);
        String base64String = Base64.getEncoder().encodeToString(temp);
        keyField.setText(base64String);
    }

    @FXML
    public void encryptBinaryFile() throws Exception {
        //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        String sciezka = file.getPath();
        byte[] content = Files.readAllBytes(Paths.get(sciezka));
        String keyHelper = keyField.getText();
        byte[] key = Base64.getDecoder().decode(keyHelper);

        byte[] encryptedMessage = aes.encryptMessage(content,key);
        File file2 = fileChooser.showOpenDialog(null);
        OutputStream outputStream = new FileOutputStream(file2.getPath());
        outputStream.write(encryptedMessage, 0,encryptedMessage.length);
        outputStream.close();
        System.out.println("AAAAAAAES");
    }

    @FXML
    public void decryptBinaryFile() throws Exception {
        AES szyfr =  new AES();
        String key = keyField.getText();
        byte[] keyInBytes = Base64.getDecoder().decode(key);

        File file = fileChooser.showOpenDialog(null);
        String sciezka = file.getPath();
        byte[] encryptedFromFile = Files.readAllBytes(Paths.get(sciezka));
        byte[] decryptedMessage = aes.decryptMessage(encryptedFromFile,keyInBytes);

        File file1 = fileChooser.showOpenDialog(null);

        OutputStream outputStream2 = new FileOutputStream(file1.getPath());
        outputStream2.write(decryptedMessage, 0,decryptedMessage.length);
        outputStream2.close();
    }

}