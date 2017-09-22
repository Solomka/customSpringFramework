package ua.rd.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by Solomiya_Yaremko on 9/20/2017.
 */
public class BenchmarkPostProcessor implements BeanPostProcessor{

    //bean - it will be proxy
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.print("PP's beans: " + beanName + " class: " + bean.getClass() + "\n");
        //implement proxy
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
