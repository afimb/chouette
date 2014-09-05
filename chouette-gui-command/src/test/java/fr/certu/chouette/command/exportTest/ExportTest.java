package fr.certu.chouette.command.exportTest;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.gui.command.Command;
import fr.certu.chouette.plugin.model.GuiExport;
import fr.certu.chouette.plugin.model.Referential;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:/chouetteContext.xml" })
public class ExportTest extends AbstractTestNGSpringContextTests 
{

	public void flush()
	{
		EntityManager em = Command.getEntityManager();

		try
		{
			em.getTransaction().begin();
			em.flush();
			em.getTransaction().commit();
		}
		catch (PersistenceException e)
		{
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
			}
			throw e;
		}

	}

	@AfterMethod(alwaysRun = true)
	public void closeDao()
	{
		Command.setBeanFactory(applicationContext);
		Command.closeDao();
	}

	   @SuppressWarnings("unchecked")
	   private Referential prepareReferential()
	   {
	      Referential ref = null;
	      ChouetteDriverManagerDataSource chouetteDataSource = (ChouetteDriverManagerDataSource) applicationContext
	            .getBean("chouetteDataSource");
	      IDaoTemplate<Referential> referentialDao = (IDaoTemplate<Referential>) applicationContext
	            .getBean("referentialDao");
	      Filter filter = Filter.getNewEqualsFilter("slug", chouetteDataSource.getDatabaseSchema());
	      List<Referential> refs = referentialDao.select(filter);
	      Assert.assertFalse(refs.isEmpty(),"Referential must exists before test");
	      ref = refs.get(0);
	      
	      return ref;
	   }

	   @SuppressWarnings("unchecked")
	   private GuiExport prepareExportTask(Referential ref)
	   {
	      IDaoTemplate<GuiExport> exportDao = (IDaoTemplate<GuiExport>) applicationContext.getBean("exportDao");
	      GuiExport exportTask = new GuiExport();
	      exportTask.setReferential(ref);
	      exportDao.save(exportTask);
	      flush();
	      exportDao.detach(exportTask);
	      return exportTask;
	   }

	   @SuppressWarnings("unchecked")
	   @Test(groups = { "exports" }, description = "export should produce zip")
	   public void verifyExportAll()
	   {
		   File f = new File("target/exp_Neptune_test.zip");
		   if (f.exists()) f.delete();
	      Command.setBeanFactory(applicationContext);
	      Command.initDao();
	      Referential ref = prepareReferential();
	      GuiExport exportTask = prepareExportTask(ref);
	      Command command = (Command) applicationContext.getBean("Command");
	      String[] args = { "-c", "export", 
	    		            "-exportid", Long.toString(exportTask.getId()),
	    		            "-format", "NEPTUNE",
	    		            "-o", "line",
	    		            "-outputFile", f.getAbsolutePath()
	    		            };
	      int code = command.execute(args);
	      Command.closeDao();
	      // check results
	      Command.initDao();
	      IDaoTemplate<GuiExport> exportDao = (IDaoTemplate<GuiExport>) applicationContext.getBean("exportDao");
	      exportTask = exportDao.get(exportTask.getId());
	      // Assert.assertEquals(exportTask.getStatus(), "processing", "exportTask should have status as processing");
	      Assert.assertNotNull(exportTask.getResults(), "exportTask should have a result list");
	      Assert.assertFalse(exportTask.getResults().isEmpty(), "exportTask should have a non empty result");
	      Assert.assertEquals(code, 0, "command should return 0");
	      Assert.assertTrue(f.exists(),"export file must exists");
	      f.delete();
	   }
	   
	   @SuppressWarnings("unchecked")
	   @Test(groups = { "exports" }, description = "export should produce zip")
	   public void verifyExportStopArea()
	   {
		   File f = new File("target/exp_Neptune_test.zip");
		   if (f.exists()) f.delete();
	      Command.setBeanFactory(applicationContext);
	      Command.initDao();
	      Referential ref = prepareReferential();
	      GuiExport exportTask = prepareExportTask(ref);
	      Command command = (Command) applicationContext.getBean("Command");
	      String[] args = { "-c", "export", 
	    		            "-exportid", Long.toString(exportTask.getId()),
	    		            "-format", "GTFS",
	    		            "-o", "stoparea",
	    		            "-outputFile", f.getAbsolutePath()
	    		            };
	      int code = command.execute(args);
	      Command.closeDao();
	      // check results
	      Command.initDao();
	      IDaoTemplate<GuiExport> exportDao = (IDaoTemplate<GuiExport>) applicationContext.getBean("exportDao");
	      exportTask = exportDao.get(exportTask.getId());
	      // Assert.assertEquals(exportTask.getStatus(), "processing", "exportTask should have status as processing");
	      Assert.assertNotNull(exportTask.getResults(), "exportTask should have a result list");
	      Assert.assertFalse(exportTask.getResults().isEmpty(), "exportTask should have a non empty result");
	      Assert.assertEquals(code, 0, "command should return 0");
	      Assert.assertTrue(f.exists(),"export file must exists");
	      f.delete();
	   }

}
