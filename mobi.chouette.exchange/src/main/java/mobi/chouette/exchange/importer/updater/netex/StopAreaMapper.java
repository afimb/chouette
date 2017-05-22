package mobi.chouette.exchange.importer.updater.netex;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.*;

/**
 * Map from NeTEx to chouette model
 */
public class StopAreaMapper {

    public StopArea mapCommercialStopPoint(Referential referential, StopArea stopArea) {
        String split[] = stopArea.getObjectId().split(":");
        String parentId = split[0]+":StopPlace:"+split[2];
        
    	StopArea parent = ObjectFactory.getStopArea(referential, parentId);
        parent.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
        parent.setLatitude(stopArea.getLatitude());
        parent.setLongitude(stopArea.getLongitude());
        parent.setLongLatType(stopArea.getLongLatType());
        parent.setName(stopArea.getName());

        stopArea.setParent(parent);

        return parent;
    }

    public StopArea mapStopPlaceToStopArea(Referential referential, StopPlace stopPlace) {
        StopArea stopArea = mapStopArea(referential, stopPlace);

        Quays_RelStructure quays = stopPlace.getQuays();
        if (quays != null) {
            for (Object q : quays.getQuayRefOrQuay()) {
                StopArea boardingPosition = mapBoardingPosition(referential, stopPlace, (Quay) q);
                boardingPosition.setParent(stopArea);
            }
        }

        return stopArea;
    }

    public void mapCentroidToChouette(Zone_VersionStructure zone, StopArea stopArea) {
        if (zone.getCentroid() != null && zone.getCentroid().getLocation() != null) {
            LocationStructure location = zone.getCentroid().getLocation();
            stopArea.setLatitude(location.getLatitude());
            stopArea.setLongitude(location.getLongitude());
            stopArea.setLongLatType(LongLatTypeEnum.WGS84);
        }
    }

    public void mapQuayName(StopPlace stopPlace, Quay quay, StopArea stopArea) {
        if (quay.getName() == null) {
            stopArea.setName(stopPlace.getName().getValue());
        } else if (quay.getName() != null) {
            if (multiLingualStringEquals(stopPlace.getName(), quay.getName())) {
                // Same as parent
                stopArea.setName(quay.getName().getValue());
            } else {
                // Different than parent
                stopArea.setName(stopPlace.getName().getValue() + " / " + quay.getName().getValue());
                stopArea.setRegistrationNumber(quay.getPublicCode());
            }
        }
    }

    public void mapName(Zone_VersionStructure zone, StopArea stopArea) {
        if (zone.getName() != null) {
            stopArea.setName(zone.getName().getValue());
        }
    }

    private boolean multiLingualStringEquals(MultilingualString a, MultilingualString b) {
        return a.getValue().equals(b.getValue());
    }

    private StopArea mapBoardingPosition(Referential referential, StopPlace stopPlace, Quay quay) {

        StopArea boardingPosition = ObjectFactory.getStopArea(referential, quay.getId());
        // Set default values TODO set what we get from NSR
        boardingPosition.setMobilityRestrictedSuitable(null);
        boardingPosition.setLiftAvailable(null);
        boardingPosition.setStairsAvailable(null);
        if (quay.getDescription() != null) {
            boardingPosition.setComment(quay.getDescription().getValue());
        }

        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        mapCentroidToChouette(quay, boardingPosition);
        mapQuayName(stopPlace, quay, boardingPosition);
        createCompassBearing(quay, boardingPosition);
        return boardingPosition;
    }

    private StopArea mapStopArea(Referential referential, StopPlace stopPlace) {
        StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
        stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

        // Set default values TODO set what we get from NSR
        stopArea.setMobilityRestrictedSuitable(null);
        stopArea.setLiftAvailable(null);
        stopArea.setStairsAvailable(null);


        mapCentroidToChouette(stopPlace, stopArea);
        mapName(stopPlace, stopArea);

        return stopArea;
    }

    private void createCompassBearing(Quay quay, StopArea boardingPosition) {
        if (quay.getCompassBearing() != null) {
            boardingPosition.setCompassBearing(quay.getCompassBearing().intValue());
        }
    }
}
