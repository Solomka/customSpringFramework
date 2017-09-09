package ua.rd.ioc;

import java.util.Arrays;
import java.util.List;

public class ApplicationContext implements Context {

	private BeanDefinition[] beanDefinitions;

	public ApplicationContext() {
		beanDefinitions = Config.EMPTY_BEAN_DEFINITION;// new BeanDefinition[0];
	}

	public ApplicationContext(Config config) {
		beanDefinitions = config.beanDefinitions();
	}

	public Object getBean(String beanName) {
		List<BeanDefinition> beanDefinitions = Arrays.asList(this.beanDefinitions);
		Object bean = null;
		if (beanDefinitions.stream().map(BeanDefinition::getBeanName).anyMatch(n -> n.equals(beanName))) {
			BeanDefinition beanDefinition = beanDefinitions.stream().filter(n -> n.getBeanName().equals(beanName))
					.findFirst().get();
			try {
				bean = beanDefinition.getBeanType().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new NoSuchBeanException();
		}
		return bean;
	}

	public String[] getBeanDefinitionNames() {
		String[] beanDefinitionNames = Arrays.stream(beanDefinitions).map(BeanDefinition::getBeanName)
				.toArray(String[]::new);
		return beanDefinitionNames;
	}
}
