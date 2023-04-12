import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class fileInput {

    private final String fileName;
    private static final byte[] HEX_LETTERS = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8);

    public fileInput(String fileName) {
        this.fileName = fileName;
    }

    public byte[] readBytesFromFile()throws Exception{
        byte[] inputFromFile = null;
        try (FileInputStream fileInput = new FileInputStream(fileName)) {
            inputFromFile = fileInput.readAllBytes();
        }
        catch (Exception exc){
            throw new Exception("Nie powiodło się zczytanie tekstu jawnego z pliku.");
        }
        return inputFromFile;
    }

    public void saveBytesToFile(byte[] cipherText) throws Exception {
        try (FileOutputStream fileOutput = new FileOutputStream(fileName)) {
            fileOutput.write(cipherText);
        }
        catch (Exception Exc) {
            throw new Exception("Nie powiodło się zapisanie szyfrogramu do pliku.");
        }
    }

    public static byte[] convertByteArrayToHex(byte[] inputByteArray) {
        byte[] hexCharArray = new byte[inputByteArray.length * 2];
        for (int i = 0; i < inputByteArray.length; i++) {
            int value = inputByteArray[i] & 0xFF;
            hexCharArray[2 * i] = HEX_LETTERS[value >> 4];
            hexCharArray[2 * i + 1] = HEX_LETTERS[value & 0xF];
        }
        return hexCharArray;
    }

    public static byte[] convertHexToByteArray(byte[] inputHexArray) {
        byte[] messageByteArray = inputHexArray;
        byte[] resultByteArray = new byte[inputHexArray.length / 2];
        for (int i = 0; i < inputHexArray.length; i += 2) {
            resultByteArray[i / 2] = (byte) ((digitValue(messageByteArray[i]) << 4) + digitValue(messageByteArray[i + 1]));
        }
        return resultByteArray;
    }

    private static int digitValue(byte value) {
        for (int i = 0; i < HEX_LETTERS.length; i++) {
            if (value == HEX_LETTERS[i]) {
                return i;
            }
        }
        return -1;
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
}
