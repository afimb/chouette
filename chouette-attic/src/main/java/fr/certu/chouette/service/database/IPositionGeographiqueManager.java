package fr.certu.chouette.service.database;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import chouette.schema.types.ChouetteAreaType;
import fr.certu.chouette.critere.IClause;
import fr.certu.chouette.modele.ArretItineraire;
import fr.certu.chouette.modele.Horaire;
import fr.certu.chouette.modele.Itineraire;
import fr.certu.chouette.modele.PositionGeographique;

public interface IPositionGeographiqueManager {

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#creer(fr.certu.chouette.modele.Zone)
	 */
	void creer(PositionGeographique positionGeo);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#modifier(fr.certu.chouette.modele.Zone)
	 */
	void modifier(PositionGeographique positionGeo);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#lire(java.lang.Long)
	 */
	PositionGeographique lire(Long idPositionGeo);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#lireParObjectId(java.lang.String)
	 */
	PositionGeographique lireParObjectId(String objectId);

	Map<Long, Boolean> getPresenceItineraireParPhysiqueId();

	Map<Long, PositionGeographique> getArretPhysiqueParIdArret(Collection<ArretItineraire> arrets);

	List<PositionGeographique> lireArretsPhysiques();
	List<PositionGeographique> lireZones();
	List<PositionGeographique> lire( Collection<ChouetteAreaType> areas);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#supprimer(java.lang.Long)
	 */
	void supprimer(Long idPositionGeo);

	List<Horaire> getHorairesArret(Long idPhysique);

	List<PositionGeographique> getArretsPhysiques(
			final Collection<Long> idPhysiques);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#getGeoPositionsParentes(java.lang.Long)
	 */
	List<PositionGeographique> getGeoPositionsParentes(
			final Long idGeoPositionParente);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#getGeoPositionsDirectementContenues(java.lang.Long)
	 */
	List<PositionGeographique> getGeoPositionsDirectementContenues(
			Long idParent);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#associerGeoPositions(java.lang.Long, java.lang.Long)
	 */
	void associerGeoPositions(Long idContenant, Long idContenue);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPosition(java.lang.Long)
	 */
	void dissocierGeoPosition(Long idGeoPosition);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPositionParente(java.lang.Long)
	 */
	void dissocierGeoPositionParente(Long idContenue);

	/* (non-Javadoc)
	 * @see fr.certu.chouette.service.database.IZoneManager#dissocierGeoPositionsContenues(java.lang.Long)
	 */
	void dissocierGeoPositionsContenues(Long idContenant);

	int getProfondeurMaximum();
	List<Itineraire> getItinerairesArretPhysique(Long idPhysique);
	List<PositionGeographique> select(final IClause clause);
	
	void fusionnerPositionsGeographiques(Long idArretSource, Long idArretDestination);
	
	List<PositionGeographique> lire(String nomArret, String code, Long idReseau, List <ChouetteAreaType> areaTypes);

    public void setBounds(BigDecimal maxLat, BigDecimal maxLong, BigDecimal minLat, BigDecimal minLong);
}