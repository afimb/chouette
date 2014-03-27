package fr.certu.chouette.command.importTest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.json.JSONObject;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import fr.certu.chouette.dao.ChouetteDriverManagerDataSource;
import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.gui.command.Command;
import fr.certu.chouette.plugin.model.CompilanceCheckTask;
import fr.certu.chouette.plugin.model.ImportTask;
import fr.certu.chouette.plugin.model.Organisation;
import fr.certu.chouette.plugin.model.Referential;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:/chouetteContext.xml" })
public class ImportTest extends AbstractTestNGSpringContextTests
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
      Organisation org = null;
      Referential ref = null;
      ChouetteDriverManagerDataSource chouetteDataSource = (ChouetteDriverManagerDataSource) applicationContext
            .getBean("chouetteDataSource");
      IDaoTemplate<Referential> referentialDao = (IDaoTemplate<Referential>) applicationContext
            .getBean("referentialDao");
      IDaoTemplate<Organisation> organisationDao = (IDaoTemplate<Organisation>) applicationContext
            .getBean("organisationDao");
      Filter filter = Filter.getNewEqualsFilter("slug", chouetteDataSource.getDatabaseSchema());
      List<Referential> refs = referentialDao.select(filter);
      if (refs.isEmpty())
      {
         List<Organisation> orgs = organisationDao.getAll();
         if (orgs.isEmpty())
         {
            org = new Organisation();
            org.setName("test");
            organisationDao.save(org);
         }
         else
         {
            org = orgs.get(0);
         }
         ref = new Referential();
         ref.setName("test");
         ref.setSlug(chouetteDataSource.getDatabaseSchema());
         ref.setOrganisation(org);
         ref.setPrefix("test");
         referentialDao.save(ref);
         flush();
         referentialDao.detach(ref);
      }
      else
      {
         ref = refs.get(0);
      }
      return ref;
   }

   @SuppressWarnings("unchecked")
   private ImportTask prepareImportTask(Referential ref, String filename, boolean save)
   {
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      IDaoTemplate<CompilanceCheckTask> compilanceDao = (IDaoTemplate<CompilanceCheckTask>) applicationContext
            .getBean("validationDao");
      ImportTask importTask = new ImportTask();
      importTask.setReferential(ref);
      JSONObject parameters = new JSONObject();
      parameters.put("format", "NEPTUNE");
      parameters.put("file_path", filename);
      parameters.put("no_save", !save);
      importTask.setParameters(parameters);
      CompilanceCheckTask compilanceCheckTask = new CompilanceCheckTask();
      importTask.setCompilanceCheckTask(compilanceCheckTask);
      compilanceCheckTask.setImportTask(importTask);
      compilanceCheckTask.setReferential(ref);
      importDao.save(importTask);
      compilanceDao.save(compilanceCheckTask);
      flush();
      importDao.detach(importTask);
      compilanceDao.detach(compilanceCheckTask);
      return importTask;
   }

   @SuppressWarnings("unchecked")
   private ImportTask prepareImportGtfsTask(Referential ref, String filename)
   {
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      IDaoTemplate<CompilanceCheckTask> compilanceDao = (IDaoTemplate<CompilanceCheckTask>) applicationContext
            .getBean("validationDao");
      ImportTask importTask = new ImportTask();
      importTask.setReferential(ref);
      JSONObject parameters = new JSONObject();
      parameters.put("format", "Gtfs");
      parameters.put("file_path", filename);
      parameters.put("no_save", true);
      parameters.put("object_id_prefix", "NINOXE");
      parameters.put("max_distance_for_connection_link", "0");
      parameters.put("max_distance_for_commercial", "0");
      parameters.put("ignore_end_chars", "0");
      parameters.put("ignore_last_word", "0");
      importTask.setParameters(parameters);
      CompilanceCheckTask compilanceCheckTask = new CompilanceCheckTask();
      importTask.setCompilanceCheckTask(compilanceCheckTask);
      compilanceCheckTask.setImportTask(importTask);
      compilanceCheckTask.setReferential(ref);
      importDao.save(importTask);
      compilanceDao.save(compilanceCheckTask);
      flush();
      importDao.detach(importTask);
      compilanceDao.detach(compilanceCheckTask);
      return importTask;
   }

   @SuppressWarnings("unchecked")
   private ImportTask prepareImportNetexTask(Referential ref, String filename)
   {
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      IDaoTemplate<CompilanceCheckTask> compilanceDao = (IDaoTemplate<CompilanceCheckTask>) applicationContext
            .getBean("validationDao");
      ImportTask importTask = new ImportTask();
      importTask.setReferential(ref);
      JSONObject parameters = new JSONObject();
      parameters.put("format", "Netex");
      parameters.put("file_path", filename);
      parameters.put("no_save", true);
      importTask.setParameters(parameters);
      CompilanceCheckTask compilanceCheckTask = new CompilanceCheckTask();
      importTask.setCompilanceCheckTask(compilanceCheckTask);
      compilanceCheckTask.setImportTask(importTask);
      compilanceCheckTask.setReferential(ref);
      importDao.save(importTask);
      compilanceDao.save(compilanceCheckTask);
      flush();
      importDao.detach(importTask);
      compilanceDao.detach(compilanceCheckTask);
      return importTask;
   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "imports" }, description = "import should find zip")
   public void verifyImportZipFound()
   {
      Command.setBeanFactory(applicationContext);
      Command.initDao();
      Referential ref = prepareReferential();
      ImportTask importTask = prepareImportTask(ref, "src/test/data/neptune.zip",true);
      Command command = (Command) applicationContext.getBean("Command");
      String[] args = { "-c", "import", "-id", Long.toString(importTask.getId()) };
      int code = command.execute(args);
      Command.closeDao();
      // check results
      Command.initDao();
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      importTask = importDao.get(importTask.getId());
      Assert.assertEquals(importTask.getStatus(), "processing", "importTask should have status as processing");
      Assert.assertNotNull(importTask.getResult(), "importTask should have a result");
      JSONObject result = importTask.getResult();
      Reporter.log("import result = " + result.toString(2));
      Assert.assertNotNull(importTask.getCompilanceCheckTask(), "importTask should have a compilanceCheckTask");
      CompilanceCheckTask compilanceCheckTask = importTask.getCompilanceCheckTask();
      Assert.assertNotNull(compilanceCheckTask.getResults(), "compilanceCheckTask should have results list");
      Assert.assertFalse(compilanceCheckTask.getResults().isEmpty(), "compilanceCheckTask should have results");
      Assert.assertEquals(code, 0, "command should return 0");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "imports" }, description = "import should load GTFS file")
   public void verifyImportGtfs()
   {
      Command.setBeanFactory(applicationContext);
      Command.initDao();
      Referential ref = prepareReferential();
      ImportTask importTask = prepareImportGtfsTask(ref, "src/test/data/gtfs.zip");
      Command command = (Command) applicationContext.getBean("Command");
      String[] args = { "-c", "import", "-id", Long.toString(importTask.getId()) };
      int code = command.execute(args);
      Command.closeDao();
      // check results
      Command.initDao();
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      importTask = importDao.get(importTask.getId());
      Assert.assertEquals(importTask.getStatus(), "processing", "importTask should have status as processing");
      Assert.assertNotNull(importTask.getResult(), "importTask should have a result");
      JSONObject result = importTask.getResult();
      Reporter.log("import result = " + result.toString(2));
      Assert.assertNotNull(importTask.getCompilanceCheckTask(), "importTask should have a compilanceCheckTask");
      CompilanceCheckTask compilanceCheckTask = importTask.getCompilanceCheckTask();
      Assert.assertNotNull(compilanceCheckTask.getResults(), "compilanceCheckTask should have results list");
      Assert.assertTrue(compilanceCheckTask.getResults().isEmpty(), "compilanceCheckTask should not have results");
      Assert.assertEquals(code, 0, "command should return 0");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "imports" }, description = "import should load NeTEx file")
   public void verifyImportNetex()
   {
      Command.setBeanFactory(applicationContext);
      Command.initDao();
      Referential ref = prepareReferential();
      ImportTask importTask = prepareImportNetexTask(ref, "src/test/data/netex.zip");
      Command command = (Command) applicationContext.getBean("Command");
      String[] args = { "-c", "import", "-id", Long.toString(importTask.getId()) };
      int code = command.execute(args);
      Command.closeDao();
      // check results
      Command.initDao();
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      importTask = importDao.get(importTask.getId());
      Assert.assertEquals(importTask.getStatus(), "processing", "importTask should have status as processing");
      Assert.assertNotNull(importTask.getResult(), "importTask should have a result");
      JSONObject result = importTask.getResult();
      Reporter.log("import result = " + result.toString(2));
      Assert.assertNotNull(importTask.getCompilanceCheckTask(), "importTask should have a compilanceCheckTask");
      CompilanceCheckTask compilanceCheckTask = importTask.getCompilanceCheckTask();
      Assert.assertNotNull(compilanceCheckTask.getResults(), "compilanceCheckTask should have results list");
      Assert.assertTrue(compilanceCheckTask.getResults().isEmpty(), "compilanceCheckTask should not have results");
      Assert.assertEquals(code, 0, "command should return 0");

   }

   @SuppressWarnings("unchecked")
   @Test(groups = { "imports" }, description = "import should not find zip")
   public void verifyImportZipNotFound()
   {
      Command.setBeanFactory(applicationContext);
      Command.initDao();
      Referential ref = prepareReferential();
      ImportTask importTask = prepareImportTask(ref, "src/test/data/bidon.zip",false);
      Command command = (Command) applicationContext.getBean("Command");
      String[] args = { "-c", "import", "-id", Long.toString(importTask.getId()) };
      int code = command.execute(args);
      Command.closeDao();
      // check results
      Command.initDao();
      IDaoTemplate<ImportTask> importDao = (IDaoTemplate<ImportTask>) applicationContext.getBean("importDao");
      importTask = importDao.get(importTask.getId());
      Assert.assertEquals(importTask.getStatus(), "processing", "importTask should have status as processing");
      Assert.assertNotNull(importTask.getResult(), "importTask should have a result");
      JSONObject result = importTask.getResult();
      Reporter.log("import result = " + result.toString(2));
      Assert.assertNotNull(importTask.getCompilanceCheckTask(), "importTask should have a compilanceCheckTask");
      CompilanceCheckTask compilanceCheckTask = importTask.getCompilanceCheckTask();
      Assert.assertNotNull(compilanceCheckTask.getResults(), "compilanceCheckTask should have results list");
      Assert.assertTrue(compilanceCheckTask.getResults().isEmpty(), "compilanceCheckTask should not have results");
      Assert.assertEquals(code, 1, "command should return 1");

   }

}
