package ua.rd;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.rd.domain.Tweet;
import ua.rd.service.TweetService;

import java.util.Arrays;

public class SpringXMLConfigRunner {

    public static void main(String[] args) {
        ConfigurableApplicationContext repoContext
                = new ClassPathXmlApplicationContext("repoContext.xml");
        ConfigurableApplicationContext serviceContext
                = new ClassPathXmlApplicationContext(new String[]{"serviceContext.xml"}, repoContext);

        System.out.println(Arrays.toString(repoContext.getBeanDefinitionNames()));
        System.out.println(Arrays.toString(serviceContext.getBeanDefinitionNames()));

        TweetService tweetService = (TweetService) serviceContext.getBean("tweetService");
        System.out.println(tweetService.allTweets());

        System.out.println(
                tweetService.newTweet() == tweetService.newTweet()
        );

        Tweet tweet = (Tweet) serviceContext.getBean("tw");
        serviceContext.close();
        repoContext.close();

    }
}
