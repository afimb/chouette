package mobi.chouette.exchange.importer.updater.netex;

import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

public class StopPlaceMapper {

	private static final String VERSION = "1";

	public StopArea createCommercialStopPoint(Referential referential, StopArea stopArea) {
		StopArea parent = ObjectFactory.getStopArea(referential, stopArea.getObjectId()+"-PARENT");
		parent.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		parent.setLatitude(stopArea.getLatitude());
		parent.setLongitude(stopArea.getLongitude());
		parent.setLongLatType(stopArea.getLongLatType());
		parent.setName(stopArea.getName());
		
		stopArea.setParent(parent);
		
		return parent;
		
		
	}
	
	/**
	 * Map stop area with contained stop areas.
	 * 
	 * @param stopArea
	 *            Typically stop areas of {@link ChouetteAreaEnum#StopPlace} or
	 *            {@link ChouetteAreaEnum#CommercialStopPoint}
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
				StopArea boardingPosition = createBoardingPosition(referential, stopPlace,(Quay) q);
				boardingPosition.setParent(stopArea);
			}
		}

		return stopArea;
	}

	public Quay createQuay(StopArea stopArea) {
		Quay quay = new Quay();
		mapId(stopArea, quay);
		setVersion(quay);
		mapCentroid(stopArea, quay);
		mapQuayName(stopArea, quay);
		mapCompassBearing(stopArea,quay);
        if(StringUtils.isNotBlank(stopArea.getComment())) {
            quay.setDescription(new MultilingualString().withValue(stopArea.getComment()));
        }
		return quay;
	}

	private StopArea createStopArea(Referential referential, StopPlace stopPlace) {
		StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
		stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		// Set default values TODO set what we get from NSR
		stopArea.setMobilityRestrictedSuitable(null);
		stopArea.setLiftAvailable(null);
		stopArea.setStairsAvailable(null);


		mapCentroid(stopPlace, stopArea);
		mapName(stopPlace, stopArea);

		return stopArea;

	}

	private StopArea createBoardingPosition(Referential referential, StopPlace stopPlace, Quay quay) {

		StopArea boardingPosition = ObjectFactory.getStopArea(referential, quay.getId());
		// Set default values TODO set what we get from NSR
		boardingPosition.setMobilityRestrictedSuitable(null);
		boardingPosition.setLiftAvailable(null);
		boardingPosition.setStairsAvailable(null);

		boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
		mapCentroid(quay, boardingPosition);
		mapQuayName(stopPlace, quay, boardingPosition);
		mapCompassBearing(quay,boardingPosition);
		return boardingPosition;
	}

	private void mapCompassBearing(Quay quay, StopArea boardingPosition) {
		if(quay.getCompassBearing() != null) {
			boardingPosition.setCompassBearing(quay.getCompassBearing().intValue());
		}
	}


	private StopPlace createStopPlace(StopArea stopArea) {
		StopPlace stopPlace = new StopPlace();
		mapId(stopArea, stopPlace);
		setVersion(stopPlace);
		mapCentroid(stopArea, stopPlace);
		mapName(stopArea, stopPlace);
		return stopPlace;
	}

	private void mapCompassBearing(StopArea stopArea, Quay quay) {
		if(stopArea.getCompassBearing() != null) {
			quay.setCompassBearing(new Float(stopArea.getCompassBearing()));
		}
	}

	public void setVersion(EntityInVersionStructure entity) {
		entity.setVersion(VERSION);
	}

	private void mapId(StopArea stopArea, Zone_VersionStructure zone) {
		zone.setId(stopArea.getObjectId());
	}

	public void mapCentroid(StopArea stopArea, Zone_VersionStructure zone) {
		setVersion(zone);
		if(stopArea.getLatitude() != null && stopArea.getLongitude() != null) {
			zone.setCentroid(new SimplePoint_VersionStructure().withLocation(
					new LocationStructure().withLatitude(stopArea.getLatitude()).withLongitude(stopArea.getLongitude())));
		}
	}

	public void mapCentroid(Zone_VersionStructure zone, StopArea stopArea) {
		if(zone.getCentroid() != null && zone.getCentroid().getLocation() != null) {
			LocationStructure location = zone.getCentroid().getLocation();
			stopArea.setLatitude(location.getLatitude());
			stopArea.setLongitude(location.getLongitude());
			stopArea.setLongLatType(LongLatTypeEnum.WGS84);
		}
	}

	public void mapName(StopArea stopArea, Zone_VersionStructure zone) {
		zone.setName(new MultilingualString().withValue(stopArea.getName()).withLang("no").withTextIdType(""));

	}

	public void mapQuayName(StopArea stopArea, Zone_VersionStructure zone) {

		String quayName = stopArea.getRegistrationNumber();
		if(quayName == null) {
			quayName = stopArea.getName();
		}
		
		zone.setName(new MultilingualString().withValue(quayName).withLang("no").withTextIdType(""));

	}

	public void mapName(Zone_VersionStructure zone, StopArea stopArea) {
		if(zone.getName() != null) {
			stopArea.setName(zone.getName().getValue());
		}
	}

	public void mapQuayName(StopPlace stopPlace, Quay quay, StopArea stopArea) {
		if(quay.getName() == null) {
			stopArea.setName(stopPlace.getName().getValue());
		} else if (quay.getName() != null) {
			if(multiLingualStringEquals(stopPlace.getName(), quay.getName())) {
				// Same as parent
				stopArea.setName(quay.getName().getValue());
			} else {
				// Different than parent
				stopArea.setName(stopPlace.getName().getValue()+ " / " + quay.getName().getValue());
				stopArea.setRegistrationNumber(quay.getName().getValue());
			}
		}
	}
	
	private boolean multiLingualStringEquals(MultilingualString a, MultilingualString b) {
		return a.getValue().equals(b.getValue());
	}

	public void mapTransportMode(StopPlace sp, TransportModeNameEnum mode) {
		switch (mode) {
		case Air:
			sp.setStopPlaceType(StopTypeEnumeration.AIRPORT);
			break;
		case Train:
		case LongDistanceTrain_2:
		case LongDistanceTrain:
		case LocalTrain:
		case RapidTransit:
			sp.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
			break;
		case Metro:
			sp.setStopPlaceType(StopTypeEnumeration.METRO_STATION);
			break;
		case Tramway:
			sp.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
			break;
		case Shuttle:
		case Coach:
		case Bus:
		case Trolleybus:
			sp.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
			break;
		case Ferry:
			sp.setStopPlaceType(StopTypeEnumeration.HARBOUR_PORT);
			break;
		case Waterborne:
			sp.setStopPlaceType(StopTypeEnumeration.FERRY_STOP);
			break;
		default:

		}
	}

}
