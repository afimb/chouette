package fr.certu.chouette.model;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
@ContextConfiguration(locations={"classpath:chouetteContext.xml"})
public class NeptuneIdentifiedObjectTests extends AbstractTestNGSpringContextTests
{

	@Test(groups = { "model" } , description = "objectId should be checked")
	public void verifyObjectIdSyntax()
	{
	   String oid = "Test_1:Line:12_abz-Ae-09";
		Assert.assertTrue(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should be valid");
		oid = "Test_1:Lin_e:12_abz-Ae-09";
      Assert.assertTrue(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should be valid");
      oid = "Test/1:Line:12_abz-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test/1:Line:12_abz-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test_1:Line";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test_1:Line:12_abz-Ae-09:toto";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test-1:Line:12_abz-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = ":Line:12_abz-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test_1:Line:12_abzé-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test_1::12_abz-Ae-09";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
      oid = "Test_1:Line:";
      Assert.assertFalse(NeptuneIdentifiedObject.checkObjectId(oid),oid+"should not be valid");
	}

	

}
