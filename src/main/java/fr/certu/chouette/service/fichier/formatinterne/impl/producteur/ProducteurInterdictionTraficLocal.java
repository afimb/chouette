package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;

public class ProducteurInterdictionTraficLocal implements IProducteurSpecifique {
	private static final Logger logger = Logger
	.getLogger(ProducteurInterdictionTraficLocal.class);
	
	private IFournisseurId			fournisseurId;
	private IGestionFichier			gestionFichier;
	private IIdentificationManager	identificationManager;
	
	public ProducteurInterdictionTraficLocal(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId = fournisseurId;
		this.gestionFichier = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(boolean majIdentification, ILectureEchange echange, IEtatDifference etatDifference, Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire( final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		Map<String, Long> resultat = new Hashtable<String, Long>();
		
		List<InterdictionTraficLocal> itls = echange.getInterdictionTraficLocal();
		List<ITLStopArea> itlStopAreas = new ArrayList<ITLStopArea>();
		for (InterdictionTraficLocal itl : itls) 
		{
			itl.setId( new Long(fournisseurId.getNouvelId( itl.getObjectId())));
			resultat.put( itl.getObjectId(), itl.getId());
			
			// trouver les objetcId des arrets phy d'après le areaId de l'ITL
			List<String> physiqueObjectIds = echange.getPhysiqueObjectIds( itl.getObjectId());
			for (String physiqueObjectId : physiqueObjectIds) 
			{
				Long idPhysique = idParObjectId.get( physiqueObjectId);
				
				// si l'arret est déjà en base
				if ( idPhysique==null)
					idPhysique = etatDifference.getIdZoneGeneriqueConnue( physiqueObjectId);
				
				itlStopAreas.add( new ITLStopArea( itl.getId(), idPhysique));
			}
		}
		List<String[]> contenuLiens = traduire(itlStopAreas);
		gestionFichier.produire(contenuLiens, gestionFichier.getCheminFichierItlStoparea());
		
		List<String[]> contenu = traduire( echange, itls);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierItl());
		return resultat;
	}
	
	private List<String[]> traduire( final List<ITLStopArea> itlStopAreas)
	{
		List<String[]> contenu = new ArrayList<String[]>();
		int position = 0;
		Long itlPrecedente = null;
		for (ITLStopArea itlStopArea : itlStopAreas) 
		{
			// ré-initialiser le compteur d'une ITL à l'autre
			if ( itlPrecedente==null || !itlPrecedente.equals( itlStopArea.itlId))
				position = 0;
			itlPrecedente = itlStopArea.itlId;
			
			List<String> champs = new ArrayList<String>();
			champs.add( itlStopArea.itlId.toString());
			champs.add( itlStopArea.physiqueId.toString());
			champs.add( String.valueOf( position));
			position++;
			
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
	
	private List<String[]> traduire( final ILectureEchange echange, final List<InterdictionTraficLocal> itls) 
	{
		List<String[]> contenu = new ArrayList<String[]>();
		for (InterdictionTraficLocal itl : itls) 
		{
			List<String> champs = new ArrayList<String>();
			champs.add( itl.getId().toString());
			champs.add( itl.getObjectId());
			champs.add( echange.getLigne().getId().toString());
			champs.add( itl.getNom());
			
			contenu.add((String[])champs.toArray(new String[]{}));
		}
		return contenu;
	}
	
	private class ITLStopArea
	{
		public Long itlId;
		public Long physiqueId;
		public ITLStopArea(Long itlId, Long physiqueId) {
			this.itlId = itlId;
			this.physiqueId = physiqueId;
		}
	}
}
