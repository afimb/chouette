package mobi.chouette.exchange.netexprofile.parser;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import net.opengis.gml._3.DirectPositionType;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.PostalAddress;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.RelationshipStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TariffZoneRef;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.netex.model.TariffZonesInFrame_RelStructure;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.rutebanken.netex.model.ZoneRefStructure;

@Log4j
public class StopPlaceParser implements Parser, Constant {

    private Map<String, Properties> tariffZoneProperties;

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        RelationshipStructure relationshipStruct = (RelationshipStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        if (relationshipStruct instanceof TariffZonesInFrame_RelStructure) {
            tariffZoneProperties = new HashMap<>();

            TariffZonesInFrame_RelStructure tariffZonesStruct = (TariffZonesInFrame_RelStructure) relationshipStruct;
            List<TariffZone> tariffZones = tariffZonesStruct.getTariffZone();

            for (TariffZone tariffZone : tariffZones) {
                Properties properties = new Properties();
                properties.put(NAME, tariffZone.getName().getValue());
                this.tariffZoneProperties.put(tariffZone.getId(), properties);
            }
        } else if (relationshipStruct instanceof StopPlacesInFrame_RelStructure) {
            StopPlacesInFrame_RelStructure stopPlacesStruct = (StopPlacesInFrame_RelStructure) relationshipStruct;
            List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();
            Map<String, String> parentZoneMap = new HashMap<>();

            for (StopPlace stopPlace : stopPlaces) {
                parseStopPlace(context, stopPlace, parentZoneMap);
            }

            for (Map.Entry<String, String> item : parentZoneMap.entrySet()) {
                StopArea child = ObjectFactory.getStopArea(referential, item.getKey());
                StopArea parent = ObjectFactory.getStopArea(referential, item.getValue());
                if (parent != null) {
                    parent.setAreaType(ChouetteAreaEnum.StopPlace);
                    child.setParent(parent);
                }
            }
        }
    }

    void parseStopPlace(Context context, StopPlace stopPlace, Map<String, String> parentZoneMap) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);

        StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
        stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
        stopArea.setObjectVersion(NetexParserUtils.getVersion(stopPlace));
        stopArea.setName(stopPlace.getName().getValue());

        if (stopPlace.getDescription() != null) {
            stopArea.setComment(stopPlace.getDescription().getValue());
        }
        if (stopPlace.getLandmark() != null) {
            stopArea.setNearestTopicName(stopPlace.getLandmark().getValue());
        }

        PrivateCodeStructure privateCodeStruct = stopPlace.getPrivateCode();
        if (privateCodeStruct != null) {
            stopArea.setRegistrationNumber(privateCodeStruct.getValue());
        } else {
            if (stopPlace.getShortName() != null) {
                stopArea.setRegistrationNumber(stopPlace.getShortName().getValue());
            }
        }

        SimplePoint_VersionStructure centroidStruct = stopPlace.getCentroid();
        if (centroidStruct != null) {
            parseCentroid(centroidStruct.getLocation(), stopArea);
        }

        ZoneRefStructure parentZoneRefStruct = stopPlace.getParentZoneRef();
        if (parentZoneRefStruct != null) {
            parentZoneMap.put(stopArea.getObjectId(), parentZoneRefStruct.getRef());
        }

        PostalAddress postalAddress = stopPlace.getPostalAddress();
        if (postalAddress != null) {
            stopArea.setCountryCode(postalAddress.getPostCode());
            stopArea.setStreetName(postalAddress.getAddressLine1().getValue());
        }

        TariffZoneRefs_RelStructure tariffZonesStruct = stopPlace.getTariffZones();
        if (tariffZonesStruct != null) {
            parseTariffZoneRefs(tariffZonesStruct, stopArea);
        }

        Quays_RelStructure quaysStruct = stopPlace.getQuays();
        if (quaysStruct != null) {
            List<Object> quayObjects = quaysStruct.getQuayRefOrQuay();
            for (Object quayObject : quayObjects) {
                parseQuay(context, stopArea, (Quay) quayObject);
            }
        }

        stopArea.setStopAreaType(mapStopAreaType(stopPlace.getStopPlaceType()));
        stopArea.setTransportModeName(mapTransportModeName(stopPlace.getTransportMode()));
        stopArea.setTransportSubMode(mapTransportSubMode(stopPlace));

        stopArea.setFilled(true);
    }

    StopAreaTypeEnum mapStopAreaType(StopTypeEnumeration netexType) {
        if (netexType == null) {
            return null;
        }

        try {
            return StopAreaTypeEnum.valueOf(StringUtils.capitalize(netexType.value()));
        } catch (IllegalArgumentException iae) {
            log.warn("Unable to map unknown StopTypeEnumeration value: " + netexType);
            return StopAreaTypeEnum.Other;
        }

    }

    TransportSubModeEnum mapTransportSubMode(StopPlace netexStop) {
        if (netexStop.getTransportMode() == null) {
            return null;
        }

        if (netexStop.getWaterSubmode() != null) {
            return mapTransportSubMode(netexStop.getWaterSubmode().value());
        } else if (netexStop.getTramSubmode() != null) {
            return mapTransportSubMode(netexStop.getTramSubmode().value());
        } else if (netexStop.getMetroSubmode() != null) {
            return mapTransportSubMode(netexStop.getMetroSubmode().value());
        } else if (netexStop.getRailSubmode() != null) {
            return mapTransportSubMode(netexStop.getRailSubmode().value());
        } else if (netexStop.getBusSubmode() != null) {
            return mapTransportSubMode(netexStop.getBusSubmode().value());
        } else if (netexStop.getAirSubmode() != null) {
            return mapTransportSubMode(netexStop.getAirSubmode().value());
        } else if (netexStop.getTaxiSubmode() != null) {
            return mapTransportSubMode(netexStop.getTaxiSubmode().value());
        } else if (netexStop.getSelfDriveSubmode() != null) {
            return mapTransportSubMode(netexStop.getSelfDriveSubmode().value());
        } else if (netexStop.getCoachSubmode() != null) {
            return mapTransportSubMode(netexStop.getCoachSubmode().value());
        } else if (netexStop.getFunicularSubmode() != null) {
            return mapTransportSubMode(netexStop.getFunicularSubmode().value());
        } else if (netexStop.getTelecabinSubmode() != null) {
            return mapTransportSubMode(netexStop.getTelecabinSubmode().value());
        }

        return null;
    }

    TransportSubModeEnum mapTransportSubMode(String netexValue) {
        try {
            return TransportSubModeEnum.valueOf(StringUtils.capitalize(netexValue));
        } catch (IllegalArgumentException iae) {
            log.warn("Unable to map unknown TransportModeNameEnum value: " + netexValue);
            return null;
        }
    }


    TransportModeNameEnum mapTransportModeName(VehicleModeEnumeration netexMode) {
        if (netexMode == null) {
            return null;
        }

        switch (netexMode) {
            case AIR:
                return TransportModeNameEnum.Air;
            case BUS:
                return TransportModeNameEnum.Bus;
            case RAIL:
                return TransportModeNameEnum.Train;
            case TAXI:
                return TransportModeNameEnum.Taxi;
            case TRAM:
                return TransportModeNameEnum.Tramway;
            case COACH:
                return TransportModeNameEnum.Coach;
            case FERRY:
                return TransportModeNameEnum.Ferry;
            case METRO:
                return TransportModeNameEnum.Metro;
            case WATER:
                return TransportModeNameEnum.Waterborne;
            case CABLEWAY:
                return TransportModeNameEnum.Cabelway;
            case FUNICULAR:
                return TransportModeNameEnum.Funicular;
            case TROLLEY_BUS:
                return TransportModeNameEnum.Trolleybus;
            case LIFT:
            case OTHER:
                return TransportModeNameEnum.Other;
        }

        return TransportModeNameEnum.Other;
    }


    private void parseQuay(Context context, StopArea parentStopArea, Quay quay) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);

        StopArea boardingPosition = ObjectFactory.getStopArea(referential, quay.getId());
        boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
        //boardingPosition.setAreaType(ChouetteAreaEnum.Quay);

        boardingPosition.setObjectVersion(NetexParserUtils.getVersion(quay));
        if (quay.getName() == null) {
            boardingPosition.setName(parentStopArea.getName());
        } else {
            boardingPosition.setName(quay.getName().getValue());
        }
        boardingPosition.setParent(parentStopArea);

        if (quay.getDescription() != null) {
            boardingPosition.setComment(quay.getDescription().getValue());
        }
        if (quay.getLandmark() != null) {
            boardingPosition.setNearestTopicName(quay.getLandmark().getValue());
        }

        PrivateCodeStructure privateCodeStruct = quay.getPrivateCode();
        if (privateCodeStruct != null) {
            boardingPosition.setRegistrationNumber(privateCodeStruct.getValue());
        } else {
            if (quay.getShortName() != null) {
                boardingPosition.setRegistrationNumber(quay.getShortName().getValue());
            }
        }

        SimplePoint_VersionStructure centroidStruct = quay.getCentroid();
        if (centroidStruct != null) {
            parseCentroid(centroidStruct.getLocation(), boardingPosition);
        }

        PostalAddress postalAddress = quay.getPostalAddress();
        if (postalAddress != null) {
            boardingPosition.setCountryCode(postalAddress.getPostCode());
            boardingPosition.setStreetName(postalAddress.getAddressLine1().getValue());
        }

        TariffZoneRefs_RelStructure tariffZonesStruct = quay.getTariffZones();
        if (tariffZonesStruct != null) {
            parseTariffZoneRefs(tariffZonesStruct, boardingPosition);
        }

        boardingPosition.setFilled(true);
    }

    private void parseCentroid(LocationStructure locationStruct, StopArea stopArea) throws Exception {
        BigDecimal latitude = locationStruct.getLatitude();
        if (latitude != null) {
            stopArea.setLatitude(latitude);
        }
        BigDecimal longitude = locationStruct.getLongitude();
        if (longitude != null) {
            stopArea.setLongitude(longitude);
        }

        DirectPositionType positionType = locationStruct.getPos();
        if (positionType != null) {
            String projectedType = locationStruct.getSrsName();
            BigDecimal x = ParserUtils.getX(String.valueOf(positionType.getValue().get(0)));
            BigDecimal y = ParserUtils.getY(String.valueOf(positionType.getValue().get(1)));

            if (projectedType != null && x != null && y != null) {
                stopArea.setProjectionType(projectedType);
                stopArea.setX(x);
                stopArea.setY(y);
            }
        }

        if (stopArea.getLongitude() != null && stopArea.getLatitude() != null) {
            stopArea.setLongLatType(LongLatTypeEnum.WGS84);
        } else {
            stopArea.setLongitude(null);
            stopArea.setLatitude(null);
        }
    }

    private void parseTariffZoneRefs(TariffZoneRefs_RelStructure tariffZonesStruct, StopArea stopArea) throws Exception {
        List<TariffZoneRef> tariffZoneRefs = tariffZonesStruct.getTariffZoneRef();

        for (TariffZoneRef tariffZoneRef : tariffZoneRefs) {
            Properties properties = tariffZoneProperties.get(tariffZoneRef.getRef());

            if (properties != null) {
                String tariffName = properties.getProperty(NAME);
                if (tariffName != null) {
                    try {
                        stopArea.setFareCode(Integer.parseInt(tariffName));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    static {
        ParserFactory.register(StopPlaceParser.class.getName(), new ParserFactory() {
            private StopPlaceParser instance = new StopPlaceParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
