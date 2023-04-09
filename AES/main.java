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

    public static void main(String[] args) {
        byte[][] array = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 10, 11},
                {12, 13, 14, 15}
        };


    AES szyfr =  new AES();
    MixColumns dupa = new MixColumns();
    main.wyswietl(array);
    dupa.mixColumns(array,true);
    main.wyswietl(array);
    dupa.mixColumns(array,false);
    main.wyswietl(array);

}
}
