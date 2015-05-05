package mobi.chouette.service;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import lombok.extern.log4j.Log4j;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class JobServiceManagerTest extends Arquillian {

    @EJB
	JobServiceManager jobServiceManager;

	@Deployment
	public static WebArchive createDeployment() {

		WebArchive result;

		File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
				.resolve("mobi.chouette:mobi.chouette.service:3.0.0").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war")
				.addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files)
				.addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;

	}
	
	@Test (groups = { "JobServiceManager" }, description = "Check wrng referential")
	public void createWrongJobReferential()
	{
		String referential = "toto";
		String action ="action";
		String type = "type";
		Map<String, InputStream> inputStreamsByName = null;
		try {
			jobServiceManager.create(referential, action, type, inputStreamsByName);
		} catch (RequestServiceException e) {
			Assert.assertEquals(e.getCode(), ServiceExceptionCode.INVALID_REQUEST.name(),"code expected");
			Assert.assertEquals(e.getRequestCode(), RequestExceptionCode.UNKNOWN_REFERENTIAL.name(),"request code expected");
			return;
			
		} catch (Exception e) {
			Assert.assertTrue(false,"RequestServiceException required");
		}
		Assert.assertTrue(false,"exception required");
		
	}
	@Test (groups = { "JobServiceManager" }, description = "Check wrong action")
	public void createWrongJobAction()
	{
		String referential = "chouette_gui";
		String action ="action";
		String type = "type";
		Map<String, InputStream> inputStreamsByName = new HashMap<>();
		try {
			jobServiceManager.create(referential, action, type, inputStreamsByName);
		} catch (RequestServiceException e) {
			Assert.assertEquals(e.getCode(), ServiceExceptionCode.INVALID_REQUEST.name(),"code expected");
			Assert.assertEquals(e.getRequestCode(), RequestExceptionCode.UNKNOWN_ACTION.name(),"request code expected");
			return;
			
		} catch (Exception e) {
			Assert.assertTrue(false,"RequestServiceException required");
		}
		Assert.assertTrue(false,"exception required");
		
	}

}
