package com.company;

public class TestsClass {
    @BeforeSuite
    public void init(){
        System.out.println("init");
    }

    @Test(priority = 4)
    public void test1() {
        System.out.println("test1");
    }

    @Test(priority = 7)
    public void test2() {
        System.out.println("test2");
    }

    @Test(priority = 2)
    public void test3() {
        System.out.println("test3");
    }

    @AfterSuite
    public void stop(){
        System.out.println("stop");
    }
}
