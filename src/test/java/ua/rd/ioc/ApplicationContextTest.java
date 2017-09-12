package ua.rd.ioc;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApplicationContextTest {

    // @Ignore
    @Test(expected = NoSuchBeanException.class)
    public void getBeanWithEmptyContext() throws Exception {
        Context context = new ApplicationContext();
        context.getBean("abc");
    }

    // @Ignore
    @Test
    public void getBeanDefinitionNamesWithEmptyContext() throws Exception {
        // given
        Context context = new ApplicationContext();

        // when
        String[] actual = context.getBeanDefinitionNames();

        // tnen
        String[] expected = {};
        assertArrayEquals(expected, actual);
    }

    // @Ignore
    @Test
    public void getBeanDefinitionNamesWithEmptyBeanDefinition() throws Exception {
        Map<String, Map<String, Object>> beanDescriptions = new HashMap<>();
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {};
        assertArrayEquals(expected, actual);
    }

    // @Ignore
    @Test
    public void getBeanDefinitionNamesWithOneBeanDefinition() throws Exception {
        String beanName = "FirstBean";
        Class<TestBean> beanType = TestBean.class;
        Map<String, Map<String, Object>> beanDescriptions = new HashMap<String, Map<String, Object>>() {
            {
                put(beanName, new HashMap<>());
            }
        };
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {beanName};
        assertArrayEquals(expected, actual);
    }

    // @Ignore
    @Test
    public void getBeanDefinitionNamesWithSeveralBeanDefinitions() throws Exception {
        String beanName1 = "FirstBean";
        String beanName2 = "SecondBean";
        Map<String, Map<String, Object>> beanDescriptions = new HashMap<String, Map<String, Object>>() {
            {
                put(beanName1, new HashMap<>());
                put(beanName2, new HashMap<>());
            }
        };
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        String[] actual = context.getBeanDefinitionNames();

        String[] expected = {beanName1, beanName2};
        assertArrayEquals(expected, actual);
    }

    @Test
    public void getBeanWithOneBeanDefinition() throws Exception {
        // future xml-file bean config
        String beanName = "FirstBean";
        Class<TestBean> beanType = TestBean.class;
        Map<String, Map<String, Object>> beanDescriptions = new HashMap<String, Map<String, Object>>() {{
            put(beanName, new HashMap<String, Object>() {
                {
                    put("type", beanType);
                }
            });
        }};
        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBean bean = (TestBean) context.getBean(beanName);

        assertNotNull(bean);
    }

    //singleton in Spring behaves differently from a usual Singleton =>
    //it prevents user from creating a bean with already existing id,
    //but creating the instance of already instantiated class is allowed
    @Test
    public void getBeanNotSameInstancesWithSameType() throws Exception {
        String beanName1 = "FirstBean";
        String beanName2 = "SecondBean";
        Class<TestBean> beanType = TestBean.class;
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put(beanName1,
                            new HashMap<String, Object>() {{
                                put("type", beanType);
                            }}
                    );
                    put(beanName2,
                            new HashMap<String, Object>() {{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBean bean1 = (TestBean) context.getBean(beanName1);
        TestBean bean2 = (TestBean) context.getBean(beanName2);

        assertNotSame(bean1, bean2);
    }

    @Test
    public void getBeanIsSingleton() throws Exception {
        String beanName = "FirstBean";
        Class<TestBean> beanType = TestBean.class;
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put(beanName,
                            new HashMap<String, Object>() {{
                                put("type", beanType);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBean bean1 = (TestBean) context.getBean(beanName);
        TestBean bean2 = (TestBean) context.getBean(beanName);

        assertSame(bean1, bean2);
    }

    @Test
    public void getBeanIsPrototype() throws Exception {
        String beanName = "FirstBean";
        Class<TestBean> beanType = TestBean.class;
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put(beanName,
                            new HashMap<String, Object>() {{
                                put("type", beanType);
                                put("isPrototype", true);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBean bean1 = (TestBean) context.getBean(beanName);
        TestBean bean2 = (TestBean) context.getBean(beanName);

        assertNotSame(bean1, bean2);
    }

    @Test
    public void getBeanWithOneDependedBean() throws Exception {
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put("testBean",
                            new HashMap<String, Object>() {{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );
                    put("testBeanWithConstructor",
                            new HashMap<String, Object>() {{
                                put("type", TestBeanWithConstructor.class);
                                put("isPrototype", false);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanWithConstructor bean
                = (TestBeanWithConstructor) context.getBean("testBeanWithConstructor");

        assertNotNull(bean);
    }


    @Test
    public void getBeanWithSeveralDependedBeansIsSingleton() throws Exception {
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put("testBean",
                            new HashMap<String, Object>() {{
                                put("type", TestBean.class);
                                put("isPrototype", false);
                            }}
                    );
                    put("testBeanWithConstructorTwoParams",
                            new HashMap<String, Object>() {{
                                put("type", TestBeanWithConstructorTwoParams.class);
                                put("isPrototype", false);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanWithConstructorTwoParams bean
                = (TestBeanWithConstructorTwoParams) context.getBean("testBeanWithConstructorTwoParams");

        assertSame(bean.testBean1, bean.testBean2);
    }

    @Test
    public void getBeanWithSeveralDependedBeansIsPrototype() throws Exception {
        Map<String, Map<String, Object>> beanDescriptions =
                new HashMap<String, Map<String, Object>>() {{
                    put("testBean",
                            new HashMap<String, Object>() {{
                                put("type", TestBean.class);
                                put("isPrototype", true);
                            }}
                    );
                    put("testBeanWithConstructorTwoParams",
                            new HashMap<String, Object>() {{
                                put("type", TestBeanWithConstructorTwoParams.class);
                                put("isPrototype", true);
                            }}
                    );
                }};

        Config config = new JavaMapConfig(beanDescriptions);
        Context context = new ApplicationContext(config);

        TestBeanWithConstructorTwoParams bean
                = (TestBeanWithConstructorTwoParams) context.getBean("testBeanWithConstructorTwoParams");

        assertNotSame(bean.testBean1, bean.testBean2);
    }


    static class TestBean {

    }

    static class TestBeanWithConstructor {
        private final TestBean testBean;

        public TestBeanWithConstructor(TestBean testBean) {
            this.testBean = testBean;
        }
    }

    static class TestBeanWithConstructorTwoParams {
        public TestBeanWithConstructorTwoParams(TestBean testBean1, TestBean testBean2) {
            this.testBean1 = testBean1;
            this.testBean2 = testBean2;
        }

        public final TestBean testBean1;
        public final TestBean testBean2;
    }

}