package ua.rd.ioc;

/**
 * Created by Solomiya_Yaremko on 9/15/2017.
 */
public class ProxiedTestBean implements ITestBean {

    @Override
    @Benchmark
    public String methodToBenchmark(String str) {
        String reversedString = new StringBuilder(str).reverse().toString();
        return reversedString;
    }

}
