package unit;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;
import fr.certu.chouette.service.xml.ILecteurFichierXML;
import java.io.File;
import org.apache.log4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ImportXML extends AbstractTestNGSpringContextTests {
	
	private static final Logger             logger            = Logger.getLogger(ImportXML.class);
	private              ILecteurFichierXML lecteurFichierXML = null;
	private              ILecteurEchangeXML lecteurEchangeXML = null;
	private              IImportateur       importateur       = null;
	
	@BeforeMethod
	protected void getBeans()
        {
            lecteurFichierXML = (ILecteurFichierXML) applicationContext.getBean("lecteurFichierXML");
            lecteurEchangeXML = (ILecteurEchangeXML) applicationContext.getBean("lecteurEchangeXML");
            importateur = (IImportateur) applicationContext.getBean("importateur");
	}
	
	@Test(groups="tests unitaires", description="Import de données XML / Chouette.")
	public void importer() 
	{
		File file = new File("target/test-classes/Alsace");
		if (!file.exists()) 
		{
			logger.error("Il n'y a pas de fichier Alsace.");
			return;
		}
		if (!file.isDirectory()) 
		{
			logger.error("Alsace n'est pas un repertoire.");
			return;
		}
		String path = "";
		try 
		{
			path = file.getCanonicalPath();
		}
		catch(Exception e) 
		{
			logger.error("Problème lors de la lecture du filePath de "+file.getName());
			return;
		}
		if (!path.endsWith(File.separator))
			path += File.separator;
		String[] fileNames = file.list();
		if ((fileNames == null) || (fileNames.length == 0))
			logger.error("Le repertoire alsace est vide.");
		for (int i = 0; i < fileNames.length; i++) 
		{
			String fileName = fileNames[i];
			if (fileName.indexOf(File.separator) >= 0)
				fileName = fileName.substring(fileName.lastIndexOf(File.separator)+1);
			importer(path+fileNames[i]);
		}
	}
	
	public void importer(String fileName) 
	{
		ChouettePTNetworkTypeType chouettePTNetworkType = null;
		try 
		{
			logger.debug("IMPORT XML DU FICHIER "+fileName);
			chouettePTNetworkType = lecteurFichierXML.lire(fileName, true);
			logger.debug("CREATION DU CHOUETTEPTNETWORKTYPE REUSSI");
		}
		catch (Exception e) 
		{
			logger.error("Erreur de lecture du fichier "+fileName+", msg = " + e.getMessage(), e);
			return;
		}
		ILectureEchange lectureEchange = lecteurEchangeXML.lire(chouettePTNetworkType);
		try 
		{
			importateur.importer(true, lectureEchange);
		}
		catch (ServiceException e) 
		{
			logger.error("Impossible de créer la ligne en base, msg = " + e.getMessage(), e);
			return;
		}
	}
}
