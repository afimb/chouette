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
        try {
            String[] beanDeinitionNames = applicationContext.getBeanDefinitionNames();
            if (beanDeinitionNames != null)
                for (int i = 0; i < beanDeinitionNames.length; i++) {
                    applicationContext.getBean(beanDeinitionNames[i]);
                    logger.info("Bean Initialized : "+beanDeinitionNames[i]);
                }
        }
        catch(Throwable e) {
            logger.error("Bean Initilisation Error : "+e.getMessage());
            throw e;
        }
    }
}