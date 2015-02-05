package mobi.chouette.exchange.gtfs.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.gtfs.Constant;
import mobi.chouette.exchange.gtfs.importer.GtfsParameters;
import mobi.chouette.exchange.gtfs.model.GtfsStop;
import mobi.chouette.exchange.gtfs.model.GtfsStop.WheelchairBoardingType;
import mobi.chouette.exchange.gtfs.model.importer.GtfsImporter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.report.Report;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class GtfsStopParser implements Parser, Constant {

	private Referential referential;
	private GtfsImporter importer;
	private GtfsParameters configuration;

	@Override
	public void parse(Context context) throws Exception {

		referential = (Referential) context.get(REFERENTIAL);
		importer = (GtfsImporter) context.get(IMPORTER);
		configuration = (GtfsParameters) context.get(CONFIGURATION);

		for (GtfsStop gtfsStop : importer.getStopById()) {
			if (gtfsStop.getLocationType() != GtfsStop.LocationType.Access) {

				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),
						StopArea.STOPAREA_KEY, gtfsStop.getStopId(), log);

				if (gtfsStop.getLocationType() != GtfsStop.LocationType.Access) {
					StopArea stopArea = ObjectFactory.getStopArea(referential,
							objectId);
					convert(context, gtfsStop, stopArea);
				}
			}
		}
	}

	protected void convert(Context context, GtfsStop gtfsStop, StopArea stopArea) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		Report report = (Report) context.get(REPORT);

		// objectId, objectVersion, creatorId, creationTime

		stopArea.setLatitude(gtfsStop.getStopLat());
		stopArea.setLongitude(gtfsStop.getStopLon());
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);

		// Name optional
		stopArea.setName(AbstractConverter.getNonEmptyTrimedString(gtfsStop
				.getStopName()));

		// URL optional
		stopArea.setUrl(AbstractConverter.toString(gtfsStop.getStopUrl()));

		// Comment optional
		stopArea.setComment(AbstractConverter.getNonEmptyTrimedString(gtfsStop
				.getStopDesc()));

		// timezone
		stopArea.setTimeZone(AbstractConverter.toString(gtfsStop
				.getStopTimezone()));

		// farecode
		stopArea.setFareCode(0);

		if (gtfsStop.getLocationType() == GtfsStop.LocationType.Station) {
			stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
			if (AbstractConverter.getNonEmptyTrimedString(gtfsStop
					.getParentStation()) != null) {
				// TODO report
			}
		} else {
			if (!importer.getStopById()
					.containsKey(gtfsStop.getParentStation())) {
				// TODO report
			}
			stopArea.setAreaType(ChouetteAreaEnum.BoardingPosition);
			StopArea parent = ObjectFactory.getStopArea(referential,
					AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(),StopArea.STOPAREA_KEY,
							gtfsStop.getParentStation(), log));

			stopArea.setParent(parent);
		}

		// RegistrationNumber optional
		stopArea.setRegistrationNumber(gtfsStop.getStopCode());

		// MobilityRestrictedSuitable
		stopArea.setMobilityRestrictedSuitable(WheelchairBoardingType.Allowed
				.equals(gtfsStop.getWheelchairBoarding()));

		// extension
		stopArea.setStreetName(gtfsStop.getAddressLine());
		stopArea.setCityName(gtfsStop.getLocality());
		stopArea.setZipCode(gtfsStop.getPostalCode());

	}

	static {
		ParserFactory.register(GtfsStopParser.class.getName(),
				new ParserFactory() {
					private GtfsStopParser instance = new GtfsStopParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}

}
