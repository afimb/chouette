package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.*;

import static mobi.chouette.exchange.netexprofile.exporter.ModelTranslator.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.*;

public class StopPlaceProducer extends NetexProducer implements NetexEntityProducer<StopPlace, StopArea> {

    private static final String DEFAULT_COORDINATE_SYSTEM = "WGS84";

    @Override
    public StopPlace produce(StopArea stopArea) {
        StopPlace stopPlace = netexFactory.createStopPlace();
        stopPlace.setVersion(stopArea.getObjectVersion() > 0 ? String.valueOf(stopArea.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String stopPlaceId = netexId(stopArea.objectIdPrefix(), STOP_PLACE_KEY, stopArea.objectIdSuffix());
        stopPlace.setId(stopPlaceId);

        if (isSet(stopArea.getName())) {
            stopPlace.setName(getMultilingualString(stopArea.getName()));
        }

        if (isSet(stopArea.getComment())) {
            stopPlace.setDescription(getMultilingualString(stopArea.getComment()));
        }

        if (isSet(stopArea.getRegistrationNumber())) {
            PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
            privateCodeStruct.setValue(stopArea.getRegistrationNumber());
            stopPlace.setPrivateCode(privateCodeStruct);
        }

        if (stopArea.hasCoordinates()) {
            SimplePoint_VersionStructure pointStruct = netexFactory.createSimplePoint_VersionStructure();
            LocationStructure locationStruct = netexFactory.createLocationStructure().withSrsName(DEFAULT_COORDINATE_SYSTEM);

            if (stopArea.hasCoordinates()) {
                locationStruct.setLatitude(stopArea.getLatitude());
                locationStruct.setLongitude(stopArea.getLongitude());
            }

            pointStruct.setLocation(locationStruct);
            stopPlace.setCentroid(pointStruct);
        }

        if (isSet(stopArea.getParent())) {
            ZoneRefStructure zoneRefStruct = netexFactory.createZoneRefStructure();
            zoneRefStruct.setVersion(String.valueOf(stopArea.getParent().getObjectVersion()));
            String parentStopPlaceId = netexId(stopArea.getParent().objectIdPrefix(), STOP_PLACE_KEY, stopArea.getParent().objectIdSuffix());
            zoneRefStruct.setRef(parentStopPlaceId);
            stopPlace.setParentZoneRef(zoneRefStruct);
        }

        if (isSet(stopArea.getNearestTopicName())) {
            // TODO set Landmark?
        }

        if (stopArea.hasAddress()) {
            // TODO set PostalAddress
        }

        if (CollectionUtils.isNotEmpty(stopArea.getAccessPoints())) {
            // TODO set StopPlaceEntrance's
        }

        if (isSet(stopArea.getFareCode())) {
            TariffZoneRefs_RelStructure tariffZoneRefsStruct = netexFactory.createTariffZoneRefs_RelStructure();
            String tariffZoneIdRef = netexId(stopArea.objectIdPrefix(), TARIFF_ZONE_REF_KEY, String.valueOf(stopArea.getFareCode()));
            TariffZoneRef tariffZoneRef = netexFactory.createTariffZoneRef().withRef(tariffZoneIdRef);
            tariffZoneRefsStruct.withTariffZoneRef(tariffZoneRef);
            stopPlace.setTariffZones(tariffZoneRefsStruct);
        }

        if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint) && CollectionUtils.isNotEmpty(stopArea.getContainedStopAreas())) {
            Quays_RelStructure quayStruct = netexFactory.createQuays_RelStructure();

            for (StopArea containedStopArea : stopArea.getContainedStopAreas()) {
                Quay quay = netexFactory.createQuay();
                quay.setVersion(containedStopArea.getObjectVersion() > 0 ? String.valueOf(containedStopArea.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

                String quayId = netexId(containedStopArea.objectIdPrefix(), QUAY_KEY, containedStopArea.objectIdSuffix());
                quay.setId(quayId);

                if (isSet(quay.getName())) {
                    quay.setName(getMultilingualString(containedStopArea.getName()));
                }

                if (isSet(containedStopArea.getComment())) {
                    quay.setDescription(getMultilingualString(containedStopArea.getComment()));
                }

                if (isSet(containedStopArea.getRegistrationNumber())) {
                    PrivateCodeStructure privateCodeStruct = netexFactory.createPrivateCodeStructure();
                    privateCodeStruct.setValue(containedStopArea.getRegistrationNumber());
                    quay.setPrivateCode(privateCodeStruct);
                }

                if (containedStopArea.hasCoordinates()) {
                    SimplePoint_VersionStructure pointStruct = netexFactory.createSimplePoint_VersionStructure();
                    LocationStructure locationStruct = netexFactory.createLocationStructure().withSrsName(DEFAULT_COORDINATE_SYSTEM);

                    if (containedStopArea.hasCoordinates()) {
                        locationStruct.setLatitude(containedStopArea.getLatitude());
                        locationStruct.setLongitude(containedStopArea.getLongitude());
                    }

                    pointStruct.setLocation(locationStruct);
                    quay.setCentroid(pointStruct);
                }

                if (isSet(containedStopArea.getNearestTopicName())) {
                    // TODO set Landmark?
                }

                if (containedStopArea.hasAddress()) {
                    // TODO set PostalAddress
                }

                if (isSet(containedStopArea.getFareCode())) {
                    TariffZoneRefs_RelStructure tariffZoneRefsStruct = netexFactory.createTariffZoneRefs_RelStructure();
                    String tariffZoneIdRef = netexId(containedStopArea.objectIdPrefix(), TARIFF_ZONE_REF_KEY, String.valueOf(containedStopArea.getFareCode()));
                    TariffZoneRef tariffZoneRef = netexFactory.createTariffZoneRef().withRef(tariffZoneIdRef);
                    tariffZoneRefsStruct.withTariffZoneRef(tariffZoneRef);
                    quay.setTariffZones(tariffZoneRefsStruct);
                }

                quayStruct.getQuayRefOrQuay().add(quay);
            }

            stopPlace.setQuays(quayStruct);
        }

        return stopPlace;
    }

}
