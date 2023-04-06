import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    public byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        key.init(128,random);
        SecretKey secretKey = key.generateKey();
        byte [] key_in_bytes = secretKey.getEncoded();
        return key_in_bytes;
    }
}
