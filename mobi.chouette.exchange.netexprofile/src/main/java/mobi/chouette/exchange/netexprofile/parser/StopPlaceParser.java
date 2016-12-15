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

import java.util.Collection;
import java.util.List;

@Log4j
public class StopPlaceParser extends AbstractParser {

	public static final String LOCAL_CONTEXT = "StopPlaceContext";
	public static final String STOP_AREA_ID = "stopAreaId";

	@Override
	public void initReferentials(Context context) throws Exception {
		NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		StopPlacesInFrame_RelStructure stopPlacesStruct = (StopPlacesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
		List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();

		for (StopPlace stopPlace : stopPlaces) {
			NetexObjectUtil.addStopPlaceReference(referential, stopPlace.getId(), stopPlace);
			// validator.addObjectReference(context, route); // TODO implement a stop place validator
		}
	}

	@Override
	public void parse(Context context) throws Exception {
		Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

		//Collection<StopPlace> stopPlaces = netexReferential.getStopPlaces().values();

		StopPlacesInFrame_RelStructure stopPlacesStruct = (StopPlacesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
		List<StopPlace> stopPlaces = stopPlacesStruct.getStopPlace();

		for (StopPlace stopPlace : stopPlaces) {

			NetexObjectUtil.addStopPlaceReference(netexReferential, stopPlace.getId(), stopPlace);

			String netexStopPlaceId = stopPlace.getId();
			String chouetteStopAreaId = stopPlace.getId();

			StopArea stopArea = ObjectFactory.getStopArea(chouetteReferential, chouetteStopAreaId);

			// TODO remove? probably not needed anymore
			addStopAreaIdRef(context, netexStopPlaceId, chouetteStopAreaId);

			String stopPlaceName = stopPlace.getName().getValue();
			stopArea.setName(stopPlaceName);

			String stopPlaceShortName = stopPlace.getShortName().getValue();
			stopArea.setRegistrationNumber(stopPlaceShortName);

			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

			SimplePoint_VersionStructure centroidStruct = stopPlace.getCentroid();

			if (centroidStruct != null) {
				LocationStructure locationStruct = centroidStruct.getLocation();
				stopArea.setLongLatType(NetexUtils.toLongLatTypeEnum(locationStruct.getSrsName()));
				stopArea.setLatitude(locationStruct.getLatitude());
				stopArea.setLongitude(locationStruct.getLongitude());
			}

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
