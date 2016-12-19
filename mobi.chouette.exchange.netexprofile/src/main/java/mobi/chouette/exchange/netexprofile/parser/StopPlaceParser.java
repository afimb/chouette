package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import net.opengis.gml._3.DirectPositionType;
import org.rutebanken.netex.model.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Log4j
public class StopPlaceParser extends AbstractParser {

	private Map<String, Properties> tariffZoneProperties;

	@Override
	public void initReferentials(Context context) throws Exception {
	}

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

	private void parseStopPlace(Context context, StopPlace stopPlace, Map<String, String> parentZoneMap) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);

		StopArea stopArea = ObjectFactory.getStopArea(referential, stopPlace.getId());
		stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		Integer version = Integer.valueOf(stopPlace.getVersion());
		stopArea.setObjectVersion(version != null ? version : 0);
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

		stopArea.setFilled(true);
	}

	private void parseQuay(Context context, StopArea parentStopArea, Quay quay) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);

		StopArea quayStopArea = ObjectFactory.getStopArea(referential, quay.getId());
		quayStopArea.setAreaType(ChouetteAreaEnum.Quay);

		Integer version = Integer.valueOf(quay.getVersion());
		quayStopArea.setObjectVersion(version != null ? version : 0);
		quayStopArea.setName(quay.getName().getValue());

		quayStopArea.setParent(parentStopArea);

		if (quay.getDescription() != null) {
			quayStopArea.setComment(quay.getDescription().getValue());
		}
		if (quay.getLandmark() != null) {
			quayStopArea.setNearestTopicName(quay.getLandmark().getValue());
		}

		PrivateCodeStructure privateCodeStruct = quay.getPrivateCode();
		if (privateCodeStruct != null) {
			quayStopArea.setRegistrationNumber(privateCodeStruct.getValue());
		} else {
			if (quay.getShortName() != null) {
				quayStopArea.setRegistrationNumber(quay.getShortName().getValue());
			}
		}

		SimplePoint_VersionStructure centroidStruct = quay.getCentroid();
		if (centroidStruct != null) {
			parseCentroid(centroidStruct.getLocation(), quayStopArea);
		}

		PostalAddress postalAddress = quay.getPostalAddress();
		if (postalAddress != null) {
			quayStopArea.setCountryCode(postalAddress.getPostCode());
			quayStopArea.setStreetName(postalAddress.getAddressLine1().getValue());
		}

		TariffZoneRefs_RelStructure tariffZonesStruct = quay.getTariffZones();
		if (tariffZonesStruct != null) {
			parseTariffZoneRefs(tariffZonesStruct, quayStopArea);
		}

		quayStopArea.setFilled(true);
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
