import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    int Nb= 4;
    public byte[] generateKey() throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        key.init(128,random);
        SecretKey secretKey = key.generateKey();
        byte [] key_in_bytes = secretKey.getEncoded();
        return key_in_bytes;
    }

    public byte[][] subBytes(byte[][] state,boolean encryption) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < Nb; j++)
                if(encryption)
                    state[i][j] = sBox.translate(state[i][j]);
                else
                    state[i][j] = sBox.translateReverse(state[i][j]);
        return state;
    }

    public byte[][] ShiftRows (byte[][] state,boolean encryption) {
        byte[] temp = new byte[Nb];
        for (int i = 1; i < 4; i++){
            for (int t = 0; t < Nb; t++)
                temp[t] = state[i][t];// kopiujemy wiersz do przenoszenia
            for (int j = 0; j < Nb; j++){

                if(encryption)//
                    state[i][j] = temp[((i + j)%Nb)]; //przesuwamy cale wiersze o i miejsc w lewo
                else
                    state[i][j] = temp[(Nb+j-i)%Nb];//przesuwamy cale wiersze o i miejsc w prawo
            }
        }
        return state;
    }



}
