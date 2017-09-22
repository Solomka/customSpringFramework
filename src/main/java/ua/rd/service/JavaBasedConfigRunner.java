package ua.rd.service;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.rd.AppConfigRunner;
import ua.rd.RepositoryConfig;
import ua.rd.ServiceConfig;

import java.util.Arrays;

/**
 * Created by Solomiya_Yaremko on 9/22/2017.
 */
public class JavaBasedConfigRunner {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctxRepo = new AnnotationConfigApplicationContext(RepositoryConfig.class);
        AnnotationConfigApplicationContext ctxService = new AnnotationConfigApplicationContext();

        ctxService.setParent(ctxRepo);
        ctxService.register(ServiceConfig.class);
        ctxService.getEnvironment().setActiveProfiles("dev", "test");
        ctxService.refresh();


        System.out.println("Tweet: " + ctxService.getBean("tweet"));
        System.out.println(Arrays.toString(ctxService.getBeanDefinitionNames()));

        TweetService tweetService = (TweetService) ctxService.getBean("tweetService");
        System.out.println(tweetService.newTweet() == tweetService.newTweet());
    }

}
