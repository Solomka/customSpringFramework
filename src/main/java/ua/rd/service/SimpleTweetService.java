package ua.rd.service;

import ua.rd.domain.Tweet;
import ua.rd.repository.TweetRepository;

public class SimpleTweetService implements TweetService {

    private final TweetRepository tweetRepository;
    private Tweet tweet;

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
    public Tweet newTweet() {
        return tweet;
    }
/*
    @Override
    public static Tweet newTweet() {
        return tweet;
    }*/
}
