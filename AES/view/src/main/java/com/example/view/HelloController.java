package com.example.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HelloController {

    private AES aes = new AES();
    private FileChooser fileChooser = new FileChooser();

    //final byte[] keyInBytes = dupa.generateKey(128);
    @FXML
    private Label welcomeText;

    @FXML
    private TextArea keyField;

    @FXML
    private TextArea normalText;

    @FXML
    private TextArea cipheredText;

    @FXML
    private javafx.scene.control.Button loadNormalText;

    @FXML
    private javafx.scene.control.Button loadCipheredText;

    @FXML
    private javafx.scene.control.Button saveToFileButton;

    public HelloController() throws NoSuchAlgorithmException {
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
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
    protected void encrypt() throws Exception {
        String toEncrypt = normalText.getText();
        String key = keyField.getText();
        byte[] messageInBytes = toEncrypt.getBytes(StandardCharsets.UTF_8);
        byte[] keyInBytes = Base64.getDecoder().decode(key);
        byte[] encryptedMessageInBytes = Base64.getEncoder().encode(aes.encryptMessage(messageInBytes,keyInBytes));
        String encryptedMessage = new String(encryptedMessageInBytes,StandardCharsets.UTF_8);
        cipheredText.setText(encryptedMessage);
    }

    @FXML
    protected void decrypt() throws Exception {
        String toDecrypt = cipheredText.getText();
        String key = keyField.getText();
        byte[] messageInBytes = Base64.getDecoder().decode(toDecrypt.getBytes(StandardCharsets.UTF_8));
        byte[] keyInBytes = Base64.getDecoder().decode(key);
        byte[] decryptedMessageInbytes = aes.decryptMessage(messageInBytes,keyInBytes);
        String decryptedMessage = new String(decryptedMessageInbytes,StandardCharsets.UTF_8);
        normalText.setText(decryptedMessage);
    }

    @FXML
    protected void readFromFileNormal() throws Exception {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        normalText.setText(fileInputMessage(file.getPath()));
    }

    @FXML
    protected void readFromFileEncrypted() throws Exception {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        cipheredText.setText(fileInputMessage(file.getPath()));
    }

    public  String fileInputMessage(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        String content = Files.readString(path, StandardCharsets.UTF_8);
        return content;
    }

    @FXML
    protected void saveToFile(){
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        saveToFileHelper(file.getPath(),keyField.getText(),cipheredText.getText(),normalText.getText());
    }

    @FXML
    protected void saveEncryptedToFile(){
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        saveEncryptedToFileHelper(file.getPath(),cipheredText.getText());
    }

    public void saveToFileHelper(String filePath,String cipherKey,String encryptedMessage,String decryptedMessage){
        String toSave = "uzyty klucz: " +  cipherKey + "\n" + "wiadomosc po zaszyfrowaniu: " +encryptedMessage + "\n wiadomość po deszyfracji: "+decryptedMessage ;
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(toSave);
            writer.close();
            System.out.println("Zapisano dane do pliku " + filePath);
        } catch (IOException e) {
            System.out.println("Błąd zapisu do pliku " + filePath + ": " + e.getMessage());
        }
    }

    public void saveEncryptedToFileHelper(String filePath,String encryptedMessage){
        String toSave = encryptedMessage;
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(toSave);
            writer.close();
            System.out.println("Zapisano dane do pliku " + filePath);
        } catch (IOException e) {
            System.out.println("Błąd zapisu do pliku " + filePath + ": " + e.getMessage());
        }
    }


}