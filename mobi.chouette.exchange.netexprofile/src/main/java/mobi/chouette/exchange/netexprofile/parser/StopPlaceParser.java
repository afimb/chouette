package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Log4j
public class StopPlaceParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "StopPlaceContext";
    public static final String STOP_AREA_ID = "stopAreaId";

    private static final String BOARDING_POSITION_ID_SUFFIX = "01";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        StopPlacesInFrame_RelStructure stopPlacesStruct = (StopPlacesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();

        for (StopPlace stopPlace : stopPlaces) {
            NetexObjectUtil.addStopPlaceReference(referential, stopPlace.getId(), stopPlace);
            //validator.addObjectReference(context, route); // TODO implement a stop place validator
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        Collection<StopPlace> stopPlaces = netexReferential.getStopPlaces().values();

        for (StopPlace stopPlace : stopPlaces) {
            String netexStopPlaceId = stopPlace.getId();
            // TODO generate chouette id with generator here, do not use netex id directly
            String chouetteStopAreaId = stopPlace.getId();
            StopArea stopArea = ObjectFactory.getStopArea(chouetteReferential, chouetteStopAreaId);
            addStopAreaIdRef(context, netexStopPlaceId, chouetteStopAreaId);

            String stopPlaceName = stopPlace.getName().getValue();
            stopArea.setName(stopPlaceName);

            String stopPlaceShortName = stopPlace.getShortName().getValue();
            stopArea.setRegistrationNumber(stopPlaceShortName);

            stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

            // TODO add support for adjustments of wrong coordinates, see RegtoppStopParser
            SimplePoint_VersionStructure centroidStruct = stopPlace.getCentroid();
            LocationStructure locationStruct = centroidStruct.getLocation();
            stopArea.setLongLatType(NetexUtils.toLongLatTypeEnum(locationStruct.getSrsName()));

            stopArea.setLatitude(locationStruct.getLatitude());
            stopArea.setLongitude(locationStruct.getLongitude());

            // TODO add support for area centroid, see regtopp CentroidGenerator for an example
/*
            stopArea.setX();
            stopArea.setY(y);
            stopArea.setProjectionType("epsg:2154"); // TODO make this a configuration parameter, static for now
*/

/*
            // TODO add support for quays/gates which will map to boarding positions

            String boardingPositionObjectId = stopPlace.getId() + "-" + BOARDING_POSITION_ID_SUFFIX; // TODO use id generator/creator
            StopArea boardingPosition = ObjectFactory.getStopArea(chouetteReferential, boardingPositionObjectId);

            boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
            boardingPosition.setY(stopArea.getY());
            boardingPosition.setX(stopArea.getX());
            boardingPosition.setProjectionType(stopArea.getProjectionType());
            boardingPosition.setLatitude(stopArea.getLatitude());
            boardingPosition.setLongitude(stopArea.getLongitude());
            boardingPosition.setLongLatType(stopArea.getLongLatType());
            boardingPosition.setName(stopArea.getName());
            boardingPosition.setParent(stopArea);
            boardingPosition.setFilled(true);
*/

            stopArea.setFilled(true);
        }
    }

    private void addStopAreaIdRef(Context context, String objectId, String stopAreaId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(STOP_AREA_ID, stopAreaId);
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
