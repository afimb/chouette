package fr.certu.chouette.service.fichier;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.fichier.formatinterne.IAnalyseurEtatInitial;
import fr.certu.chouette.service.fichier.formatinterne.IChargeur;
import fr.certu.chouette.service.fichier.formatinterne.IGestionModification;
import fr.certu.chouette.service.fichier.formatinterne.IProducteurFichier;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.log4j.Logger;
import fr.certu.chouette.service.database.ChouetteDriverManagerDataSource;

public class Importateur implements IImportateur {
	
	private static final Logger                  logger              = Logger.getLogger(Importateur.class);
	private              ChouetteDriverManagerDataSource managerDataSource;
	private              IProducteurFichier      producteurFichier;
	private              IAnalyseurEtatInitial   analyseurEtatInitial;
	private              IChargeur               chargeur;
	private              IGestionModification    gestionModification;
	
	public void importer(final boolean majIdentification, final ILectureEchange lectureEchange) {
		importer(majIdentification, lectureEchange, false);
	}
		
	public void importer(final boolean majIdentification, final ILectureEchange lectureEchange, final boolean incremental) {
		Connection connexion = null;
		try {
			Properties props = new Properties();
			props.setProperty("user",managerDataSource.getUsername());
			props.setProperty("password",managerDataSource.getPassword());
			props.setProperty("allowEncodingChanges","true");
			connexion = DriverManager.getConnection(managerDataSource.getUrl(), props);
			connexion.setAutoCommit(false);
			synchronized (producteurFichier) {
				IEtatDifference etatDifference = analyseurEtatInitial.analyser(lectureEchange, connexion, incremental);
				producteurFichier.produire(majIdentification, lectureEchange, etatDifference, connexion, incremental);
				logger.debug("etatDifference.isLigneConnue() ? "+etatDifference.isLigneConnue());
				chargeur.charger(etatDifference, connexion, incremental);
				gestionModification.setConnexion(connexion);
				gestionModification.setEtatDifference(etatDifference);
				gestionModification.setIdParObjectId(producteurFichier.getIdParObjectId());
				gestionModification.modifier(lectureEchange, incremental);
			}
			connexion.commit();
                        connexion.close();
		}
		catch(Exception e) {
			try {
				logger.debug("annuler :"+e.getMessage(), e);
				if (connexion != null)
					connexion.rollback();
			}
			catch(Exception ex) {
				logger.error("Echec de la tentative de rollback de la transaction "+ex.getMessage(), ex);
			}
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (connexion != null)
					connexion.close();
			}
			catch(Exception e) {
				logger.error("Echec de la tentative de fermeture de la connexion "+e.getMessage(), e);
			}
		}
	}
	
	public void setAnalyseurEtatInitial(IAnalyseurEtatInitial analyseurEtatInitial) {
		this.analyseurEtatInitial = analyseurEtatInitial;
	}
	
	public void setChargeur(IChargeur chargeur) {
		this.chargeur = chargeur;
	}
	
	public void setProducteurFichier(IProducteurFichier producteurFichier) {
		this.producteurFichier = producteurFichier;
	}
	
	public void setManagerDataSource(ChouetteDriverManagerDataSource managerDataSource) {
		this.managerDataSource = managerDataSource;
	}
	
	public void setGestionModification(IGestionModification gestionModification) {
		this.gestionModification = gestionModification;
	}
}
