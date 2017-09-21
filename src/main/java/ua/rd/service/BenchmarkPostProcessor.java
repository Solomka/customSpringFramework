package ua.rd.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Created by Solomiya_Yaremko on 9/20/2017.
 */
public class BenchmarkPostProcessor implements BeanPostProcessor{

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        System.out.println("BenchmarkPostProcessor: " + o.getClass().getSimpleName());
        System.out.println("BenchmarkPostProcessor: " + o.getClass().getSimpleName());
        return null;
    }
}
