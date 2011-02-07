package unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.AndClause;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.critere.ScalarClause;
import fr.certu.chouette.critere.VectorClause;
import fr.certu.chouette.modele.Correspondance;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ICorrespondanceManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ServicesZone extends AbstractTestNGSpringContextTests { 
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(ServicesZone.class);
	
	private IPositionGeographiqueManager positionGeographiqueManager;
	private ICorrespondanceManager correspondanceManager;
	private Random random = new Random();
	

	public ServicesZone() {
	}

	@BeforeMethod
	protected void getBeans() throws Exception
	{
		positionGeographiqueManager = ( IPositionGeographiqueManager)applicationContext.getBean( "positionGeographiqueManager");
		correspondanceManager = ( ICorrespondanceManager)applicationContext.getBean( "correspondanceManager");
	}
	
	
	
	private PositionGeographique creerPositionGeo( String cle)
	{
		PositionGeographique positionGeographique = new PositionGeographique();
		try
		{
			if ( random.nextBoolean())
				BeanUtils.copyProperties( positionGeographique, GenerateurDonnee.creerArretPhysique(cle));
			else
				BeanUtils.copyProperties( positionGeographique, GenerateurDonnee.creerZone(cle, random.nextBoolean()?ChouetteAreaType.STOPPLACE:ChouetteAreaType.COMMERCIALSTOPPOINT));
		}
		catch( Exception e)
		{
			logger.error( e.getMessage(), e);
			throw new RuntimeException( e);
		}
		return positionGeographique;
	}
	
	@Test(groups="tests unitaires", 
			description="controle de la destruction des correspondances d'une position détruite")
	public void selectionCorrespondances()
	{
		PositionGeographique positionA = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionB = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionC = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionD = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		positionGeographiqueManager.creer( positionA);
		positionGeographiqueManager.creer( positionB);
		positionGeographiqueManager.creer( positionC);
		positionGeographiqueManager.creer( positionD);
		
		Correspondance AB = GenerateurDonnee.creerCorrespondance();
		AB.setIdDepart( positionA.getId());
		AB.setIdArrivee( positionB.getId());
		Correspondance DC = GenerateurDonnee.creerCorrespondance();
		DC.setIdDepart( positionD.getId());
		DC.setIdArrivee( positionC.getId());
		Correspondance BC = GenerateurDonnee.creerCorrespondance();
		BC.setIdDepart( positionB.getId());
		BC.setIdArrivee( positionC.getId());
		
		correspondanceManager.creer( AB);
		correspondanceManager.creer( DC);
		correspondanceManager.creer( BC);
		
		Collection<Long> positionIds = new ArrayList<Long>();
		positionIds.add( positionA.getId());
		positionIds.add( positionD.getId());
		
		List<Correspondance> corresp = correspondanceManager.selectionParPositions(positionIds);
		assert corresp.size()==2;
		
		
		positionGeographiqueManager.supprimer( positionA.getId());
		positionGeographiqueManager.supprimer( positionB.getId());
		positionGeographiqueManager.supprimer( positionC.getId());
		positionGeographiqueManager.supprimer( positionD.getId());
		
		try {
			correspondanceManager.lire( AB.getId());
			assert false:"La suppression de position doit entrainer celle de leurs correspdces ";
		} catch( ServiceException e) {
			assert CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()):
				"La sélection ne renvoie pas l'exeception attendue "+CodeIncident.IDENTIFIANT_INCONNU+
				" mais "+e.getCode();
		}
		try {
			correspondanceManager.lire( BC.getId());
			assert false:"La suppression de position doit entrainer celle de leurs correspdces ";
		} catch( ServiceException e) {
			assert CodeIncident.IDENTIFIANT_INCONNU.equals( e.getCode()):
				"La sélection ne renvoie pas l'exeception attendue "+CodeIncident.IDENTIFIANT_INCONNU+
				" mais "+e.getCode();
		}
	}
	
	@Test(groups="tests unitaires", description="sélection des zones ou d'arrets")
	public void selectionArrets() {
		
		PositionGeographique position = GenerateurDonnee.creerZone("", ChouetteAreaType.STOPPLACE);
		
		positionGeographiqueManager.creer(position);
		
		Collection<String> areas = new HashSet<String>();
		
		areas.add(ChouetteAreaType.BOARDINGPOSITION.toString());
		areas.add(ChouetteAreaType.QUAY.toString());
		areas.add(ChouetteAreaType.COMMERCIALSTOPPOINT.toString());
		areas.add(ChouetteAreaType.STOPPLACE.toString());
		
		IClause clause1 = VectorClause.newInClause("areaType", areas);
		
		String debutNom = position.getName().substring(0, position.getName().length() / 2);
		IClause clause2 = ScalarClause.newIlikeClause("name", debutNom);
		
		IClause clause3 = ScalarClause.newEqualsClause("objectId", position.getObjectId());
		
		AndClause maClause = new AndClause();
		maClause.add(clause1);
		maClause.add(clause2);
		maClause.add(clause3);
		
		List<PositionGeographique> positions = positionGeographiqueManager.select(maClause);

		assert positions.size() == 1 : positions.size() + " stopareas trouvés, 1 seul attendu ";
		assert positions.get(0).getId().equals(position.getId());
		
		clause2 = ScalarClause.newIlikeClause("name", "A" + debutNom);
		maClause = new AndClause();
		maClause.add(clause1);
		maClause.add(clause2);
		maClause.add(clause3);
		
		positions = positionGeographiqueManager.select(maClause);
		
		assert positions.size() == 0;
		
		positionGeographiqueManager.supprimer( position.getId());
	}	

	
	@Test(groups="tests unitaires", 
			description="conservation des enfants sur maj du parent")
	public void majParent()
	{
		PositionGeographique positionA = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionB = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionC = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionD = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		
		List<PositionGeographique> liste = new ArrayList<PositionGeographique>();
		liste.add( positionA);
		liste.add( positionB);
		liste.add( positionC);
		liste.add( positionD);
		
		for (PositionGeographique positionGeographique : liste) {
			positionGeographiqueManager.creer( positionGeographique);
		}
		
		positionGeographiqueManager.associerGeoPositions( positionA.getId(), 
				positionB.getId());
		positionGeographiqueManager.associerGeoPositions( positionB.getId(), 
				positionC.getId());
		
		positionB = positionGeographiqueManager.lire( positionB.getId());
		assert positionB.getIdParent().equals( positionA.getId());
		positionC = positionGeographiqueManager.lire( positionC.getId());
		assert positionC.getIdParent().equals( positionB.getId());
		
		positionGeographiqueManager.associerGeoPositions( positionD.getId(), 
				positionB.getId());
		positionB = positionGeographiqueManager.lire( positionB.getId());
		assert positionB.getIdParent().equals( positionD.getId());
		
		List<PositionGeographique> enfants = positionGeographiqueManager.getGeoPositionsDirectementContenues( positionB.getId());
		assert enfants.size()==1;
		assert enfants.get( 0).getId().equals( positionC.getId());
		
		for (PositionGeographique positionGeographique : liste) {
			positionGeographiqueManager.supprimer( positionGeographique.getId());
		}
	}	
	
	@Test(groups="tests unitaires", 
			description="formation d'une structure récursive")
	public void formationRecursive()
	{
		PositionGeographique positionA = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionB = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionC = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique positionD = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		
		List<PositionGeographique> liste = new ArrayList<PositionGeographique>();
		liste.add( positionA);
		liste.add( positionB);
		liste.add( positionC);
		liste.add( positionD);
		
		for (PositionGeographique positionGeographique : liste) {
			positionGeographiqueManager.creer( positionGeographique);
		}
		
		positionGeographiqueManager.associerGeoPositions( positionA.getId(), 
				positionB.getId());
		positionGeographiqueManager.associerGeoPositions( positionB.getId(), 
				positionC.getId());
		
		// A -> B -> C -> A
		try
		{
			positionGeographiqueManager.associerGeoPositions( positionC.getId(), 
					positionA.getId());
			assert false:"La récursivité aurait du être refusée";
		}
		catch( ServiceException e)
		{
			if ( !CodeIncident.RECURSIVITE_INTER_ZONES.equals( e.getCode()))
			{
				assert false:"Le code d'exception devrait signaler la récursivité, code reçu:"+
				e.getCode()+", code attendu:"+CodeIncident.RECURSIVITE_INTER_ZONES;
			}
		}
		
		// A -> A
		try
		{
			positionGeographiqueManager.associerGeoPositions( positionA.getId(), 
					positionA.getId());
			assert false:"La récursivité aurait du être refusée";
		}
		catch( ServiceException e)
		{
			if ( !CodeIncident.RECURSIVITE_INTER_ZONES.equals( e.getCode()))
			{
				assert false:"Le code d'exception devrait signaler la récursivité, code reçu:"+
				e.getCode()+", code attendu:"+CodeIncident.RECURSIVITE_INTER_ZONES;
			}
		}
		
		for (PositionGeographique positionGeographique : liste) {
			positionGeographiqueManager.supprimer( positionGeographique.getId());
		}
	}	
	
	@Test(groups="tests unitaires", 
			description="gestion des zones")
	public void zones_incompatibles()
	{
		PositionGeographique zoneA = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique zoneAB = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique zoneAC = GenerateurDonnee.creerArretPhysique( "");
		 
		PositionGeographique zoneH = GenerateurDonnee.creerZone( "", ChouetteAreaType.STOPPLACE);
		PositionGeographique zoneHI = GenerateurDonnee.creerArretPhysique( "");
		PositionGeographique zoneHJ = GenerateurDonnee.creerZone( "", ChouetteAreaType.COMMERCIALSTOPPOINT);
		PositionGeographique zoneHJK = GenerateurDonnee.creerArretPhysique( "");
		
		
		positionGeographiqueManager.creer(zoneA);
		positionGeographiqueManager.creer(zoneAB);
		positionGeographiqueManager.creer( zoneAC);
	    positionGeographiqueManager.associerGeoPositions( zoneA.getId(), zoneAB.getId());
	    positionGeographiqueManager.associerGeoPositions( zoneA.getId(), zoneAC.getId());
		
		positionGeographiqueManager.creer(zoneH);
		positionGeographiqueManager.creer(zoneHI);
		positionGeographiqueManager.creer( zoneHJ);
		positionGeographiqueManager.creer(zoneHJK);
	    
	    try {
	    	positionGeographiqueManager.associerGeoPositions( zoneHJK.getId(), zoneHJ.getId());
	    	assert false: "bloquer l'asso arret physique contient commercialstop";
	    } catch( ServiceException e) 
	    {
	    	assert e.getCode().equals( CodeIncident.ASSOCIATION_ZONES_INVALIDE):
	    		"exception invalide "+e.getMessage()+", code "+e.getCode();
	    }
	    try {
	    	positionGeographiqueManager.associerGeoPositions( zoneHJK.getId(), zoneH.getId());
	    	assert false: "bloquer l'asso arret physique contient stopplace";
	    } catch( ServiceException e) 
	    {
	    	assert e.getCode().equals( CodeIncident.ASSOCIATION_ZONES_INVALIDE):
	    		"exception invalide "+e.getMessage()+", code "+e.getCode();
	    }
	    try {
	    	positionGeographiqueManager.associerGeoPositions( zoneHI.getId(), zoneH.getId());
	    	assert false: "bloquer l'asso commercialstop contient stopplace";
	    } catch( ServiceException e) 
	    {
	    	assert e.getCode().equals( CodeIncident.ASSOCIATION_ZONES_INVALIDE):
	    		"exception invalide "+e.getMessage()+", code "+e.getCode();
	    }

	    positionGeographiqueManager.associerGeoPositions( zoneH.getId(), zoneHI.getId());
	    positionGeographiqueManager.associerGeoPositions( zoneH.getId(), zoneHJ.getId());
	    positionGeographiqueManager.associerGeoPositions( zoneHJ.getId(), zoneHJK.getId());
	   
	    PositionGeographique zoneLue = positionGeographiqueManager.lire( zoneH.getId());
	    List<PositionGeographique> enfants = positionGeographiqueManager.getGeoPositionsDirectementContenues( zoneH.getId());
	    assert enfants.size()==2;
	    zoneLue = positionGeographiqueManager.lire( zoneHJ.getId());
	    enfants = positionGeographiqueManager.getGeoPositionsDirectementContenues( zoneHJ.getId());
	    assert enfants.size()==1;
	    
	 
	    try {
		    positionGeographiqueManager.associerGeoPositions( zoneAB.getId(), zoneH.getId());
	    	assert false: "bloquer la profondeur est dépassée";
	    } catch( ServiceException e) 
	    {
	    	assert e.getCode().equals( CodeIncident.PROFONDEUR_ZONES_INVALIDE):
	    		"exception invalide "+e.getMessage()+", code "+e.getCode();
	    }
		Set<Long> parentsIdsAttendus = new HashSet<Long>();
		parentsIdsAttendus.add( zoneA.getId());
		parentsIdsAttendus.add( zoneH.getId());
		parentsIdsAttendus.add( zoneHJ.getId());
		parentsIdsAttendus.add( zoneHJK.getId());
	    positionGeographiqueManager.associerGeoPositions( zoneA.getId(), zoneH.getId());
	    
	    List<PositionGeographique> parents = positionGeographiqueManager.getGeoPositionsParentes( zoneHJK.getId());
	    assert parents.size()==4;
	    
		Set<Long> parentsIdsTrouves = new HashSet<Long>();
		for (PositionGeographique parentTrouve : parents) {
			parentsIdsTrouves.add( parentTrouve.getId());
		}
		parentsIdsTrouves.removeAll( parentsIdsAttendus);
		assert parentsIdsTrouves.size()==0:"les zones parentes suivantes sont inattendues "+parentsIdsTrouves;
	}
}
