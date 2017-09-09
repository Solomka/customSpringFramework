package ua.rd.ioc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

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
		Map<String, Class<?>> beanDescriptions = new HashMap<>();
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
		Map<String, Class<?>> beanDescriptions = new HashMap<String, Class<?>>() {
			{
				put(beanName, beanType);
			}
		};
		Config config = new JavaMapConfig(beanDescriptions);
		Context context = new ApplicationContext(config);

		String[] actual = context.getBeanDefinitionNames();

		String[] expected = { beanName };
		assertArrayEquals(expected, actual);
	}

	// @Ignore
	@Test
	public void getBeanDefinitionNamesWithSeveralBeanDefinitions() throws Exception {
		String beanName1 = "FirstBean";
		String beanName2 = "SecondBean";
		Class<TestBean> beanType = TestBean.class;
		Map<String, Class<?>> beanDescriptions = new HashMap<String, Class<?>>() {
			{
				put(beanName1, beanType);
				put(beanName2, beanType);
			}
		};
		Config config = new JavaMapConfig(beanDescriptions);
		Context context = new ApplicationContext(config);

		String[] actual = context.getBeanDefinitionNames();

		String[] expected = { beanName1, beanName2 };
		assertArrayEquals(expected, actual);
	}

	// @Ignore
	@Test
	public void getBeanWithOneBeanDefinitionIsNotNull() throws Exception {
		String beanName = "FirstBean";
		Class<?> beanType = TestBean.class;
		Map<String, Class<?>> beanDescriptions = new HashMap<String, Class<?>>() {
			{
				put(beanName, beanType);
			}
		};
		Config config = new JavaMapConfig(beanDescriptions);
		Context context = new ApplicationContext(config);

		Object bean = context.getBean(beanName);

		assertNotNull(bean);
	}

	@Test
	public void getBeanWithOneBeanDefinition() throws Exception {
		// future xml-file bean config
		String beanName = "FirstBean";
		Class<TestBean> beanType = TestBean.class;
		Map<String, Class<?>> beanDescriptions = new HashMap<String, Class<?>>() {
			{
				put(beanName, beanType);
			}
		};
		Config config = new JavaMapConfig(beanDescriptions);
		Context context = new ApplicationContext(config);

		TestBean bean = (TestBean) context.getBean(beanName);

		assertNotNull(bean);
	}

}