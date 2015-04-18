package mobi.chouette.exchange.gtfs.exporter.producer;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.exchange.gtfs.model.exporter.Tokenizer;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsExportTokenizerTests 
{


   @Test(groups = { "Helper" }, description = "test tokenize")
   public void verifyTokenize() throws Exception
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
   public void verifyUntokenize() throws Exception
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
