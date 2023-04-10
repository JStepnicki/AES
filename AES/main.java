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

    public static void main(String[] args) throws NoSuchAlgorithmException {
        byte[][] array = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 10, 11},
                {12, 13, 14, 15}
        };


    AES szyfr =  new AES();

    szyfr.ShiftRows(array,true);
    szyfr.subBytes(array,true);
    szyfr.mixColumns(array,true);
    wyswietl(array);

    szyfr.mixColumns(array,false);
    szyfr.subBytes(array,false);
    szyfr.ShiftRows(array,false);
    wyswietl(array);


}
}
