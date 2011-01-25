package fr.certu.chouette.manager;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
@ContextConfiguration(locations={"classpath:chouetteContext.xml"})
public abstract class AbstractManagerTests<T extends NeptuneIdentifiedObject> extends AbstractTestNGSpringContextTests {

	protected AbstractNeptuneManager<T>  manager;
	protected String beanName;
	protected T bean;

	public abstract void createManager();
	
	@SuppressWarnings("unchecked")
	public void initManager(String beanName, String managerName, T bean)
	{
		manager = (AbstractNeptuneManager<T>) applicationContext.getBean(managerName);
		this.beanName = beanName;
		this.bean = bean;
	}


}
