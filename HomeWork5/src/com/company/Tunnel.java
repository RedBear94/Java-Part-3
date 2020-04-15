package com.company;

import java.util.concurrent.Semaphore;

public class Tunnel extends Stage {
    Semaphore semTunnel;
    public Tunnel(Semaphore semTunnel) {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
        this.semTunnel = semTunnel;
    }
    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                semTunnel.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                semTunnel.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
