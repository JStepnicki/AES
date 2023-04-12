package com.example.view;

public class main {
    public static void wyswietl2(byte[] array){
        System.out.println();
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }


    public static void main(String[] args) throws Exception {
       // algorithm.input.manualInput();
        input.fileInput();


    }
}
