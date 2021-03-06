package ua.rd;

import ua.rd.domain.Tweet;
import ua.rd.ioc.ApplicationContext;
import ua.rd.ioc.Config;
import ua.rd.ioc.Context;
import ua.rd.ioc.JavaMapConfig;
import ua.rd.repository.InMemoryTweetRepository;
import ua.rd.repository.TweetRepository;
import ua.rd.service.SimpleTweetService;
import ua.rd.service.TweetService;

import java.util.HashMap;
import java.util.Map;

public class IoCRunner {

    public static void main(String[] args) {
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put("tweetRepository",
                            new HashMap<String, Object>() {{
                                put("type", InMemoryTweetRepository.class);
                                put("isPrototype", true);
                            }}
                    );
                    //prototype
                    put("tweet",
                            new HashMap<String, Object>() {{
                                put("type", Tweet.class);
                                put("isPrototype", true);
                            }}
                    );
                    //singleton
                    put("tweetService",
                            new HashMap<String, Object>() {{
                                put("type", SimpleTweetService.class);
                                put("isPrototype", false);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TweetRepository tweetRepository = (TweetRepository) context.getBean("tweetRepository");
        TweetService tweetService = (TweetService) context.getBean("tweetService");

        System.out.println(tweetRepository.allTweets());
        System.out.println(tweetService.allTweets());

        System.out.println(
                tweetService.newTweet() == tweetService.newTweet()
        );

        /*System.out.println(
                tweetService.getRepository() == tweetService.getRepository()
        );*/

    }
}