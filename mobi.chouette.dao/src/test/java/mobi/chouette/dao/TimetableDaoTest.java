package mobi.chouette.dao;

import java.io.File;
import java.util.Collection;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.model.Line;
import mobi.chouette.model.Timetable;
import mobi.chouette.persistence.hibernate.ContextHolder;

public class TimetableDaoTest extends Arquillian {
	@EJB
	TimetableDAO timetableDao;

	@Deployment
	public static WebArchive createDeployment() {

		try {
			WebArchive result;
			File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
					.withTransitivity().asFile();

			result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
					.addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
			return result;
		} catch (RuntimeException e) {
			System.out.println(e.getClass().getName());
			throw e;
		}

	}

	@Test
	public void getTimetableForLineTest()
	{
		ContextHolder.setContext("chouette_gui"); // set tenant schema
		
		Line l = new Line();
		l.setId(1L);
		
	
		Collection<Timetable> timetableForLine = timetableDao.getTimetableForLine(l);
		Assert.assertNotNull(timetableForLine);
	}

}
