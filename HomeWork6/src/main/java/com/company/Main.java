package com.company;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        int[] arrayInt = {1, 2, 4, 4, 2, 3, 4, 1, 7 };
        arrayInt = modifyArray(arrayInt);
        System.out.println(Arrays.toString(arrayInt));

        System.out.println(arrayIncludeOneOrFour(arrayInt));
        System.out.println(arrayIncludeOneOrFour(new int[]{0, 5, 6}));
    }

    public static int[] modifyArray(int [] array) {
        int [] newArray;
        for(int i = array.length - 1; i >= 0; i--){
            if (array[i] == 4){
                newArray = new int[array.length - i - 1];
                for(int j = 0; j < newArray.length; j++){
                    newArray[j] = array[i+j+1];
                }
                return newArray;
            }
        }
        throw new RuntimeException();
    }

    public static boolean arrayIncludeOneOrFour(int [] array){
        for(int elem: array){
            if (elem == 1 || elem == 4){
                return true;
            }
        }
        return false;
    }
}