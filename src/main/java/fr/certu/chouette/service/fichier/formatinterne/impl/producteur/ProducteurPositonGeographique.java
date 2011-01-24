package fr.certu.chouette.service.fichier.formatinterne.impl.producteur;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeDetailIncident;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.fichier.formatinterne.IFournisseurId;
import fr.certu.chouette.service.fichier.formatinterne.IGestionFichier;
import fr.certu.chouette.service.fichier.formatinterne.impl.IProducteurSpecifique;
import fr.certu.chouette.service.fichier.formatinterne.modele.IEtatDifference;
import fr.certu.chouette.service.identification.IIdentificationManager;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
//import org.apache.log4j.Logger;

public class ProducteurPositonGeographique implements IProducteurSpecifique {

        //private static Logger          logger = Logger.getLogger(ProducteurPositonGeographique.class);
	private IFournisseurId         fournisseurId;
	private IGestionFichier        gestionFichier;
	private IIdentificationManager identificationManager;
	
	public ProducteurPositonGeographique(final IIdentificationManager identificationManager, final IFournisseurId fournisseurId, final IGestionFichier gestionFichier) {
		super();
		this.fournisseurId         = fournisseurId;
		this.gestionFichier        = gestionFichier;
		this.identificationManager = identificationManager;
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId) {
		return produire(majIdentification, echange, etatDifference, idParObjectId, false);
	}
	
	public Map<String, Long> produire(final boolean majIdentification, final ILectureEchange echange, final IEtatDifference etatDifference, final Map<String, Long> idParObjectId, boolean incremental) {
		final Map<String, Long> resultat = new Hashtable<String, Long>();
		final List<PositionGeographique> zonesGeneriquesNouvelles = new ArrayList<PositionGeographique>();
		final List<PositionGeographique> zonesGeneriques = echange.getPositionsGeographiques();
		for (PositionGeographique geoPosition : zonesGeneriques) {
                        //logger.debug("La position "+geoPosition);
			Long idGeoPosition = null;
			final String zoneGeneriqueObjectId = geoPosition.getObjectId();
			if (etatDifference.isObjectIdZoneGeneriqueConnue(zoneGeneriqueObjectId))
				idGeoPosition = etatDifference.getIdZoneGeneriqueConnue(zoneGeneriqueObjectId);
			else {
				idGeoPosition = resultat.get(zoneGeneriqueObjectId);
				if (idGeoPosition==null) {
					idGeoPosition = new Long(fournisseurId.getNouvelId(zoneGeneriqueObjectId));
				geoPosition.setId(idGeoPosition);
				zonesGeneriquesNouvelles.add(geoPosition);
				}
			}
			resultat.put(zoneGeneriqueObjectId, idGeoPosition);
		}
		for (PositionGeographique geoPosition : zonesGeneriques) {
			final String parenteObjectId = echange.getZoneParente(geoPosition.getObjectId());
			if (parenteObjectId != null) {
				geoPosition.setIdParent(resultat.get(parenteObjectId));
				if (geoPosition.getIdParent() == null)
					throw new ServiceException(CodeIncident.IDENTIFIANT_INCONNU, CodeDetailIncident.STOPAREA_PARENT,parenteObjectId,geoPosition.getObjectId(),geoPosition.getName());
			}
		}
		final List<String[]> contenu = traduire(majIdentification, zonesGeneriquesNouvelles);
		gestionFichier.produire(contenu, gestionFichier.getCheminFichierZoneGenerique());
		return resultat;
	}
	
	private List<String[]> traduire(final boolean majIdentification, final List<PositionGeographique> geoPositions) {
		List<String[]> contenu = new ArrayList<String[]>();
		for (PositionGeographique geoPosition : geoPositions) {
			List<String> champs = new ArrayList<String>();
			String objectId = majIdentification ? identificationManager.getIdFonctionnel("StopArea", String.valueOf(geoPosition.getId())) : geoPosition.getObjectId();
			if (majIdentification && (geoPosition.getAreaType() != null))
				if  (geoPosition.getAreaType().equals(ChouetteAreaType.COMMERCIALSTOPPOINT))
					identificationManager.getDictionaryObjectId().addObjectIdParOldObjectId(geoPosition.getObjectId(), objectId);
			champs.add(geoPosition.getId().toString());
			champs.add(gestionFichier.getChamp(geoPosition.getIdParent()));
			champs.add(gestionFichier.getChamp(objectId));
			champs.add(gestionFichier.getChamp(geoPosition.getObjectVersion()));
			champs.add(gestionFichier.getChamp(geoPosition.getCreationTime()));
			champs.add(gestionFichier.getChamp(geoPosition.getCreatorId()));
			champs.add(gestionFichier.getChamp(geoPosition.getName()));
			champs.add(gestionFichier.getChamp(geoPosition.getComment()));
			champs.add(gestionFichier.getChamp(geoPosition.getAreaType()));
			champs.add(gestionFichier.getChamp(geoPosition.getRegistrationNumber()));
			champs.add(gestionFichier.getChamp(geoPosition.getNearestTopicName()));
			champs.add(gestionFichier.getChamp(geoPosition.getFareCode()));
			champs.add(gestionFichier.getChamp(geoPosition.getLongitude()));
			champs.add(gestionFichier.getChamp(geoPosition.getLatitude()));
			champs.add(gestionFichier.getChamp(geoPosition.getLongLatType()));
			champs.add(gestionFichier.getChamp(geoPosition.getX()));
			champs.add(gestionFichier.getChamp(geoPosition.getY()));
			champs.add(gestionFichier.getChamp(geoPosition.getProjectionType()));
			champs.add(gestionFichier.getChamp(geoPosition.getCountryCode()));
			champs.add(gestionFichier.getChamp(geoPosition.getStreetName()));
			contenu.add((String[])champs.toArray(new String[]{}));
			if (majIdentification)
				if (geoPosition.getName() != null)
					if (geoPosition.getName().trim().length() > 0)
						if (geoPosition.getAreaType() != null)
							if (geoPosition.getAreaType().equals(ChouetteAreaType.COMMERCIALSTOPPOINT))
								identificationManager.getDictionaryObjectId().addObjectIdParReference(geoPosition.getName(), objectId);
		}
		return contenu;
	}
}
