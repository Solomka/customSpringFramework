package ua.rd.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import ua.rd.repository.TweetRepository;

import java.util.Arrays;

/**
 * Created by Solomiya_Yaremko on 9/20/2017.
 */
public class TweetBFPP implements BeanFactoryPostProcessor {
    //before been creation in serviceContext
    //repoContext is visible
    //work with beanDefinition
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //System.out.println("TweetBeanFactoryPostProcessor: " + beanFactory.getBean(TweetRepository.class));
        //System.out.println("TweetBeanFactoryPostProcessor: " + beanFactory.getBean("tweetService"));
        BeanDefinition bd = beanFactory.getBeanDefinition("tweet");
        PropertyValue pv = bd.getPropertyValues().getPropertyValue("user");
        System.out.println("TweetBeanFactoryPostProcessor propValues: " + Arrays.toString(bd.getPropertyValues().getPropertyValues()));
        System.out.println("TweetBeanFactoryPostProcessor: " + beanFactory.getBeanDefinition("tweet"));
        //change bean definition here
        //chage prototype/singleton
        //create different object
        //change bean dependency
        //EL execution
    }
}
