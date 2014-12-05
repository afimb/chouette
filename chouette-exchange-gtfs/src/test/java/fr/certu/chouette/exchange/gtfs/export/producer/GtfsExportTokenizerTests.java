package fr.certu.chouette.exchange.gtfs.export.producer;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.exchange.gtfs.export.producer.mock.GtfsExporterMock;
import fr.certu.chouette.exchange.gtfs.exporter.producer.GtfsTransferProducer;
import fr.certu.chouette.exchange.gtfs.exporter.report.GtfsReport;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.Tokenizer;
import fr.certu.chouette.exchange.gtfs.refactor.exporter.TransferExporter;
import fr.certu.chouette.exchange.gtfs.refactor.importer.Context;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer;
import fr.certu.chouette.exchange.gtfs.refactor.model.GtfsTransfer.TransferType;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:chouetteContext.xml" })
public class GtfsExportTokenizerTests extends AbstractTestNGSpringContextTests
{
   private static final Logger logger = Logger.getLogger(GtfsExportTokenizerTests.class);


   @Test(groups = { "Helper" }, description = "test tokenize")
   public void verifyTokenize() throws ChouetteException
   {

      String test = "123,456,toto,\"tutu\",\"test\"\"autre\""; 

      List<String> result = Tokenizer.tokenize(test);
      Assert.assertEquals(result.size(), 5 , "string should be cut in 5 token");
      Assert.assertEquals(result.get(0), "123", "1st value must be correctly set");
      Assert.assertEquals(result.get(1), "456", "2nd value must be correctly set");
      Assert.assertEquals(result.get(2), "toto", "3rd value must be correctly set");
      Assert.assertEquals(result.get(3), "tutu", "4th value must be correctly set");
      Assert.assertEquals(result.get(4), "test\"autre", "5th value must be correctly set");

   }

   @Test(groups = { "Helper" }, description = "test untokenize")
   public void verifyUntokenize() throws ChouetteException
   {

      List<String> test = new ArrayList<>(); 
      test.add("123");
      test.add("456");
      test.add("toto");
      test.add("tutu");
      test.add("test\"autre");

      String result = Tokenizer.untokenize(test);
      Assert.assertEquals(result, "123,456,toto,tutu,\"test\"\"autre\"" , "string should be correctly built");

   }


}
