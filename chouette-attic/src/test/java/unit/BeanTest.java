package unit;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import fr.certu.chouette.manager.SingletonManager;

public class BeanTest {

    private static final Logger logger = Logger.getLogger(BeanTest.class);
    private ApplicationContext applicationContext;

    @BeforeSuite
    public void initialisation() {
		applicationContext = SingletonManager.getApplicationContext();
    }

    @Test(groups="tests unitaires", description="test des Beans Spring")
	public void generateBeans() throws Throwable {
        String[] beanDeinitionNames = applicationContext.getBeanDefinitionNames();
        if (beanDeinitionNames != null)
            for (int i = 0; i < beanDeinitionNames.length; i++) {
                try {
                    applicationContext.getBean(beanDeinitionNames[i]);
                    logger.info("Bean Initialized : "+beanDeinitionNames[i]);
                }
                catch(Throwable e) {
                    String message = e.getMessage();
                    logger.error("Bean Initilisation Error : "+message);
                    if ((message == null) || (message.indexOf("abstract") < 0))
                        throw e;
                }
            }
    }
}