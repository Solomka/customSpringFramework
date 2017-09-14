package ua.rd.ioc;

import java.beans.Introspector;
import java.lang.reflect.*;
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
        BeanDefinition beanDefinition = getBeanDefinitionByName(beanName);
        Object bean = beans.get(beanName);
        if (bean != null) {
            return bean;
        }
        bean = createNewBean(beanDefinition);
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
        //beanBuilder.createBenchmarkProxy();

        Object bean = beanBuilder.build();

        return bean;
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

        private void createBrenchmarkProxy() {
            Class<?> beanClass = bean.getClass();
            bean = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                    beanClass.getInterfaces(),
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return method.invoke(bean, args);
                        }
                    });
        }

        public Object build() {
            return bean;
        }
    }
}
