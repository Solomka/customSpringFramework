package ua.rd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import ua.rd.domain.Tweet;
import ua.rd.domain.User;
import ua.rd.ioc.BeanDefinition;

import java.util.Arrays;

/**
 * Created by Solomiya_Yaremko on 9/22/2017.
 */

//@Configuration
public class AppConfigRunner {

    @Autowired
    private Environment encv;


    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfigRunner.class);
        System.out.println(Arrays.toString(ctx.getBeanDefinitionNames()));

        // BeanDefinition beanDefinition = (BeanDefinition) ctx.getBeanFactory().getBeanDefinition("tweet");
        //factory-bean
        //factory-mathod
        //<bean name="tweet" factory-bean="tweetService" factory-method="newTweet" scope="prototype"/>
        //System.out.println(beanDefinition);

        Tweet tweet = (Tweet) ctx.getBean("tweet");
        System.out.println("Tweet: " + tweet);

        User user = (User) ctx.getBean("user");
        System.out.println("User: " + user);

        AppConfigRunner appConfigRunner = ctx.getBean(AppConfigRunner.class);

        System.out.println(ctx.getBean(AppConfigRunner.class).getClass().getSimpleName());

        /*
        AppConfigRunner$$EnhancerBySpringCGLIB$$f13ad912 - proxying all the methods of the class
         */

    }

    @Bean
    @Lazy
    // @Scope("prototype")
    public Tweet tweet() {
        System.out.println("We are here =(");
        return new Tweet();
    }

    @Bean
    @Lazy
    public User user() {
        User user = new User();
        user.setTweet(tweet());
        return user;
    }
}
