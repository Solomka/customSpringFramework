package ua.rd.ioc;

import java.io.Serializable;

public class TestBean implements Serializable, ITestBean{

    public String initValue;
    public String postConstructValue;


    //call method dynamically by name
    public void init() {
        initValue = "initialized";
    }

    //call method dynamically by @MyPostConstruct annotation presence
    @MyPostConstruct
    public void postConstruct() {
        postConstructValue = "initializedByPostConstruct";
    }

   // @Benchmark
    public String methodToBenchmark(String str) {
        String reversedString = new StringBuilder(str).reverse().toString();
        return reversedString;
    }
}