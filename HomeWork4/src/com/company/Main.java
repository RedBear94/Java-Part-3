package com.company;

public class Main {
    private final Object monitor = new Object();
    private boolean printA = true;
    private boolean printB = true;

    public static void main(String[] args) {
        Main main = new Main();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    main.printA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    main.printB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    main.printC();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }

    private void printA() throws InterruptedException {
        synchronized (monitor) {
            // System.out.println("Start A");
            for (int i = 0; i <= 5; i++) {
                //System.out.println();
                //System.out.println("i = " + i);
                //System.out.println("Start A");

                if(!printA){
                    //System.out.println("Wait A");
                    monitor.wait();
                }

                System.out.println("A");
                printA = false;
                printB = true;

                //System.out.println("NotifyAll A");
                monitor.notifyAll();
                // monitor.notify();

                //System.out.println("Wait A");
                //System.out.println();
                monitor.wait();
            }
        }
    }

    private void printB() throws InterruptedException {
        synchronized (monitor) {
            // monitor.wait();
            // System.out.println("Start B");
            for (int i = 0; i <= 5; i++) {

                if(printA){
                    //System.out.println("Wait B");
                    monitor.wait();
                }
                //System.out.println();
                //System.out.println("i = " + i);
                //System.out.println("Start B");

                System.out.println("B");
                printB = false;

                //System.out.println("NotifyAll B");
                monitor.notifyAll();
                //System.out.println("Wait B");
                //System.out.println();
                monitor.wait();
            }
        }
    }

    private void printC() throws InterruptedException {
        synchronized (monitor) {
            // monitor.wait();
            // System.out.println("Start C");
            for (int i = 0; i <= 5; i++) {

                if(printB){
                    //System.out.println("Wait C");
                    monitor.wait();
                }
                //System.out.println();
                //System.out.println("i = " + i);
                //System.out.println("Start C");

                System.out.println("C");
                printA = true;

                //System.out.println("NotifyAll C");
                monitor.notifyAll();
                //System.out.println("Wait C");
                //System.out.println();
                monitor.wait();
            }
        }
    }
}
