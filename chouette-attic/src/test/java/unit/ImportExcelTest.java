package unit;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.identification.IDictionaryObjectId;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ImportExcelTest extends AbstractTestNGSpringContextTests{
	
	private static final Logger              logger             = Logger.getLogger(ImportExcelTest.class);
	private static final String              REP                = "target/test-classes";
	private              ILecteurPrincipal   lecteurCSV;
	private              IImportateur        importateur        = null;
	private              IDictionaryObjectId dictionaryObjectId = null;
	
	@BeforeMethod
	protected void initialise() {
		lecteurCSV = (ILecteurPrincipal)applicationContext.getBean("lecteurCSVPrincipal");
		importateur = (IImportateur)applicationContext.getBean("importateur");
		dictionaryObjectId = (IDictionaryObjectId)applicationContext.getBean("dictionaryObjectId");
	}
	
	@Test(groups="tests unitaires", description="fichier de test Excel")
	public void importMultiLignesExcel() {
		String nom = "multi_lignes_excel.csv";
		//String nom = "Exemple_ImportCSV.csv";
		try {
			lecteurCSV.lire(nom);
		}
		catch( ServiceException e) {
			if (CodeIncident.ERR_CSV_NON_TROUVE.equals(e.getCode()))
				lecteurCSV.lire(REP + File.separator + nom);
			else
				throw e;
		}
		List<ILectureEchange> lecturesEchange = lecteurCSV.getLecturesEchange();
		Map<String, String> oldTableauxMarcheObjectIdParRef = new HashMap<String, String>();
		Map<String, String> oldPositionsGeographiquesObjectIdParRef = new HashMap<String, String>();
		Map<String, String> oldObjectIdParOldObjectId = new HashMap<String, String>();
		for (ILectureEchange lectureEchange : lecturesEchange) {
			try {
				List<TableauMarche> tableauxMarche = lectureEchange.getTableauxMarche();
				for (TableauMarche tableauMarche : tableauxMarche)
					if (oldTableauxMarcheObjectIdParRef.get(tableauMarche.getComment()) != null)
						tableauMarche.setObjectId(oldTableauxMarcheObjectIdParRef.get(tableauMarche.getComment()));
				lectureEchange.setTableauxMarche(tableauxMarche);
				
				List<PositionGeographique> positionsGeographiques = lectureEchange.getZonesCommerciales();
				for (PositionGeographique positionGeographique : positionsGeographiques) {
					if (oldPositionsGeographiquesObjectIdParRef.get(positionGeographique.getName()) != null)
						positionGeographique.setObjectId(oldPositionsGeographiquesObjectIdParRef.get(positionGeographique.getName()));
				}
				lectureEchange.setZonesCommerciales(positionsGeographiques);
				
				List<String> objectIdZonesGeneriques = lectureEchange.getObjectIdZonesGeneriques();
				List<String> tmpObjectIdZonesGeneriques = new ArrayList<String>();
				for (String objectId : objectIdZonesGeneriques)
					if (oldObjectIdParOldObjectId.get(objectId) == null)
						tmpObjectIdZonesGeneriques.add(objectId);
					else
						tmpObjectIdZonesGeneriques.add(oldObjectIdParOldObjectId.get(objectId));
				lectureEchange.setObjectIdZonesGeneriques(tmpObjectIdZonesGeneriques);
				
				Map<String, String> zoneParenteParObjectId = lectureEchange.getZoneParenteParObjectId();
				Map<String, String> newZoneParenteParObjectId = new HashMap<String,String>();
				for (String objId : zoneParenteParObjectId.keySet()) {
					String commObjectId = zoneParenteParObjectId.get(objId);
					String newCommObjectId = oldObjectIdParOldObjectId.get(commObjectId);
					if (newCommObjectId == null)
						newZoneParenteParObjectId.put(objId, commObjectId);
					else
						newZoneParenteParObjectId.put(objId, newCommObjectId);
				}
				lectureEchange.setZoneParenteParObjectId(newZoneParenteParObjectId);
				
				importateur.importer(true, lectureEchange);
				
				Map<String, String> _oldTableauxMarcheObjectIdParRef = dictionaryObjectId.getTableauxMarcheObjectIdParRef();
				for (String key : _oldTableauxMarcheObjectIdParRef.keySet())
					oldTableauxMarcheObjectIdParRef.put(key, _oldTableauxMarcheObjectIdParRef.get(key));
				
				Map<String, String> _oldPositionsGeographiquesObjectIdParRef = dictionaryObjectId.getPositionsGeographiquesObjectIdParRef();
				for (String key : _oldPositionsGeographiquesObjectIdParRef.keySet())
					oldPositionsGeographiquesObjectIdParRef.put(key, _oldPositionsGeographiquesObjectIdParRef.get(key));
				
				Map<String, String> _oldObjectIdParOldObjectId = dictionaryObjectId.getObjectIdParOldObjectId();
				for (String key : _oldObjectIdParOldObjectId.keySet())
					oldObjectIdParOldObjectId.put(key, _oldObjectIdParOldObjectId.get(key));
			}
			catch(Exception e) {
				logger.error("ERREUR D'IMPORT.", e);
			}
		}
		dictionaryObjectId.completion();
	}
}
