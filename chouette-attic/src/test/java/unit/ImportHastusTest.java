package unit;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.geographie.IConvertisseur;
import fr.certu.chouette.service.importateur.IReducteur;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.CodeIncident;
import fr.certu.chouette.service.importateur.multilignes.hastus.commun.ServiceException;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class ImportHastusTest {
	
	private static final Logger                 logger                = Logger.getLogger(ImportHastusTest.class);
	private static final String                 REP                   = "target/test-classes";
	private              ILecteurPrincipal      lecteurCSV;
	private              IImportateur           importateur           = null;
	private              IConvertisseur         convertisseur         = null;
	private              IReducteur             reducteur             = null;
	
	@BeforeSuite
	protected void initialise() {
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();
		lecteurCSV = (ILecteurPrincipal)applicationContext.getBean("lecteurCSVHastus");
		importateur = (IImportateur)applicationContext.getBean("importateur");
		convertisseur = (IConvertisseur)applicationContext.getBean("convertisseur");
		reducteur = (IReducteur)applicationContext.getBean("reducteur");
	}
	
	@Test(groups="tests unitaires", description="fichier de test Hastus")
	public void importHastusTest() {
		//importer("ImportHastus_1moisV7.csv");
		//importer("ImportHastus_1moisV7bis.csv");
		//importer("donneesHastus_2007_3009.csv");
		importer("donness_incremental.csv");
	}
	
	private void importer(String nom) {
		String nouveauNom = nom;
		if (new File(nom).exists())
			nouveauNom = reducteur.reduire(nom, true);
		else {
			nom = REP + File.separator + nom;
			if (new File(nom).exists())
				nouveauNom = reducteur.reduire(nom, true);
		}
		try {
			lecteurCSV.lire(nouveauNom);
		}
		catch(ServiceException e) {
			if (CodeIncident.FILE_NOT_FOUND.equals(e.getCode()))
				lecteurCSV.lire(REP + File.separator + nouveauNom);
			else
				throw e;
		}
		List<ILectureEchange> lecturesEchange = lecteurCSV.getLecturesEchange();
		logger.debug("IMPORT ...");
		for (ILectureEchange lectureEchange : lecturesEchange) {
			try {
				if (lectureEchange.getLigne().getName().startsWith("NAVETTE"))
					continue;
				if (lectureEchange.getLigne().getName().startsWith("Etude"))
					continue;
				
				List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
				if ((tableauxMarche == null) || (tableauxMarche.size() <= 0))
					logger.warn("LA LIGNE "+lectureEchange.getLigne().getNumber()+" N'A PAS DE CALENDRIERS.");
				
				importateur.importer(false, lectureEchange, true);
				lectureEchange.getHoraires().clear();
				lectureEchange.getCourses().clear();
				lectureEchange.getMissions().clear();
				lectureEchange.getItineraires().clear();
				lectureEchange.getArrets().clear();
			}
			catch(Exception e) {
				logger.error("ERREUR D'IMPORT.", e);
				System.exit(1);
			}
		}
		convertisseur.deLambertAWGS84();
	}
}
