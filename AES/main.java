import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class main {
    public static void wyswietl2(byte[] array){
        System.out.println();
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }


    public static void main(String[] args) throws Exception {
        fileInput.manualInput();


        //String path = "D:/test.txt";
      //  fileInput dupa =new fileInput(path);
      //  byte[] fileInputPlainText = dupa.readBytesFromFile();

       // fileInputPlainText = szyfr.encryptMessage(fileInputPlainText, szyfr.generateKey(256));
        //fileInputPlainText = szyfr.decryptMessage(fileInputPlainText, szyfr.generateKey(256));
        //String plainText = new String(dupa.convertByteArrayToHex(fileInputPlainText));
        //System.out.println(plainText);
}
}
