package com.company;

import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.ArrayList;
import java.util.Arrays;

public class Mass {

    public static void main(String[] args) {
        // 1 -  Поменять два элемента массива местами
        Double [] arrDouble = {1.4, 2.4, 3.5, 10.2};
        Integer [] intArray = {6, 31, 8, 52, 15, 9};

        changeArrayElem(arrDouble, 1, 3);
        System.out.println(Arrays.toString(arrDouble));

        changeArrayElem(intArray, 2, 5);
        System.out.println(Arrays.toString(intArray));

        // --------------------------------------------------

        // 2 - Преобразование массива в ArrayList
        ArrayList listOfStrings = convertToArrayList(arrDouble);
        System.out.println(listOfStrings);

        // --------------------------------------------------

        // 3 - Задача с яблоками/апельсинами и коробкой
        // Положили яблоки в коробку
        Box appleBox1 = new Box("ЯБЛОКИ-1");
        appleBox1.addFruit(new Apple(15));
        appleBox1.getWeight();
        System.out.println("");

        // Не можем добавить если есть фрукты другого типа
        appleBox1.addFruit(new Orange(12));

        // Перекладывание фруктов в другую коробку
        System.out.println("");
        Box appleBox2 = new Box("ЯБЛОКИ-2");

        appleBox1.getWeight();    // 15
        appleBox2.getWeight();   // 0

        appleBox1.moveFruit(appleBox2);

        appleBox1.getWeight();    // 0
        appleBox2.getWeight();   // 15
        System.out.println("");

        // Сравнение коробок по весу
        boolean compareResult;
        Box orangeBox = new Box("АПЕЛЬСИНЫ-1");
        orangeBox.addFruit(new Orange(10));
        compareResult = orangeBox.compare(appleBox1);
        System.out.println(compareResult);
        compareResult = orangeBox.compare(appleBox2);
        System.out.println(compareResult);
    }

    // Generic Methods

    // 1
    static <T> void changeArrayElem(T[] array, int pos1, int pos2){
        try {
            T temp = array[pos1];
            array[pos1] = array[pos2];
            array[pos2] = temp;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Не верно указаны позиции для обмена значениями");
        }
    }

    // 2
    public static <T> ArrayList convertToArrayList(T[] array){
        ArrayList convertedArray = new ArrayList();
        for(T elem : array){
            convertedArray.add(elem);
        }
        return convertedArray;
    }
}
