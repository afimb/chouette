package mobi.chouette.exchange.importer.updater.netex;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.Zone_VersionStructure;

public class StopPlaceMapper {

	/**
	 * Map stop area with contained stop areas. Does not support mapping parent stop areas.
	 * @param stopArea Typically stop areas of {@link ChouetteAreaEnum#StopPlace} or {@link ChouetteAreaEnum#CommercialStopPoint}
	 * @return NeTEx stop place
	 */
	public StopPlace mapStopAreaToStopPlace(StopArea stopArea) {
		StopPlace stopPlace = createStopPlace(stopArea);
		if (stopArea.getContainedStopAreas().size() > 0) {
			stopPlace.setQuays(new Quays_RelStructure());
			for (StopArea children : stopArea.getContainedStopAreas()) {
				Quay quay = createQuay(children);
				stopPlace.getQuays().getQuayRefOrQuay().add(quay);
			}
		}

		return stopPlace;
	}

	public StopArea mapStopPlaceToStopArea(Referential referential, StopPlace stopPlace) {
		StopArea stopArea = createStopArea(referential, stopPlace);

		Quays_RelStructure quays = stopPlace.getQuays();
		if (quays != null) {
			for (Object q : quays.getQuayRefOrQuay()) {
				StopArea boardingPosition = createBoardingPosition(referential, (Quay) q);
				boardingPosition.setParent(stopArea);
			}
		}

		return stopArea;
	}

	private StopArea createStopArea(Referential referential, StopPlace stopPlace) {
		StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
		stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		mapCentroid(stopPlace, stopArea);
		mapName(stopPlace, stopArea);

		return stopArea;

	}

	private StopArea createBoardingPosition(Referential referential, Quay quay) {

		StopArea boardingPosition = ObjectFactory.getStopArea(referential, quay.getId());
		boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
		mapCentroid(quay, boardingPosition);
		mapName(quay, boardingPosition);
		return boardingPosition;
	}

	private StopPlace createStopPlace(StopArea stopArea) {
		StopPlace stopPlace = new StopPlace();
		mapId(stopArea, stopPlace);
		mapCentroid(stopArea, stopPlace);
		mapName(stopArea, stopPlace);
		return stopPlace;
	}

	private Quay createQuay(StopArea stopArea) {
		Quay quay = new Quay();
		mapId(stopArea, quay);
		mapCentroid(stopArea, quay);
		mapName(stopArea, quay);
		return quay;
	}

	private void mapId(StopArea stopArea, Zone_VersionStructure zone) {
		zone.setId(stopArea.getObjectId());
	}

	public void mapCentroid(StopArea stopArea, Zone_VersionStructure zone) {
		zone.setCentroid(new SimplePoint_VersionStructure().withLocation(
				new LocationStructure().withLatitude(stopArea.getLatitude()).withLongitude(stopArea.getLongitude())));
	}

	public void mapCentroid(Zone_VersionStructure zone, StopArea stopArea) {
		LocationStructure location = zone.getCentroid().getLocation();
		stopArea.setLatitude(location.getLatitude());
		stopArea.setLongitude(location.getLongitude());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);
	}

	public void mapName(StopArea stopArea, Zone_VersionStructure zone) {

		zone.setName(new MultilingualString().withValue(stopArea.getName()).withLang("").withTextIdType(""));

	}

	public void mapName(Zone_VersionStructure zone, StopArea stopArea) {
		stopArea.setName(zone.getName().getValue());
	}

}
