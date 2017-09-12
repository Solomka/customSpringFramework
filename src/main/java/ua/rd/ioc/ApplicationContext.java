package ua.rd.ioc;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext implements Context {

    private List<BeanDefinition> beanDefinitions;

    //beans cash
    private Map<String, Object> beans = new HashMap<>();

    public ApplicationContext() {
        beanDefinitions = Arrays.asList(Config.EMPTY_BEAN_DEFINITION);//new BeanDefinition[0];
    }

    public ApplicationContext(Config config) {
        beanDefinitions = Arrays.asList(config.beanDefinitions());
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = getBeanDefinitionByName(beanName);
        Object bean = beans.get(beanName);
        if (bean == null) {
            bean = createNewBean(beanDefinition);
            if (!beanDefinition.isPrototype()) {
                beans.put(beanName, bean);
            }
        }
        return bean;
    }

    private Object createNewBean(BeanDefinition beanDefinition) {
        Object bean = createNewBeanInstance(beanDefinition);
        return bean;
    }

    // checks constructor with args here
    private Object createNewBeanInstance(BeanDefinition beanDefinition) {
        Class<?> type = beanDefinition.getBeanType();
        Constructor<?> constructor = type.getDeclaredConstructors()[0];
        Object newBean = null;
        if (constructor.getParameterCount() == 0) {
            newBean = createBeanWithDefaultConstructor(type);
        } else {
            newBean = createBeanWithParametrizedConstructor(type);
        }
        return newBean;
    }

    private Object createBeanWithDefaultConstructor(Class<?> type) {
        Object newBean;
        try {
            newBean = type.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return newBean;
    }

    private Object createBeanWithParametrizedConstructor(Class<?> type) {
        Object object;
        Constructor<?> constructor = type.getDeclaredConstructors()[0];
        Class<?>[] parameterTypes = constructor.getParameterTypes();

        List<String> beansNames = Arrays.stream(parameterTypes).map(t -> Introspector.decapitalize(t.getSimpleName())).collect(Collectors.toList());
        Object[] parametersObjects = beansNames.stream().map(this::getBean).toArray(Object[]::new);
        try {
            object = constructor.newInstance(parametersObjects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    private BeanDefinition getBeanDefinitionByName(String beanName) {
        return beanDefinitions.stream()
                .filter(bd -> Objects.equals(bd.getBeanName(), beanName))
                .findAny().orElseThrow(NoSuchBeanException::new);
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitions.stream()
                .map(BeanDefinition::getBeanName)
                .toArray(String[]::new);
    }
}
