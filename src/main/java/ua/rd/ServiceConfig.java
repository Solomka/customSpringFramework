package ua.rd;

import org.omg.CORBA.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import ua.rd.domain.Tweet;
import ua.rd.repository.TweetRepository;
import ua.rd.service.SimpleTweetService;
import ua.rd.service.TweetService;

import java.util.Arrays;

/**
 * Created by Solomiya_Yaremko on 9/22/2017.
 */

@Configuration
public class ServiceConfig {

    private final TweetRepository tweetRepository;

    @Autowired
    private Environment environment;

    @Autowired
    public ServiceConfig(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    @Bean(name = "tweetService")
    public TweetService tweetService() {
        //annonimus class that
        return new SimpleTweetService(tweetRepository) {
            @Override
            public Tweet newTweet() {
                return tweet();
            }
        };
    }

    @Bean
    @Scope("prototype")
    @Profile("default") //meta-annotation over @Conditional => matches: true/false
    public Tweet tweet() {
        return new Tweet();
    }

    @Bean(name = "tweet")
    @Scope("prototype")
    @Profile("dev")
    public Tweet tweetDev() {
        Tweet tweet = new Tweet();
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            tweet.setTxt(" Dev + Test txt");
        } else {
            tweet.setTxt("Dev txt");
        }


        return tweet;
    }
}
