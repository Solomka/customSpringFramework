package ua.rd.ioc;

import ua.rd.service.PrototypeTweetServiceProxy;
import ua.rd.service.TweetService;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Clock;
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
        //immediate all beanDefinitions beans creation  at the moment of AppContext with Config initialization
        initContext(beanDefinitions);
    }

    private void initContext(List<BeanDefinition> beanDefinitions) {
        beanDefinitions.forEach(bd -> getBean(bd.getBeanName()));
    }


    public Object getBean(String beanName) {
        //Optional or empty Optional
        return Optional.ofNullable(beans.get(beanName))
                //Return the value if present, otherwise invoke other and return the result of that invocation.
                .orElseGet(() ->
                        createBeanByBeanDefinition(getBeanDefinitionByName(beanName))
                );

    }

    private Object createBeanByBeanDefinition(BeanDefinition beanDefinition) {
        String beanName = beanDefinition.getBeanName();
        Object bean = createNewBean(beanDefinition);
        if (!beanDefinition.isPrototype()) {
            beans.put(beanName, bean);
        }
        return bean;
    }

    private Object createNewBean(BeanDefinition beanDefinition) {
        BeanBuilder beanBuilder = new BeanBuilder(beanDefinition);
        beanBuilder.createNewBeanInstance();

        beanBuilder.callInitMethod();
        beanBuilder.callPostConstructAnnotatedMethod();
        beanBuilder.createBenchmarkProxy();
        //substitude PrototypeTweetServiceProxy instead of SimpleTweetService object instance
        //for new prototype bean creation in case of newTweet method call in SimpleTweetService
        /**TODO:
         * rewrite this
         */
        if (beanDefinition.getBeanName().equals("tweetService")) {
            beanBuilder.creatTweetServiceProxy();
        }

        Object bean = beanBuilder.build();

        return bean;
    }

    private BeanDefinition getBeanDefinitionByName(String beanName) {
        System.out.println(beanName);
        return beanDefinitions.stream()
                .filter(bd -> Objects.equals(bd.getBeanName(), beanName))
                .findAny().orElseThrow(NoSuchBeanException::new);
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitions.stream()
                .map(BeanDefinition::getBeanName)
                .toArray(String[]::new);
    }

    class BeanBuilder {
        private BeanDefinition beanDefinition;
        private Object bean;

        public BeanBuilder(BeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        // checks constructor with args here
        private void createNewBeanInstance() {
            Class<?> type = beanDefinition.getBeanType();
            Constructor<?> constructor = type.getDeclaredConstructors()[0];
            Object newBean;
            if (constructor.getParameterCount() == 0) {
                newBean = createBeanWithDefaultConstructor(type);
            } else {
                newBean = createBeanWithParametrizedConstructor(type);
            }
            bean = newBean;
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
            Object bean;
            Constructor<?> constructor = type.getDeclaredConstructors()[0];
            Class<?>[] parameterTypes = constructor.getParameterTypes();

            List<String> beansNames = Arrays.stream(parameterTypes).map(t -> Introspector.decapitalize(t.getSimpleName())).collect(Collectors.toList());
            Object[] parametersObjects = beansNames.stream().map(beanName -> getBean(beanName)).toArray(Object[]::new);
            try {
                bean = constructor.newInstance(parametersObjects);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
            return bean;
        }

        /*private String getBeanNameByType(Class<?> paramType){
            String paramTypeName = paramType.getSimpleName();
            String localBeanName = Character.toLowerCase(paramTypeName.charAt(0)) + paramTypeName.substring(1);
            return  localBeanName;
        }*/

        private void callInitMethod() {
            Class<?> beanType = bean.getClass();
            try {
                Method initMethod = beanType.getMethod("init");
                initMethod.invoke(bean);
            } catch (NoSuchMethodException e) {
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private void callPostConstructAnnotatedMethod() {
            Class<?> beanType = bean.getClass();

            Method[] postConstructMethod = beanType.getMethods();

            Arrays.stream(postConstructMethod).filter(method -> method.isAnnotationPresent(MyPostConstruct.class)).findAny()
                    .ifPresent(m -> {
                        try {
                            m.invoke(bean);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        private void createBenchmarkProxy() {
            Class<?> beanClass = bean.getClass();
            Object originalBean = bean;

            if (isBenchmarkAnnotatedMethodPresent(beanClass)) {
                bean = generateProxy(beanClass, originalBean);
            }
        }

        private boolean isBenchmarkAnnotatedMethodPresent(Class<?> beanClass) {
            return Arrays.stream(beanClass.getMethods()).filter(m -> m.isAnnotationPresent(Benchmark.class)).findAny().isPresent();
        }

        private Object generateProxy(Class<?> beanClass, Object originalBean) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(),
                    beanClass.getInterfaces(), (Object proxy, Method method, Object[] args) -> {
                        //if (Arrays.stream(beanClass.getMethods()).filter(m -> m.getName().equals(method.getName())).findAny().filter(m -> m.isAnnotationPresent(Benchmark.class)).isPresent()) {
                        if (beanClass.getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Benchmark.class)) {
                            return methodInvocationWithExecutionTimeNoted(originalBean, method, args);
                        } else {
                            return method.invoke(originalBean, args);
                        }
                    });
        }

        private Object methodInvocationWithExecutionTimeNoted(Object newBean, Method method, Object[] args) {
            Object objectAnnotatedMethodExecutionResult;
            long startTime = System.nanoTime();

            try {
                objectAnnotatedMethodExecutionResult = method.invoke(newBean, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }

            long stopTime = System.nanoTime();
            long elapsedTimeInSeconds = (stopTime - startTime);

            System.out.println("Method execution time: " + elapsedTimeInSeconds);
            return objectAnnotatedMethodExecutionResult;
        }


        public Object build() {
            return bean;
        }

        public void creatTweetServiceProxy() {
            bean = new PrototypeTweetServiceProxy((TweetService) bean, ApplicationContext.this).createProxy();
        }
    }


}
