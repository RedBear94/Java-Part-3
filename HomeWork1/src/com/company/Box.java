package com.company;

import java.util.ArrayList;

// Generic Class
public class Box <T> {
    String name;
    ArrayList <T> box = new ArrayList<>();

    Box(){
        this.name = "Коробка";
    }

    Box(String name){
        this.name = name;
    }

    public void addFruit(T fruit){
        if(box.isEmpty()){
            box.add(fruit);
        } else if(fruit.getClass().equals(box.get(0).getClass())){
            box.add(fruit);
        } else {
            System.out.println("В корзине уже лежат фрукты другого типа");
        }

    }

    public float getWeight(){
        float boxWeight = 0;
        for(T elem : box){
            try {
                boxWeight += ((Fruit) elem).getWeight();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Коробка " + this.name + " весит: " + boxWeight);
        return boxWeight;
    }

    public boolean compare(Box another){
        if(another.getWeight() == getWeight()){
            return true;
        }
        return false;
    }

    public void moveFruit(Box another){
        for(T elem : box){
            another.addFruit(elem);
        }
        box.clear();
    }
}
