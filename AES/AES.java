import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    private final int Nb = 4; //Nb - liczba kolumn w macierzy stanu zawsze wynosi 4.
    private int Nk;  //Nk - liczba 32-bitowych słów w kluczu; wynosi 4 dla 128-bitowego klucza, 6 dla 192-bitowego , a 8 dla 256-bitowego .
    private int Nr; //Nr - liczba rund (iteracji),10/12/14 zaleznie od dlugosci klucza
    private byte[][] mainKey; // klucze do rund


    public byte[] encryptMessage(byte[] message, byte[] encryptionKey) throws Exception{
        if(!validateKey(encryptionKey)){
            throw new Exception("nieprawidłowy klucz");
        }
        if (message.length == 0) {
            throw new Exception("Nie podano danych do odszyfrowania.");
        }
        Nk = encryptionKey.length / 4;
        Nr = Nk + 6;
        int msgLength;
        int numberOfBlocks = message.length / 16;
        if (numberOfBlocks == 0)
            msgLength = 16;
        else if ((message.length % 16) != 0)
            msgLength = (numberOfBlocks + 1) * 16;

        else msgLength = numberOfBlocks * 16;
        byte[] result = new byte[msgLength];
        byte[] temp = new byte[msgLength];
        byte[] blok = new byte[16];
        mainKey = keyExpansionRoutine(encryptionKey);
        for (int i = 0; i < msgLength; i++) {
            if (i < message.length)
                temp[i] = message[i];
            else temp[i] = 0;
        }
        for (int k = 0; k < temp.length; ){
            for (int j = 0; j < 16; j++)
                blok[j] = temp[k++];
            blok = encryptSingleBlock(blok);
            System.arraycopy(blok, 0, result, k - 16, blok.length);
        }
        return result;
    }


    public byte[] decryptMessage(byte[] message, byte[] decryptionKey) throws Exception {
        if(!validateKey(decryptionKey)){
            throw new Exception("nieprawidłowy klucz");
        }
        if (message.length == 0) {
            throw new Exception("Nie podano danych do odszyfrowania.");
        }
        byte[] tmpResult = new byte[message.length];
        byte[] blok = new byte[16];
        mainKey = keyExpansionRoutine(decryptionKey);
        Nk = decryptionKey.length / 4;
        Nr = Nk + 6;
        for (int i = 0; i < message.length; ) {
            for (int j = 0; j < 16; j++) blok[j] = message[i++];
            blok = decryptSingleBlock(blok);
            System.arraycopy(blok, 0, tmpResult, i - 16, blok.length);
        }
        int cnt = 0;
        int i = tmpResult.length - 1;
        while (i >= 0 && tmpResult[i] == 0) {
            cnt++;
            i--;
        }
        byte[] result = new byte[tmpResult.length - cnt];
        System.arraycopy(tmpResult, 0, result, 0, tmpResult.length - cnt);
        return result;
    }


    public byte[] encryptSingleBlock(byte[] message) {
        byte[] result = new byte[16];
        byte[][] stateMatrix = new byte[4][4];
        for (int i = 0; i < 16; i++)
            stateMatrix[i / 4][i % 4] = message[i];
        stateMatrix = addRoundKey(stateMatrix, mainKey, 0);
        for (int i = 1; i < Nr; i++) {
            stateMatrix = subBytes(stateMatrix, true);
            stateMatrix = shiftRows(stateMatrix, true);
            stateMatrix = mixColumns(stateMatrix, true);
            stateMatrix = addRoundKey(stateMatrix, mainKey, i);
        }
        stateMatrix = subBytes(stateMatrix, true);
        stateMatrix = shiftRows(stateMatrix, true);
        stateMatrix = addRoundKey(stateMatrix, mainKey, Nr);
        for (int i = 0; i < 16; i++)
            result[i] = stateMatrix[i / 4][i % 4];
        return result;
    }

    public byte[] decryptSingleBlock(byte[] message) {
        byte[] result = new byte[16];
        byte[][] stateMatrix = new byte[4][4];
        for (int i = 0; i < 16; i++)
            stateMatrix[i / 4][i % 4] = message[i];
        stateMatrix = addRoundKey(stateMatrix, mainKey, Nr);
        for (int i = Nr - 1; i > 0; i--) {
            stateMatrix = subBytes(stateMatrix, false);
            stateMatrix = shiftRows(stateMatrix, false);
            stateMatrix = addRoundKey(stateMatrix, mainKey, i);
            stateMatrix = mixColumns(stateMatrix, false);
        }
        stateMatrix = subBytes(stateMatrix, false);
        stateMatrix = shiftRows(stateMatrix, false);
        stateMatrix = addRoundKey(stateMatrix, mainKey, 0);
        for (int i = 0; i < 16; i++) {
            result[i] = stateMatrix[i/4][i % 4];
        }
        return result;
    }

    public byte[][] keyExpansionRoutine(byte[] inputKey) {
        byte[][] resultKeys = new byte[Nb * (Nr + 1)][4];
        int iterator = 0;

        // Pętla uzupełniająca pierwsze Nk (tj. numberOfKey32BitBlocks) wierszy kluczem podanym przez użytkownika
        while (iterator < Nk) {
            resultKeys[iterator][0] = inputKey[4 * iterator];
            resultKeys[iterator][1] = inputKey[4 * iterator + 1];
            resultKeys[iterator][2] = inputKey[4 * iterator + 2];
            resultKeys[iterator][3] = inputKey[4 * iterator + 3];
            iterator++;
        }

        iterator = Nk;
        byte[] temp = new byte[4];
        // Pętla uzupełniająca kolejne wiersze odpowiednimi wartościami
        while (iterator < Nb * (Nr + 1)) {
            temp[0] = resultKeys[iterator - 1][0];
            temp[1] = resultKeys[iterator - 1][1];
            temp[2] = resultKeys[iterator - 1][2];
            temp[3] = resultKeys[iterator - 1][3];

            if (iterator % Nk == 0) {
                byte[] subWordByteArray = SubWord(RotWord(temp));
                byte[] roundConstant = Rcon(iterator / Nk);
                temp[0] = (byte) (subWordByteArray[0] ^ roundConstant[0]);
                temp[1] = (byte) (subWordByteArray[1] ^ roundConstant[1]);
                temp[2] = (byte) (subWordByteArray[2] ^ roundConstant[2]);
                temp[3] = (byte) (subWordByteArray[3] ^ roundConstant[3]);
            } else if (Nk > 6 && (iterator % Nk) == 0) {
                temp = SubWord(temp);
            }

            resultKeys[iterator][0] = (byte) (resultKeys[iterator - Nk][0] ^ temp[0]);
            resultKeys[iterator][1] = (byte) (resultKeys[iterator - Nk][1] ^ temp[1]);
            resultKeys[iterator][2] = (byte) (resultKeys[iterator - Nk][2] ^ temp[2]);
            resultKeys[iterator][3] = (byte) (resultKeys[iterator - Nk][3] ^ temp[3]);

            iterator++;
        }
        return resultKeys;
    }

    public byte[] RotWord(byte[] inputByteArray) {
        byte[] output = new byte[inputByteArray.length];
        for (int i = 0; i < inputByteArray.length; i++) {
            output[i] = inputByteArray[(i + 1) % inputByteArray.length];
        }
        return output;
    }

    public byte[] SubWord(byte[] inputByteArray) {
        byte[] output = new byte[inputByteArray.length];
        for (int i = 0; i < inputByteArray.length; i++) {
            output[i] = translate((inputByteArray[i]), true);
        }
        return output;
    }

    public byte[] Rcon(int roundNumber) {
        int roundCoefficient = 0;
        if (roundNumber > 0) {
            roundCoefficient = 1;
            for (int i = 1; i < roundNumber; i++) {
                if (roundCoefficient >= 0x80) {
                    roundCoefficient = (2 * roundCoefficient) ^ 0x11B;
                } else {
                    roundCoefficient *= 2;
                }
            }
        }
        byte[] rCon = {(byte) roundCoefficient, 0, 0, 0};
        return rCon;
    }


    public byte[][] addRoundKey(byte[][] state, byte[][] key, int roundNumber) {
        byte[][] temp = new byte[state.length][state[0].length];
        for (int i = 0; i < Nb; i++) {
            for (int j = 0; j < 4; j++) {
                temp[j][i] = (byte) (state[j][i] ^ key[roundNumber * Nb + i][j]);
            }
        }
        return temp;
    }


    public boolean validateKey(byte[] inputKey) {
        if (inputKey == null)
            return false;
        return inputKey.length == 16 || inputKey.length == 24 || inputKey.length == 32;
    }

    public byte[] generateKey(int size) throws NoSuchAlgorithmException {
        KeyGenerator key = KeyGenerator.getInstance("AES");
        SecureRandom random = new SecureRandom();
        key.init(size, random);
        SecretKey secretKey = key.generateKey();
        byte[] key_in_bytes = secretKey.getEncoded();
        return key_in_bytes;
    }


    public byte[][] shiftRows(byte[][] state, boolean encryption) {
        byte[] temp = new byte[4];
        for (int i = 1; i < 4; i++) {
            // kopiujemy wiersz do przenoszenia
            System.arraycopy(state[i], 0, temp, 0, 4);
            for (int j = 0; j < 4; j++) {
                if (encryption)//
                    state[i][j] = temp[((i + j) % 4)]; //przesuwamy cale wiersze o i miejsc w lewo
                else
                    state[i][j] = temp[(4 + j - i) % 4];//przesuwamy cale wiersze o i miejsc w prawo
            }
        }
        return state;
    }


    public byte[][] mixColumns(byte[][] state, boolean encryption) {
        int[] temp = new int[4];
        if (encryption) {
            for (int i = 0; i < 4; i++) {
                temp[0] = multiply(Matrix[0][0], state[0][i]) ^ multiply(Matrix[0][1], state[1][i]) ^
                        multiply(Matrix[0][2], state[2][i]) ^ multiply(Matrix[0][3], state[3][i]);
                temp[1] = multiply(Matrix[1][0], state[0][i]) ^ multiply(Matrix[1][1], state[1][i]) ^
                        multiply(Matrix[1][2], state[2][i]) ^ multiply(Matrix[1][3], state[3][i]);
                temp[2] = multiply(Matrix[2][0], state[0][i]) ^ multiply(Matrix[2][1], state[1][i]) ^
                        multiply(Matrix[2][2], state[2][i]) ^ multiply(Matrix[2][3], state[3][i]);
                temp[3] = multiply(Matrix[3][0], state[0][i]) ^ multiply(Matrix[3][1], state[1][i]) ^
                        multiply(Matrix[3][2], state[2][i]) ^ multiply(Matrix[3][3], state[3][i]);
                for (int j = 0; j < 4; j++) {
                    state[j][i] = (byte) temp[j];
                }
            }
        } else {
            for (int i = 0; i < 4; i++) {
                temp[0] = multiply(invMatrix[0][0], state[0][i]) ^ multiply(invMatrix[0][1], state[1][i]) ^
                        multiply(invMatrix[0][2], state[2][i]) ^ multiply(invMatrix[0][3], state[3][i]);
                temp[1] = multiply(invMatrix[1][0], state[0][i]) ^ multiply(invMatrix[1][1], state[1][i]) ^
                        multiply(invMatrix[1][2], state[2][i]) ^ multiply(invMatrix[1][3], state[3][i]);
                temp[2] = multiply(invMatrix[2][0], state[0][i]) ^ multiply(invMatrix[2][1], state[1][i]) ^
                        multiply(invMatrix[2][2], state[2][i]) ^ multiply(invMatrix[2][3], state[3][i]);
                temp[3] = multiply(invMatrix[3][0], state[0][i]) ^ multiply(invMatrix[3][1], state[1][i]) ^
                        multiply(invMatrix[3][2], state[2][i]) ^ multiply(invMatrix[3][3], state[3][i]);
                for (int j = 0; j < 4; j++) {
                    state[j][i] = (byte) temp[j];
                }
            }
        }
        return state;
    }

    public byte multiply(byte firstByte, byte secondByte) {
        byte result = 0;
        byte temp;
        while (firstByte != 0) {
            if ((firstByte & 1) != 0) {
                result = (byte) (result ^ secondByte);
            }
            temp = (byte) (secondByte & 0x80);
            secondByte = (byte) (secondByte << 1);
            if (temp != 0) {
                secondByte = (byte) (secondByte ^ 0x1B);
            }
            firstByte = (byte) ((firstByte & 0xFF) >> 1);
        }
        return result;
    }


    public byte[][] subBytes(byte[][] state, boolean encryption) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                if (encryption)
                    state[i][j] = translate(state[i][j], true);
                else
                    state[i][j] = translate(state[i][j], false);
        return state;
    }

    static byte translate(byte b, boolean encryption) { // dajemy bajt  z 8 bitow
        int x = (b & 0b11110000) >> 4; // 4 bity starsze to numer wiersza (te z lewej)
        int y = b & 0b00001111; // 4 mlodsze bity to numer kolumny  (te z prawej)
        if (encryption)
            return (byte) sBox[x][y];
        else
            return (byte) inv_sbox[x][y];
    }

    static int[][] sBox = {
            {0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76},
            {0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0},
            {0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15},
            {0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75},
            {0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84},
            {0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf},
            {0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8},
            {0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2},
            {0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73},
            {0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb},
            {0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79},
            {0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08},
            {0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a},
            {0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e},
            {0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf},
            {0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16}};

    static int[][] inv_sbox = new int[][]{
            {0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb},
            {0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb},
            {0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e},
            {0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25},
            {0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92},
            {0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84},
            {0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06},
            {0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b},
            {0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73},
            {0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e},
            {0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b},
            {0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4},
            {0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f},
            {0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef},
            {0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61},
            {0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d}
    };


    private final byte[][] Matrix = {
            {0x02, 0x03, 0x01, 0x01},
            {0x01, 0x02, 0x03, 0x01},
            {0x01, 0x01, 0x02, 0x03},
            {0x03, 0x01, 0x01, 0x02}
    };

    private final byte[][] invMatrix = {
            {0x0E, 0x0B, 0x0D, 0x09},
            {0x09, 0x0E, 0x0B, 0x0D},
            {0x0D, 0x09, 0x0E, 0x0B},
            {0x0B, 0x0D, 0x09, 0x0E}
    };
}
