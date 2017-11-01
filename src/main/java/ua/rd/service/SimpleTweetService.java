package ua.rd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ua.rd.domain.Tweet;
import ua.rd.ioc.Benchmark;
import ua.rd.repository.TweetRepository;

@Service("tweetService")
@Profile("dev")
public class SimpleTweetService implements TweetService {

    @Autowired
    private final TweetRepository tweetRepository;
    private Tweet tweet;

    public SimpleTweetService() {
        tweetRepository = null;
    }

    public SimpleTweetService(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    @Override
    public Iterable<Tweet> allTweets() {
        return tweetRepository.allTweets();
    }

    @Override
    public TweetRepository getRepository() {
        return tweetRepository;
    }

    @Override
    // @Lookup - doesn't work without package scan
    @Benchmark
    public Tweet newTweet() {
        return tweet;
    }
/*
    @Override
    public static Tweet newTweet() {
        return tweet;
    }*/
}
