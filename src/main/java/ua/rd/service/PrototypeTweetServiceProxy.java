package ua.rd.service;

import ua.rd.domain.Tweet;
import ua.rd.ioc.Context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Solomiya_Yaremko on 9/18/2017.
 */
public class PrototypeTweetServiceProxy {
    private TweetService realTweetService;
    private Context context;

    public PrototypeTweetServiceProxy(TweetService realTweetService, Context context) {
        this.realTweetService = realTweetService;
        this.context = context;
    }

    public TweetService createProxy() {
        Object proxyTweetService =
                Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                        new Class[]{TweetService.class},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                if (method.getName().equals("newTweet")) {
                                    return context.getBean("tweet");
                                } else {
                                    return method.invoke(realTweetService, args);
                                }
                            }
                        });
        return (TweetService) proxyTweetService;

    }

}
