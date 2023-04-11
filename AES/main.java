import java.security.NoSuchAlgorithmException;

public class main {
    public static void wyswietl(byte[][] array){
        System.out.println();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void wyswietl2(byte[] array){
        System.out.println();
        for (int i = 0; i < array.length; i++) {
                System.out.print(array[i] + " ");
            }

        }


    public static void main(String[] args) throws Exception {
        byte[][] array = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 10, 11},
                {12, 13, 14, 15}
        };
        byte[] message = {
                (byte) 0x32, (byte) 0x43, (byte) 0xF6, (byte) 0xA8,
                (byte) 0x88, (byte) 0x5A, (byte) 0x30, (byte) 0x8D,
                (byte) 0x21, (byte) 0x31, (byte) 0x98, (byte) 0xA2,
                (byte) 0xE0, (byte) 0x37, (byte) 0x07, (byte) 0x34,
                (byte) 0x32, (byte) 0x43, (byte) 0xF6, (byte) 0xA8,
                (byte) 0x88, (byte) 0x5A, (byte) 0x30, (byte) 0x8D,
                (byte) 0x31, (byte) 0x31, (byte) 0x98, (byte) 0xA2,
                (byte) 0xE0, (byte) 0x37, (byte) 0x07, (byte) 0x34
        };

        byte[] key = {
                (byte) 0x2B, (byte) 0x7E, (byte) 0x15, (byte) 0x16,
                (byte) 0x28, (byte) 0xAE, (byte) 0xD2, (byte) 0xA6,
                (byte) 0xAB, (byte) 0xF7, (byte) 0x15, (byte) 0x88,
                (byte) 0x09, (byte) 0xCF, (byte) 0x4F, (byte) 0x3C,
                (byte) 0x2B, (byte) 0x7E, (byte) 0x15, (byte) 0x16,
                (byte) 0x28, (byte) 0xAE, (byte) 0xD2, (byte) 0xA6
        };

        byte[] message2 = new byte[16];
        byte[] message3 = new byte[16];

    AES szyfr =  new AES();
    key = szyfr.generateKey(256);

        wyswietl2(message);
        message2 = szyfr.encryptMessage(message,key);
    wyswietl2(message2);
        message3 =szyfr.decryptMessage(message2,key);
    wyswietl2(message3);

}
}
