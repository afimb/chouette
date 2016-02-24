package mobi.chouette.dao;

import java.io.File;

import javax.ejb.EJB;

import mobi.chouette.model.Line;
import mobi.chouette.persistence.hibernate.ContextHolder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;


public class LineDaoTest extends Arquillian
{
	@EJB 
	LineDAO lineDao;


	@Deployment
	public static WebArchive createDeployment() {

		try
		{
		WebArchive result;
		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("mobi.chouette:mobi.chouette.dao").withTransitivity().asFile();

		result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
				.addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
		return result;
		}
		catch (RuntimeException e)
		{
			System.out.println(e.getClass().getName());
			throw e;
		}

	}
	
	@Test
	public void checkSequence()
	{
		ContextHolder.setContext("chouette_gui"); // set tenant schema
		for (int i = 0; i < 300; i++)
		{
			Line l = createLine();
			lineDao.create(l);
			Assert.assertEquals(l.getId(), Long.valueOf(i+1),"line id");
		}
	}
	
	private int id = 1;
	private Line createLine()
	{
		Line l = new Line();
		l.setName("toto");
		l.setObjectId("test:Line:"+id);
		id++;
		
		return l;
	}
	

}
