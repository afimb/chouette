package mobi.chouette.dao;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

import javax.ejb.EJB;

import mobi.chouette.model.FootNoteAlternativeText;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.Assert;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.Footnote;
import mobi.chouette.persistence.hibernate.ContextHolder;

@Log4j
public class FootnoteDaoTest extends Arquillian {
	@EJB 
	FootnoteDAO footnoteDao;

	@EJB
	FootnoteAlternativeTextDAO footNoteAlternativeTextDao;

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
	public void checkWriteReadFootnote() {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema
			
			Footnote f = new Footnote();
			f.setObjectId("XYZ:Notice:"+UUID.randomUUID());
			f.setObjectVersion(1);
			f.setCode("Code");
			f.setLabel("Label");
			
			footnoteDao.create(f);

			
			Footnote found = footnoteDao.findByObjectId(f.getObjectId());
			
			Assert.assertNotNull(found);
			Assert.assertEquals(found.getCode(),f.getCode());
			Assert.assertEquals(found.getLabel(), f.getLabel());
			
		} catch (RuntimeException ex) {
			Throwable cause = ex.getCause();
			while (cause != null) {
				log.error(cause);
				if (cause instanceof SQLException)
					traceSqlException((SQLException) cause);
				cause = cause.getCause();
			}
			throw ex;
		}
	}

	@Test
	public void checkWriteReadFootnoteAlternativeText() {
		try {
			ContextHolder.setContext("chouette_gui"); // set tenant schema

			Footnote footnote = new Footnote();
			footnote.setObjectId("XYZ:Notice:"+UUID.randomUUID());
			footnote.setObjectVersion(1);
			footnote.setCode("Code");
			footnote.setLabel("Label");

			footnoteDao.create(footnote);

			FootNoteAlternativeText footNoteAlternativeText = new FootNoteAlternativeText();
			footNoteAlternativeText.setFootnote(footnote);
			footNoteAlternativeText.setObjectId("XYZ:AlternativeText:"+UUID.randomUUID());
			footNoteAlternativeText.setObjectVersion(1);
			footNoteAlternativeText.setLanguage("en");
			footNoteAlternativeText.setText("Alternative Text in english");

			footNoteAlternativeTextDao.create(footNoteAlternativeText);


			FootNoteAlternativeText found = footNoteAlternativeTextDao.findByObjectId(footNoteAlternativeText.getObjectId());

			Assert.assertNotNull(found);
			Assert.assertEquals(found.getLanguage(),footNoteAlternativeText.getLanguage());
			Assert.assertEquals(found.getText(), footNoteAlternativeText.getText());

		} catch (RuntimeException ex) {
			Throwable cause = ex.getCause();
			while (cause != null) {
				log.error(cause);
				if (cause instanceof SQLException)
					traceSqlException((SQLException) cause);
				cause = cause.getCause();
			}
			throw ex;
		}
	}

	private void traceSqlException(SQLException ex) {
		while (ex.getNextException() != null) {
			ex = ex.getNextException();
			log.error(ex);
		}
	}

}
