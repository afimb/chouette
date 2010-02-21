package unit;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import chouette.schema.ChouettePTNetworkTypeType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.fichier.formatinterne.IAnalyseurEtatInitial;
import fr.certu.chouette.service.fichier.formatinterne.INettoyeurLigne;
import fr.certu.chouette.service.fichier.formatinterne.IProducteurFichier;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.xml.ILecteurEchangeXML;

public class ImportFormatInterneTest 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(ImportFormatInterneTest.class);
	
	private IProducteurFichier producteurFichier;
	private INettoyeurLigne nettoyeurLigne;
	private ILigneManager ligneManager;
	private ILecteurEchangeXML lecteurEchangeXML;
	private IAnalyseurEtatInitial analyseurEtatInitial;
	private ChouetteDriverManagerDataSource managerDataSource;
	
	@BeforeSuite
	public void initialisation()
	{
		ApplicationContext applicationContext = SingletonManager.getApplicationContext();

		producteurFichier = ( IProducteurFichier)applicationContext.getBean( "producteurFichier");
		nettoyeurLigne = ( INettoyeurLigne)applicationContext.getBean( "nettoyeurLigne");
		lecteurEchangeXML = ( ILecteurEchangeXML)applicationContext.getBean( "lecteurEchangeXML");
		ligneManager = ( ILigneManager)applicationContext.getBean( "ligneManager");
		analyseurEtatInitial = ( IAnalyseurEtatInitial)applicationContext.getBean( "analyseurEtatInitial");
		managerDataSource = (ChouetteDriverManagerDataSource)applicationContext.getBean( "dataSourceAdministrateur");
	}
	
	@Test(groups="tests unitaires", description="impact des modifications sur les 2 connexions simultanees")
	public void synchroServices()
	{
		Ligne uneLigne = GenerateurDonnee.creerLigne();
		
	   ligneManager.creer( uneLigne);
	   Ligne ligneLue = ligneManager.lire( uneLigne.getId());
	   assert ligneLue!=null: "Echec enregistrement ligne";
	   
		try
		{
		   Class.forName( managerDataSource.getDriverClassName());
		   Connection connexion = DriverManager.getConnection( managerDataSource.getUrl(), 
				   managerDataSource.getUsername(), 
				   managerDataSource.getPassword());
		   connexion.setAutoCommit( false);
		   
		   nettoyeurLigne.setConnexion( connexion);
		   nettoyeurLigne.nettoyer( uneLigne.getId());
		   
		   connexion.commit();
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
		
		
		try
		{
			ligneLue = ligneManager.lire( uneLigne.getId());
			throw new RuntimeException( "La ligne ne doit plus être visible");
		}
		catch (ServiceException e) {
			if ( !CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()))
			{
				throw new RuntimeException( e);
			}
		}
	   
	}

	@Test(groups="tests unitaires", description="transformation d'une ligne en fichier CSV pour import par COPY")
    public void testProductionFichier()
    {
	   ChouettePTNetworkTypeType chouettePTNetwork = GenerateurDonnee.creerChouettePTNetwork(4, 15, 8);
		try
		{
		   ILectureEchange echange = lecteurEchangeXML.lire(chouettePTNetwork);
			   
		   Class.forName( managerDataSource.getDriverClassName());
		   Connection connexion = DriverManager.getConnection( managerDataSource.getUrl(), 
				   managerDataSource.getUsername(), 
				   managerDataSource.getPassword());
		   connexion.setAutoCommit( false);
		   
		   IEtatDifference etatDifference = analyseurEtatInitial.analyser( echange, connexion);
		   
		   producteurFichier.produire( false, echange, etatDifference, connexion );
		   
		   connexion.rollback();
		}
		catch( Exception e)
		{
			throw new RuntimeException( e);
		}
    }
}
