package fr.certu.chouette.manager;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.log4testng.Logger;

@ContextConfiguration(locations={"classpath:managers.xml"})
public class LineManagerTests extends AbstractTestNGSpringContextTests {

    private static final Logger logger = Logger.getLogger(LineManagerTests.class);
    private LineManager lineManager;


//    public void setLineManager(LineManager lineManager) {
//        System.out.println("set lineManager : " + lineManager);
//        this.lineManager = lineManager;
//    }
    
    @Test(groups = { "manager" })
    public void verifyCreateLineManager() throws ChouetteException
    {
            //System.out.println("lineManager : " + applicationContext);
            assert applicationContext.getBean("lineManager") != null;
            lineManager = (LineManager) applicationContext.getBean("lineManager");
            assert lineManager.getNewInstance(null) != null;
     }

    @Test(groups = { "manager" }, expectedExceptions={ChouetteException.class})
    public void notDao() throws ChouetteException
    {
        lineManager = (LineManager) applicationContext.getBean("lineManager");
        lineManager.get(null, Filter.getNewEmptyFilter(), DetailLevelEnum.ATTRIBUTE);
        assert false;  //shouldn't be invoked
    }
}
