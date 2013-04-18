package fr.certu.chouette.exchange.netex.importer;

import com.ximpleware.EOFException;
import com.ximpleware.EncodingException;
import com.ximpleware.EntityException;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NetexFileReaderTest extends AbstractTestNGSpringContextTests {
    private NetexFileReader netexFileReader;
    private ComplexModelFactory complexModelFactory;
    private Line netexLine;

    @BeforeClass
    protected void setUp() throws Exception {
        netexFileReader = (NetexFileReader) applicationContext.getBean("netexFileReader");
        
        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        netexLine = complexModelFactory.nominalLine( "1");
        netexLine.complete();
    }

    @Test(groups = {"NetexFileReader"}, description = "Netex File Reader must return a Line Object")
    public void verifyReadInputStream() throws FileNotFoundException, IOException, EncodingException, EOFException, EntityException, ParseException, XPathParseException, XPathEvalException, NavException, java.text.ParseException, DatatypeConfigurationException {        
		ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "NETEX");
        File f = FileUtils.getFile("src","test", "resources", "line2_test.xml");
        InputStream stream = new FileInputStream(f);
        
        Line neptuneLine = netexFileReader.readInputStream(stream,report);
        
        Assert.assertEquals(neptuneLine.getName(), netexLine.getName());
    }
    
}