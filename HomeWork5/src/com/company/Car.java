package com.company;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Car implements Runnable {
    Semaphore sem;
    private CountDownLatch start;;

    private static int CARS_COUNT;
    private static boolean winPosition = false;

    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, Semaphore sem, CountDownLatch start) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.sem=sem;
        this.start = start;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
        } catch (Exception e) {
            e.printStackTrace();
        }

        start.countDown();
        try {
            start.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
        }
        try {
            sem.acquire();
            if(winPosition == false){
                System.out.println(name + " - WIN");
                winPosition = true;
            }
            sem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}