package fr.certu.chouette.exchange.netex.importer.converters;

import javax.xml.xpath.XPathExpressionException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class PTNetworkConverterTests extends AbstractTestNGSpringContextTests {

    private PTNetworkConverter networkConverter;

    @BeforeMethod
    protected void setUp() throws Exception {
        networkConverter = new PTNetworkConverter("/home/luc/line_test.xml");
    }

    @Test(groups = {"ServiceFrame"}, description = "Export Plugin should have one network")
    public void verifyNetwork() {
        
    }

}
