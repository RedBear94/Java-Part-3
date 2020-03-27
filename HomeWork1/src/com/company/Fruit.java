package com.company;

public class Fruit {
    float weight;
    int count;

    public Fruit(float weight, int count){
        this.weight = weight;
        this.count = count;
    }

    public Fruit(float weight){
        this.weight = weight;
    }

    public Fruit(int count){
        this.count = count;
    }

    public float getWeight() {
        return weight * count;
    }
}
