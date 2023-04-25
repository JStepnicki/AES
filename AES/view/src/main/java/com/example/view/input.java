package com.example.view;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class input {
    public input() throws IOException {}
    public static void fileInput() throws Exception {
        AES szyfr =  new AES();
        String filePath = "message.txt";
        Path path = Paths.get(filePath);
        String content = Files.readString(path, StandardCharsets.UTF_8);
        byte[] mesByte = content.getBytes(StandardCharsets.UTF_8);

        byte[] keyByte = szyfr.generateKey(128);//generowany
        mesByte=szyfr.encryptMessage(mesByte,keyByte);
        System.out.println("Zawartośc pliku: " + content);
        String base64String = Base64.getEncoder().encodeToString(mesByte);
        System.out.println("Zaszyfrowana wiadomość: " + base64String);
        mesByte= szyfr.decryptMessage(mesByte,keyByte);
        System.out.println("Odszyfrowana wiadomość: " + new String(mesByte, StandardCharsets.UTF_8));

        String toSave = "uzyty klucz: " +  Base64.getEncoder().encodeToString(keyByte) + "\n" + "wiadomosc: " +new String(mesByte, StandardCharsets.UTF_8) ;
        String fileName = "output.txt";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(toSave);
            writer.close();
            System.out.println("Zapisano dane do pliku " + fileName);
        } catch (IOException e) {
            System.out.println("Błąd zapisu do pliku " + fileName + ": " + e.getMessage());
        }
    }








    public static void manualInput() throws Exception {
        AES szyfr =  new AES();
        System.out.print("Wpisz tekst do zaszfrowania: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String message = reader.readLine();
        //System.out.print("Podaj klucz do zaszyfrowania wiadomości: ");
        //String encryptionKey = reader.readLine();
        byte[] mesByte = message.getBytes(StandardCharsets.UTF_8);
        //byte[] keyByte = encryptionKey.getBytes(StandardCharsets.UTF_8); //wprowadzany
        byte[] keyByte = szyfr.generateKey(128);//generowany
        mesByte=szyfr.encryptMessage(mesByte,keyByte);
        String base64String = Base64.getEncoder().encodeToString(mesByte);
        System.out.println("Zaszyfrowana wiadomość: " + base64String);
        mesByte= szyfr.decryptMessage(mesByte,keyByte);
        System.out.println("Odszyfrowana wiadomość: " + new String(mesByte, StandardCharsets.UTF_8));
    }

    public static void binaryInput(String sciezka) throws Exception {
        byte[] content = Files.readAllBytes(Paths.get(sciezka));
        AES szyfr =  new AES();
        byte[] key = szyfr.generateKey(192);
        byte[] encryptedMessage = szyfr.encryptMessage(content,key);
        OutputStream outputStream = new FileOutputStream("SZYFROWANY");
        outputStream.write(encryptedMessage, 0,encryptedMessage.length);


        byte[] encryptedFromFile = Files.readAllBytes(Paths.get("SZYFROWANY"));
        byte[] decryptedMessage = szyfr.decryptMessage(encryptedFromFile,key);
        OutputStream outputStream2 = new FileOutputStream("odszyfrowany.exe");
        outputStream2.write(decryptedMessage, 0,decryptedMessage.length);
    }



}
