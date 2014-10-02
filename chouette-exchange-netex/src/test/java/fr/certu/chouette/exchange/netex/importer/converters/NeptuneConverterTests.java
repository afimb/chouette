package fr.certu.chouette.exchange.netex.importer.converters;

import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ximpleware.NavException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.exchange.report.ExchangeReport;

@ContextConfiguration(locations = { "classpath:testContext.xml",
      "classpath*:chouetteContext.xml" })
public class NeptuneConverterTests extends AbstractTestNGSpringContextTests
{

   private NeptuneConverter neptuneConverter;

   @BeforeClass
   protected void setUp() throws Exception
   {
      File f = FileUtils.getFile("src", "test", "resources", "line2_test.xml");
      ;
      FileInputStream fis = new FileInputStream(f);
      byte[] b = new byte[(int) f.length()];
      fis.read(b);

      VTDGen vg = new VTDGen();
      vg.setDoc(b);
      vg.parse(true); // set namespace awareness to true

      VTDNav nav = vg.getNav();
      neptuneConverter = new NeptuneConverter(nav);
      fis.close();
   }

   @Test(groups = { "NeptuneConverter" }, description = "Must return a line")
   public void verifyNeptune() throws XPathEvalException, NavException,
         XPathParseException, ParseException
   {
      ExchangeReport report = new ExchangeReport(ExchangeReport.KEY.IMPORT,
            "NETEX");
      Line line = neptuneConverter.convert(report);
      Assert.assertEquals(line.getName(), "7B");
      Assert.assertEquals(line.getPtNetwork().getName(), "METRO");
      Assert.assertEquals(line.getCompany().getName(), "RATP");
   }

}
