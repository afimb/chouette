package fr.certu.chouette.export.metadata;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import fr.certu.chouette.export.metadata.model.Metadata;
import fr.certu.chouette.export.metadata.writer.DublinCoreFileWriter;


/**
 * 
 * @author michel
 */
@ContextConfiguration(locations = { "classpath:testContext.xml" })
public class DublinCoreMetadataTest extends AbstractTestNGSpringContextTests
{
   protected DublinCoreFileWriter fileWriter;

   @BeforeGroups  (groups = { "dc" })
   protected void setUp() throws Exception
   {
      fileWriter = new DublinCoreFileWriter();

   }
   
   
   private Metadata initMetadata() throws MalformedURLException
   {
      Calendar date = Calendar.getInstance();
      date.set(2015,Calendar.JANUARY,15,13,00);
      Calendar start = Calendar.getInstance();
      start.set(2014,Calendar.DECEMBER,01,13,00);
      Calendar end = Calendar.getInstance();
      end.set(2015,Calendar.MARCH,31,13,00);
      Metadata data = new Metadata();
      data.setCreator("the creator");
      data.setDate(date);
      data.setPublisher("the publisher");
      data.setFormat("the format");
      data.getSpatialCoverage().update(3.45678, 45.78965);
      data.getTemporalCoverage().update(start, end);
      data.setTitle("the title");
      data.setRelation(new URL("http://the.relation.com"));
      return data;
      
   }

   @Test(groups = { "dc" }, description = "dublin core with description")
   public void verifyDCMetadataWithoutResource() throws Exception
   {
      Metadata data = initMetadata();
      data.setDescription("the description");

      fileWriter.writePlainFile(data, ".");
      
      File f = new File("./metadata_chouette_dc.xml");
      Assert.assertTrue(f.exists(), "File metadata_chouette_dc.xml should exist");
      String s = FileUtils.readFileToString(f);
      Reporter.log(s);
      String model = FileUtils.readFileToString(new File("src/test/resources/metadata_chouette_dc_1.xml"));
      Assert.assertTrue(s.equals(model), "metadata must be as expected in metadata_chouette_dc_1.xml");
      
   }
   
   
   @Test(groups = { "dc" }, description = "dublin core with resources")
   public void verifyTextMetadataWithResources() throws Exception
   {
      Metadata data = initMetadata();
      data.setDescription("the description");
      data.getResources().add(data.new Resource(null, "ligne 1"));
      data.getResources().add(data.new Resource("réseau 1", "ligne 2"));
      data.getResources().add(data.new Resource("fichier1.xml", "réseau 2", "ligne 3"));
      data.getResources().add(data.new Resource("fichier2.xml", null, "ligne 4"));

      fileWriter.writePlainFile(data, ".");
      
      File f = new File("./metadata_chouette_dc.xml");
      Assert.assertTrue(f.exists(), "File metadata_chouette_dc.xml should exist");
      String s = FileUtils.readFileToString(f);
      Reporter.log(s);
      String model = FileUtils.readFileToString(new File("src/test/resources/metadata_chouette_dc_2.xml"));
      Assert.assertTrue(s.equals(model), "metadata must be as expected in metadata_chouette_dc_2.xml");
      
   }

   
   
   

}
