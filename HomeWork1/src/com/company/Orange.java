package com.company;

public class Orange extends Fruit {
    public Orange(int count){
        super(1.5f, count);
    }

    public Orange(float weight, int count) {
        super(weight, count);
    }
}
