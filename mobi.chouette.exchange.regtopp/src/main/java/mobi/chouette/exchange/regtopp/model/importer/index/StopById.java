package mobi.chouette.exchange.regtopp.model.importer.index;

import java.io.IOException;

import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppImporter;

public class StopById extends IndexImpl<RegtoppStopHPL>   {

	public StopById(FileContentParser fileParser) throws IOException {
		super(fileParser);
	}


	public static final String FILETYPE = ".HPL";

	private RegtoppStopHPL bean = new RegtoppStopHPL();
	private String _stopId = null;
	

	@Override
	public boolean validate(RegtoppStopHPL bean, RegtoppImporter dao) {
		boolean result = true;
		System.err.println("Validation code for RegtoppStopp commented out");
	/*	
		RegtoppStopHPL copy_bean = new RegtoppStopHPL(bean);
		String parentStationId = copy_bean.getParentStation();
		if (isPresent(parentStationId)) {

			// parentStation must reference a stop
			GtfsStop parent = dao.getStopById().getValue(parentStationId);
			if (parent == null) {
				result = false;
				bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), RegtoppException.ERROR.UNREFERENCED_ID, copy_bean.getStopId(), parentStationId));
			} else if (copy_bean.getLocationType() == LocationType.Station) {
				result = false;
				bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), RegtoppException.ERROR.NO_PARENT_FOR_STATION, copy_bean.getStopId(), parentStationId));
			} else {
				bean.getOkTests().add(RegtoppException.ERROR.UNREFERENCED_ID);
				bean.getOkTests().add(RegtoppException.ERROR.NO_PARENT_FOR_STATION);
			}
			
			// the stop parentStation is a station
			if (result) { // Stop, Station, Access
				if (LocationType.Station != parent.getLocationType()) {
					result = false;
					if (parent.getLocationType() != null)
					   bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), RegtoppException.ERROR.BAD_REFERENCED_ID, copy_bean.getStopId(), Integer.toString(parent.getLocationType().ordinal())));
					else
					   bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.parent_station.name()), FIELDS.parent_station.name(), RegtoppException.ERROR.BAD_REFERENCED_ID, copy_bean.getStopId(), "0"));
					   
				} else {
					bean.getOkTests().add(RegtoppException.ERROR.BAD_REFERENCED_ID);
				}
			}
		}
		
		// stopId is used by a stop_time. OK: See GtfsTripParser.validateStopTimes(FileParserValidationContext)
		
		// stopDesc != stopName
		boolean result2 = true;
		String stopName = copy_bean.getStopName();
		String stopDesc = copy_bean.getStopDesc();
		if (stopName != null && stopDesc != null) {
			if (stopName.equals(stopDesc)) {
				result2 = false;
				bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.stop_name.name()), FIELDS.stop_name.name(), RegtoppException.ERROR.BAD_VALUE, copy_bean.getStopId(), stopName));
			} else {
				bean.getOkTests().add(RegtoppException.ERROR.BAD_VALUE);
			}
		}
		result = result && result2;
		
		// stopUrl != GtfsAgency.agencyUrl
		boolean result3 = true;
		if (copy_bean.getStopUrl() != null) {
			for (GtfsAgency agency : (AgencyById)dao.getAgencyById()) {
				if (agency.getAgencyUrl() != null) {
					if (copy_bean.getStopUrl().equals(agency.getAgencyUrl())) {
						result3 = false;
						bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.stop_url.name()), FIELDS.stop_url.name()+","+AgencyById.FIELDS.agency_url.name(), RegtoppException.ERROR.SHARED_VALUE,  copy_bean.getStopId(), copy_bean.getStopUrl().toString()));
						break;
					}
				}
			}
			for (GtfsRoute route : (RouteById)dao.getRouteById()) {
				if (route.getRouteUrl() != null) {
					if (copy_bean.getStopUrl().equals(route.getRouteUrl())) {
						result3 = false;
						bean.getErrors().add(new RegtoppException(_path, copy_bean.getId(), getIndex(FIELDS.stop_url.name()), FIELDS.stop_url.name()+","+RouteById.FIELDS.route_url.name(), RegtoppException.ERROR.SHARED_VALUE, copy_bean.getStopId(), copy_bean.getStopUrl().toString()));
						break;
					}
				}
			}
		}
		if (result3)
			bean.getOkTests().add(RegtoppException.ERROR.SHARED_VALUE);
		result = result && result3;
		
		// stopUrl != routeUrl. OK: See RouteById.validate(GtfsRoute bean, GtfsImporter dao)
		
		// locationType is set for at least one stop. OK: See GtfsStopParser.validate(FileParserValidationContext context)
*/
		return result;
	}
	
	private void clearBean() {
		//bean.getErrors().clear();
		bean.setId(null);
//		bean.setAddressLine(null);
//		bean.setLocality(null);
//		bean.setLocationType(null);
//		bean.setParentStation(null);
//		bean.setPostalCode(null);
//		bean.setStopCode(null);
//		bean.setStopDesc(null);
		bean.setStopId(null);
		bean.setStopLat(null);
		bean.setStopLon(null);
//		bean.setStopName(null);
//		bean.setStopTimezone(null);
//		bean.setStopUrl(null);
//		bean.setWheelchairBoarding(null);
//		bean.setZoneId(null);
	}

	public static class DefaultImporterFactory extends IndexFactory {
		@SuppressWarnings("rawtypes")
		@Override
		protected Index create(FileContentParser parser) throws IOException {
			return new StopById(parser);
		}
	}

	static {
		IndexFactory factory = new DefaultImporterFactory();
		IndexFactory.factories.put(StopById.class.getName(), factory);
	}


	@Override
	protected void index() throws IOException {
		for(Object obj : _parser.getRawContent()) {
			RegtoppStopHPL stop = (RegtoppStopHPL) obj;
			_index.put(stop.getStopId(), stop);
		}
	}
}
