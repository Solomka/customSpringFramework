package ua.rd.repository;

import ua.rd.domain.Tweet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryTweetRepository implements TweetRepository {

    private List<Tweet> tweets;

    private void init() {
       /* tweets = Arrays.asList(
                new Tweet(1L, "First Mesg", null),
                new Tweet(2L, "Second Mesg", null)
        );*/
       tweets = new ArrayList<>();
    }

    @Override
    public Iterable<Tweet> allTweets() {
        return tweets;
    }
}
