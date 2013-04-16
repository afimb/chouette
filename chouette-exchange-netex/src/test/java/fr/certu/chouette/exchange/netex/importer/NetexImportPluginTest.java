package fr.certu.chouette.exchange.netex.importer;

import static org.mockito.Mockito.*;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.report.ReportHolder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NetexImportPluginTest extends AbstractTestNGSpringContextTests {
    private ComplexModelFactory complexModelFactory;
    private NetexImportPlugin netexImportPlugin;
    private Line netexLine;
    private List<Line> netexLines = new ArrayList<Line>();
    private ReportHolder reportContainer = new ReportHolder();    
    private List<ParameterValue> parameters = new ArrayList<ParameterValue>();
    private String xmlPath = FileUtils.getFile("src","test", "resources", "line2_test.xml").getAbsolutePath();
    private String zipPath = FileUtils.getFile("src","test", "resources", "netex.zip").getAbsolutePath();

    @BeforeClass
    protected void setUp() throws Exception {
        netexImportPlugin = (NetexImportPlugin) applicationContext.getBean("netexLineImport");       
        
        complexModelFactory = new ComplexModelFactory();
        complexModelFactory.init();
        netexLine = complexModelFactory.nominalLine( "1");
        netexLine.complete();
        
        netexLines.add(netexLine);
    }

    @Test(groups = {"NetexExportPlugin"}, description = "Netex Import Plugin should call readXmlFile or readZipFile")
    public void verifyDoImport() throws ChouetteException 
    {
		ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "NETEX");
        SimpleParameterValue inputFile = new SimpleParameterValue("inputFile");        
        inputFile.setFilepathValue(xmlPath);
        parameters.add(inputFile);
        
        NetexImportPlugin netexImportPluginSpy = spy(netexImportPlugin);
        
        when(netexImportPluginSpy.readXmlFile(xmlPath, report)).thenReturn(netexLine);        
        
        Assert.assertEquals(netexImportPluginSpy.doImport(parameters, reportContainer), netexLines);
           
        inputFile = new SimpleParameterValue("inputFile");        
        inputFile.setFilepathValue(zipPath);
        parameters.add(inputFile);
        
        when(netexImportPluginSpy.readZipFile(zipPath, report)).thenReturn(netexLines);        
        
        Assert.assertEquals(netexImportPluginSpy.doImport(parameters, reportContainer), netexLines);
    }
    
    @Test(groups = {"NetexExportPlugin"}, description = "Must return a line")
    public void verifyReadXmlFile() throws ChouetteException 
    {
		ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "NETEX");
        Line line = netexImportPlugin.readXmlFile(xmlPath, report);
        Assert.assertNotNull(line);
    }
    
    @Test(groups = {"NetexExportPlugin"}, description = "Must return lines")
    public void verifyReadZipFile() throws ChouetteException 
    {
		ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT, "NETEX");
       List<Line> lines = netexImportPlugin.readZipFile(zipPath, report); 
       Assert.assertTrue(!lines.isEmpty());
    }
}
