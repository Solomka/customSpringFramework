package ua.rd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import ua.rd.domain.Tweet;
import ua.rd.ioc.Benchmark;
import ua.rd.repository.TweetRepository;

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
    @Lookup
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
