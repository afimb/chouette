package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.TransportModeParameters;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.RouteSection;

@Log4j
public class RouteSectionCheckPoints extends AbstractValidation<RouteSection> implements Validator<RouteSection>{
	@Override
	public void validate(Context context, RouteSection target)
			throws ValidationException {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<JourneyPattern> beans = new ArrayList<>(data.getJourneyPatterns());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		
		if (isEmpty(beans))
			return ;
		
		initCheckPoint(context, ROUTE_SECTION_1, SEVERITY.W);
		// 3-RouteSection-1 : Check if route section distance doesn't exceed gap as parameter
		
		// checkPoint is applicable
		for (int i = 0; i < beans.size(); i++) {
			List<RouteSection> lstRouteSection = beans.get(i).getRouteSections();
			for(int j = 0; j < lstRouteSection.size(); j++) {
				RouteSection rs = lstRouteSection.get(j);
				// 3-RouteSection-1 : Check if route section distance doesn't exceed gap as parameter
				check3RouteSection1(context,report, beans, i, rs, parameters);
			}
		}	
	}



	//3-RouteSection-1 : Check if route section distance doesn't exceed gap as parameter
	private void check3RouteSection1(Context context, ValidationReport report, List<JourneyPattern> beans, int jpRank,
			RouteSection rs, ValidationParameters parameters) {
		if (beans.size() <= 1)
			return;
		prepareCheckPoint(context, ROUTE_SECTION_1);
		
		for (int i = jpRank + 1; i < beans.size(); i++) {
			JourneyPattern jp2 = beans.get(i);
			String modeKey = jp2.getRoute().getLine().getTransportModeName().toString();
			TransportModeParameters mode = getModeParameters(parameters, modeKey, log);
			if (mode == null) {
				log.error("no parameters for mode " + modeKey);
				mode = getModeParameters(parameters, MODE_OTHER, log);
				if (mode == null) {
					log.error("no parameters for mode " + MODE_OTHER);
					mode = modeDefault;
				}
			}
			double distanceMax = mode.getInterStopAreaDistanceMax();
			List<RouteSection> lstRouteSection = jp2.getRouteSections();
			for(int j = 0; j < lstRouteSection.size(); j++) {
					RouteSection rs2 = lstRouteSection.get(j);
				if (rs.equals(rs2)) {
					double distance = distance(rs.getDeparture(), rs.getArrival());
					// If route section distance doesn't exceed gap    as parameter
					if(distance > distanceMax) {
						DataLocation location = buildLocation(context, rs2);
						DataLocation targetLocation = buildLocation(context, rs.getDeparture());

						ValidationReporter reporter = ValidationReporter.Factory.getInstance();
						reporter.addCheckPointReportError(context,ROUTE_SECTION_1, location, null,null,targetLocation);
					}
				}	
			}
		}
	}

}
