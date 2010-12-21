package fonctionnel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IItineraireManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.exportateur.monoitineraire.csv.IExportHorairesManager;
import fr.certu.chouette.service.exportateur.monoitineraire.csv.impl.EcrivainCSV;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.importateur.monoitineraire.csv.IImportHorairesManager;
import fr.certu.chouette.service.importateur.monoitineraire.csv.impl.LecteurCSV;
import fr.certu.chouette.service.importateur.monoligne.ILecteurCSV;

public class TestExportHorairesItineraire 
{
	private ILecteurCSV lecteurCSV;
	private IImportateur importateur = null;
	private ILigneManager ligneManager;
	private IItineraireManager itineraireManager;
	private IExportHorairesManager exportHorairesManager;
	private IImportHorairesManager importHorairesManager;
	
	private final static String REP = "target/test-classes";
	//private final static String SEP = "$";

	
	@BeforeSuite
	protected void setUp() throws Exception 
	{
		
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();	
		lecteurCSV = (ILecteurCSV)applicationContext.getBean("lecteurCSV");
		importateur = (IImportateur)applicationContext.getBean("importateur");
		ligneManager = (ILigneManager)applicationContext.getBean("ligneManager");
		itineraireManager = (IItineraireManager)applicationContext.getBean("itineraireManager");
		importHorairesManager = (IImportHorairesManager)applicationContext.getBean("importHorairesManager");
		exportHorairesManager = (IExportHorairesManager)applicationContext.getBean("exportHorairesManager");
	}
	
	@Test(groups="tests fonctionnels", description="verification du fonctionnement de l'export des horaires d'un itineraire")
	public void test() {
		
		String nom = "RERA_3itineraires_4aller.csv";
		try
		{
			lecteurCSV.lire( nom);
		}
		catch( ServiceException e)
		{
			if ( CodeIncident.ERR_CSV_NON_TROUVE.equals( e.getCode()))
				lecteurCSV.lire(REP + File.separator + nom);
			else
				throw e;
		}
		String codeLigne = "ligneDeTest";
		
		ILectureEchange lectureEchange = lecteurCSV.getLectureEchange();
		lectureEchange.getLigne().setRegistrationNumber( codeLigne);
		importateur.importer( true, lectureEchange);
		
		List<Itineraire> itineraires = ligneManager.getItinerairesLigne(ligneManager.getLigneParRegistration(codeLigne).getId());
		
		if(itineraires.size()>0){
			long idItineraire = itineraires.get(0).getId();
			List<String[]> donneesOut = exportHorairesManager.exporter(idItineraire);
			File fichierOut = new File("exportItineraire.csv");
			EcrivainCSV ec = new EcrivainCSV();
			try {
				ec.ecrire(donneesOut, fichierOut);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Comparator<Horaire> horaireComparator = new Comparator<Horaire>(){
				public int compare(Horaire o1, Horaire o2) {
					if(o1.getIdCourse()!=o2.getIdCourse()){
						return (int)(o1.getIdCourse()-o2.getIdCourse());
					}
					if(o1.getIdArret()!=o2.getIdArret()){
						return (int)(o1.getIdArret()-o2.getIdArret());
					}
					if(!o1.getArrivalTime().equals(o2.getArrivalTime())){
						return o1.getArrivalTime().compareTo(o2.getArrivalTime());
					}
					if(!o1.getDepartureTime().equals(o2.getDepartureTime())){
						return o1.getDepartureTime().compareTo(o2.getDepartureTime());
					}
					return 0;
				}
			};
			
			List<Horaire> horairesExportes = itineraireManager.getHorairesItineraire(idItineraire);
			Collections.sort(horairesExportes,horaireComparator);
			
			List<String[]> donneesIn = null;
			File fichierIn = new File("exportItineraire.csv");
			LecteurCSV lc = new LecteurCSV();
			try 
			{
				donneesIn = lc.lire(fichierIn);
			} 
			catch (Exception e) {
			//ez catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			importHorairesManager.importer(donneesIn);
			
			List<Horaire> horairesImportes = itineraireManager.getHorairesItineraire(idItineraire);
			Collections.sort(horairesImportes,horaireComparator);
			
			assert(horairesExportes.equals(horairesImportes));
			
			//creation d'un fichier modifié
			List<String[]> donneesModif = new ArrayList<String[]>(donneesIn);
			donneesModif.get(2)[2] = "07:00:00";
			donneesModif.get(2)[3] = "07:00:00";
			File fichierModif = new File("exportItineraireModifie.csv");
			try {
				ec.ecrire(donneesModif, fichierModif);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			donneesIn = null;
			fichierIn = new File("exportItineraireModifie.csv");
			lc = new LecteurCSV();
			try 
			{
				donneesIn = lc.lire(fichierIn);
			} 
			catch (Exception e) {
				//catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			importHorairesManager.importer(donneesIn);
			
			horairesImportes = itineraireManager.getHorairesItineraire(idItineraire);
			Collections.sort(horairesImportes,horaireComparator);
			
			assert(!horairesExportes.equals(horairesImportes));
		}
		
		ligneManager.supprimer( ligneManager.getLigneParRegistration( codeLigne).getId());
	}
}
