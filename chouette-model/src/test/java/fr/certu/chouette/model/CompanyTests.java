package fr.certu.chouette.model;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.Company;

@ContextConfiguration(locations = { "classpath:testContext.xml" })
public class CompanyTests extends AbstractTestNGSpringContextTests
{
   
   @Test(groups = { "model" }, description = "check maximum size of fields")
   public void verifyFieldTruncating()
   {
      Company obj = new Company();
      String longString = "long string with more than 256 chrs";
      while (longString.length() < 256) longString += "0123456789";
      obj.setObjectId("toto:Line:"+longString);
      Assert.assertEquals(obj.getObjectId().length(),255, "objectId should be truncated");
      obj.setName(longString);
      Assert.assertEquals(obj.getName().length(),255, "name should be truncated");
      obj.setRegistrationNumber(longString);
      Assert.assertEquals(obj.getRegistrationNumber().length(),255, "registration number should be truncated");
      obj.setShortName(longString);
      Assert.assertEquals(obj.getShortName().length(),255, "shortName should be truncated");
      obj.setOrganisationalUnit(longString);
      Assert.assertEquals(obj.getOrganisationalUnit().length(),255, "organisationalUnit should be truncated");
      obj.setOperatingDepartmentName(longString);
      Assert.assertEquals(obj.getOperatingDepartmentName().length(),255, "operatingDepartmentName should be truncated");
      obj.setUrl(longString);
      Assert.assertEquals(obj.getUrl().length(),255, "url should be truncated");
      obj.setCode(longString);
      Assert.assertEquals(obj.getCode().length(),255, "code should be truncated");
      obj.setPhone(longString);
      Assert.assertEquals(obj.getPhone().length(),255, "phone should be truncated");
      obj.setFax(longString);
      Assert.assertEquals(obj.getFax().length(),255, "fax should be truncated");
      obj.setEmail(longString);
      Assert.assertEquals(obj.getEmail().length(),255, "email should be truncated");
      obj.setTimeZone(longString);
      Assert.assertEquals(obj.getTimeZone().length(),255, "timeZone should be truncated");
      obj.setObjectId("toto:Company:"+longString);
      Assert.assertEquals(obj.getObjectId().length(),255, "objectId should be truncated");
   }

}
