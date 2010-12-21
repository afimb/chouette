package unit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import chouette.schema.ChouettePTNetworkTypeType;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.manager.SingletonManager;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import fr.certu.chouette.service.exportateur.impl.MassiveExportManager;
import fr.certu.chouette.service.fichier.IImportateur;
import fr.certu.chouette.service.importateur.multilignes.ILecteurPrincipal;
import fr.certu.chouette.service.xml.ILecteurFichierXML;

public class MassiveExportTest {
	private static final Logger logger = Logger
			.getLogger(MassiveExportTest.class);
	private MassiveExportManager massiveExportManager;
	private ILigneManager ligneManager;
	private ILecteurPrincipal lecteurCSV;
	private IPositionGeographiqueManager positionGeographiqueManager;
	private IImportateur importateur;
	private ILecteurFichierXML lecteurFichierXML;

	@BeforeSuite
	public void initialisation() throws Exception {
		ApplicationContext applicationContext = SingletonManager
				.getApplicationContext();
		massiveExportManager = (MassiveExportManager) applicationContext
				.getBean("massiveExportManager");
		ligneManager = (ILigneManager) applicationContext
				.getBean("ligneManager");
		lecteurCSV = (ILecteurPrincipal) applicationContext
				.getBean("lecteurCSVPrincipal");
		importateur = (IImportateur) applicationContext.getBean("importateur");
		positionGeographiqueManager = (IPositionGeographiqueManager) applicationContext
				.getBean("positionGeographiqueManager");
		lecteurFichierXML = (ILecteurFichierXML)applicationContext.getBean("lecteurFichierXML");
	}

	@Test(groups = "tests unitaires", description = "export massif - export multilignes")
	public void exportLines() {
		for (Ligne l : ligneManager.lire()) {
			ligneManager.supprimer(l.getId());
		}
		for (PositionGeographique pg : positionGeographiqueManager
				.lireArretsPhysiques()) {
			positionGeographiqueManager.supprimer(pg.getId());
		}
		for (PositionGeographique pg : positionGeographiqueManager.lireZones()) {
			positionGeographiqueManager.supprimer(pg.getId());
		}

		// import of some test lines
		importTestLines();

		File zipFile = new File("massiveExportUnitTest.zip");
		List<Long> lineIds = new ArrayList<Long>();

		for (Ligne l : ligneManager.lire()) {
			lineIds.add(l.getId());
			logger.error("line id : " + l.getId());
		}

		try {
			massiveExportManager.exportLines(zipFile, lineIds, null, null,
					false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		List<String> lineNames = new ArrayList<String>();
		try {
			FileInputStream fin = new FileInputStream(zipFile);
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			while ((ze = zin.getNextEntry()) != null) {
				File outFile = new File(ze.getName());
				FileOutputStream fout = new FileOutputStream(outFile);
				for (int c = zin.read(); c != -1; c = zin.read()) {
					fout.write(c);
				}
				zin.closeEntry();
				fout.close();
				ChouettePTNetworkTypeType line = lecteurFichierXML.lire(ze.getName());
				lineNames.add(line.getChouetteLineDescription().getLine().getName());
				outFile.delete();
			}
			zin.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		assert lineNames.contains("Ligne 01");
		assert lineNames.contains("Ligne 02");
		assert lineNames.size() == 2;
	}

	private void importTestLines() {
		String nom = "target/test-classes/multi_lignes_excel.csv";
		try {
			lecteurCSV.lire(nom);
			List<ILectureEchange> lecturesEchange = lecteurCSV
					.getLecturesEchange();
			for (ILectureEchange lectureEchange : lecturesEchange) {
				importateur.importer(true, lectureEchange);
			}
		} catch (ServiceException e) {
			throw new RuntimeException(e);
		}
	}

}
