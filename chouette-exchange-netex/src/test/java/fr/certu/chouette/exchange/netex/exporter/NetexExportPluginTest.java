package fr.certu.chouette.exchange.netex.exporter;

import fr.certu.chouette.exchange.netex.exporter.NetexExportPlugin;
import com.tobedevoured.modelcitizen.ModelFactory;
import fr.certu.chouette.common.ChouetteException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.report.ReportHolder;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
@SuppressWarnings("unchecked")
public class NetexExportPluginTest extends AbstractTestNGSpringContextTests {

    private XPath xPath = XPathFactory.newInstance().newXPath();
    private ModelFactory modelFactory;
    private NetexExportPlugin netexExportPlugin;
    private Line line;
    private List<Line> lines = new ArrayList<Line>();
    private ReportHolder reportContainer = new ReportHolder();

    @BeforeMethod
    protected void setUp() throws Exception {
        netexExportPlugin = (NetexExportPlugin) applicationContext.getBean("netexLineExport");
        modelFactory = (ModelFactory) applicationContext.getBean("modelFactory");
        line = modelFactory.createModel(Line.class);
    }

    @Test(groups = {"NetexExportPlugin"}, description = "Netex Export Plugin should produce one xml file")
    public void verifyCreateXmlFile() throws ChouetteException {
        List<ParameterValue> parameters = new ArrayList<ParameterValue>();
        SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
        outputFile.setFilenameValue("/tmp/testXmlFile.xml");
        parameters.add(outputFile);
        logger.error(line);
        lines.add(line);

        netexExportPlugin.doExport(lines, parameters, reportContainer);
        
        //assert netexExportPlugin.createXmlFile("/tmp/testXmlFile.xml", line).class ;
    }
    
    
    @Test(groups = {"NetexExportPlugin"}, description = "Netex Export Plugin should produce one zip file")
    public void verifyCreateZipFile() throws ChouetteException {
        List<ParameterValue> parameters = new ArrayList<ParameterValue>();
        SimpleParameterValue outputFile = new SimpleParameterValue("outputFile");
        outputFile.setFilenameValue("/tmp/testXmlFile.zip");
        parameters.add(outputFile);

        lines.add(line);

        netexExportPlugin.doExport(lines, parameters, reportContainer);
    }
}
