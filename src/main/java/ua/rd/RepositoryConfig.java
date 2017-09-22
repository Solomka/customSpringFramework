package ua.rd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.rd.repository.InMemoryTweetRepository;
import ua.rd.repository.TweetRepository;

/**
 * Created by Solomiya_Yaremko on 9/22/2017.
 */
@Configuration
public class RepositoryConfig {

    @Bean(name = "tweetRepository", initMethod = "init")
    public TweetRepository tweetRepository() {
        return new InMemoryTweetRepository();
    }
}
