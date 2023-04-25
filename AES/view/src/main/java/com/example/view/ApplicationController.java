package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WARNING");
        alert.setHeaderText(null);
        alert.setContentText("You are going to choose which file you want to encrypt. Make sure you have your key saved. If you lose it you will not be able to decrypt the file.");


        alert.showAndWait();
        File file = fileChooser.showOpenDialog(null);
        String sciezka = file.getPath();
        byte[] content = Files.readAllBytes(Paths.get(sciezka));
        String keyHelper = keyField.getText();
        byte[] key = Base64.getDecoder().decode(keyHelper);
        byte[] encryptedMessage;
        try {
            encryptedMessage = aes.encryptMessage(content, key);
        } catch (Exception e) {
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Done!");
            alert3.setHeaderText(null);
            alert3.setContentText("The key was invalid");
            alert3.showAndWait();
            return;
        }
        OutputStream outputStream = new FileOutputStream(file.getPath());
        outputStream.write(encryptedMessage, 0, encryptedMessage.length);
        outputStream.close();
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert2.setTitle("Done!");
        alert2.setHeaderText(null);
        alert2.setContentText("The file has been encrypted. Remember to save your key!");
        alert2.showAndWait();
    }

    @FXML
    public void decryptBinaryFile() throws Exception {
        String key = keyField.getText();
        byte[] keyInBytes = Base64.getDecoder().decode(key);


        File file = fileChooser.showOpenDialog(null);
        String sciezka = file.getPath();
        byte[] encryptedFromFile = Files.readAllBytes(Paths.get(sciezka));
        byte[] decryptedMessage;
        try{
            decryptedMessage = aes.decryptMessage(encryptedFromFile,keyInBytes);
        }
        catch(Exception e){
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION);
            alert3.setTitle("Done!");
            alert3.setHeaderText(null);
            alert3.setContentText("The key was invalid");
            alert3.showAndWait();
            return;
        }


        OutputStream outputStream2 = new FileOutputStream(file.getPath());
        outputStream2.write(decryptedMessage, 0,decryptedMessage.length);
        outputStream2.close();
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert2.setTitle("Done!");
        alert2.setHeaderText(null);
        alert2.setContentText("The file has been decrypted.");
        alert2.showAndWait();
    }

}