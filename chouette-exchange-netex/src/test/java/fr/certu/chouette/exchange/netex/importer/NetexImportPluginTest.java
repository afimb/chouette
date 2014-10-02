package fr.certu.chouette.exchange.netex.importer;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.netex.ComplexModelFactory;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.ParameterValue;
import fr.certu.chouette.plugin.exchange.SimpleParameterValue;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportHolder;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class NetexImportPluginTest extends AbstractTestNGSpringContextTests
{
   private ComplexModelFactory complexModelFactory;
   private NetexImportPlugin netexImportPlugin;
   private Line netexLine;
   private List<Line> netexLines = new ArrayList<Line>();
   private ReportHolder reportContainer = new ReportHolder();
   private List<ParameterValue> parameters = new ArrayList<ParameterValue>();
   private String xmlPath = FileUtils.getFile("src", "test", "resources",
         "line2_test.xml").getAbsolutePath();
   private String zipPath = FileUtils.getFile("src", "test", "resources",
         "netex.zip").getAbsolutePath();

   @BeforeClass
   protected void setUp() throws Exception
   {
      netexImportPlugin = (NetexImportPlugin) applicationContext
            .getBean("netexLineImport");

      complexModelFactory = new ComplexModelFactory();
      complexModelFactory.init();
      netexLine = complexModelFactory.nominalLine("1");
      netexLine.complete();

      netexLines.add(netexLine);
   }

   @Test(groups = { "NetexExportPlugin" }, description = "Netex Import Plugin should call readXmlFile or readZipFile")
   public void verifyDoImport() throws ChouetteException
   {
      ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "NETEX");
      SimpleParameterValue inputFile = new SimpleParameterValue("inputFile");
      inputFile.setFilepathValue(xmlPath);
      parameters.add(inputFile);

      NetexImportPlugin netexImportPluginSpy = spy(netexImportPlugin);

      when(netexImportPluginSpy.readXmlFile(xmlPath, (Report) report))
            .thenReturn(netexLine);

      Assert.assertEquals(
            netexImportPluginSpy.doImport(parameters, reportContainer, null),
            netexLines);

      inputFile = new SimpleParameterValue("inputFile");
      inputFile.setFilepathValue(zipPath);
      parameters.add(inputFile);

      when(netexImportPluginSpy.readZipFile(zipPath, (Report) report))
            .thenReturn(netexLines);

      Assert.assertEquals(
            netexImportPluginSpy.doImport(parameters, reportContainer, null),
            netexLines);
   }

   @Test(groups = { "NetexExportPlugin" }, description = "Must return a line")
   public void verifyReadXmlFile() throws ChouetteException
   {
      ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "NETEX");
      Line line = netexImportPlugin.readXmlFile(xmlPath, (Report) report);
      Assert.assertNotNull(line);
   }

   @Test(groups = { "NetexExportPlugin" }, description = "Must return lines")
   public void verifyReadZipFile() throws ChouetteException
   {
      ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "NETEX");
      List<Line> lines = netexImportPlugin
            .readZipFile(zipPath, (Report) report);
      Assert.assertTrue(!lines.isEmpty());
   }
}
