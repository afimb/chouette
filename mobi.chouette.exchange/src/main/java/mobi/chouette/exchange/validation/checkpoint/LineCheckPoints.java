package mobi.chouette.exchange.validation.checkpoint;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;

@Log4j
public class LineCheckPoints extends AbstractValidation<Line> implements Validator<Line> {

	@Override
	public void validate(Context context, Line target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Line bean = data.getCurrentLine();
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (bean == null)
			return ;
//		Monitor monitor = MonitorFactory.start(this.getClass().getSimpleName());
		// init checkPoints : add here all defined check points for this kind of
		// object
		initCheckPoint(context, LINE_2, SEVERITY.E);

		// 3-Line-2 : check if line has routes
		// 4-Line-2 : check if line has valid transport mode
		// 4-Line-3 : check if line has one group and only one
		// 4-Line-4 : check if line has one route or one pair (inbound/outbound)

		boolean test4_1 = parameters.getCheckLine() != 0;
		boolean test4_2 = parameters.getCheckAllowedTransportModes() == 1;
		boolean test4_3 = parameters.getCheckLinesInGroups() == 1;
		boolean test4_4 = parameters.getCheckLineRoutes() == 1;
		

		// checkPoint is applicable
		prepareCheckPoint(context, LINE_2);
		if (test4_1) {
			initCheckPoint(context, L4_LINE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_LINE_1);
		}
		if (test4_2) {
			initCheckPoint(context, L4_LINE_2, SEVERITY.E);
			prepareCheckPoint(context, L4_LINE_2);
		}
		if (test4_3) {
			initCheckPoint(context, L4_LINE_3, SEVERITY.E);
			prepareCheckPoint(context, L4_LINE_3);
		}
		if (test4_4) {
			initCheckPoint(context, L4_LINE_4, SEVERITY.E);
			prepareCheckPoint(context, L4_LINE_4);
		}

		// en cas d'erreur, on reporte autant de detail que de lignes en
		// erreur
		    // 3-Line-1 : TODO lignes homonymes sur le même réseau
		
			// 3-Line-2 : check if line has routes
			check3Line2(context, bean);
			// 4-Line-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, bean, L4_LINE_1, parameters, log);
			// 4-Line-2 : check if line has valid transportMode
			if (test4_2)
				check4Line2(context, bean, parameters);
			// 4-Line-3 : check if line has one group and only one
			if (test4_3)
				check4Line3(context, bean, parameters);
			// 4-Line-4 : check if line has one route or one pair
			// (inbound/outbound)
			if (test4_4)
				check4Line4(context, bean, parameters);

//			log.info(Color.CYAN + monitor.stop() + Color.NORMAL);
		return ;

	}


	/**
	 * @param context 
	 * @param report
	 * @param line1
	 */
	private void check3Line2(Context context,  Line line1) {
		if (isEmpty(line1.getRoutes())) {
			// failure encountered, add line 1
			DataLocation location = buildLocation(context,line1);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, LINE_2, location);
		}
	}

	private void check4Line2(Context context,  Line line1, ValidationParameters parameters) {
		if (getModeParameters(parameters, line1.getTransportModeName().name(), log).getAllowedTransport() != 1) {
			// failure encountered, add line 1
			DataLocation location = buildLocation(context,line1);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, L4_LINE_2, location, line1.getTransportModeName().name());
		}

	}

	private void check4Line3(Context context,  Line line1, ValidationParameters parameters) {
		if (line1.getGroupOfLines().size() == 0) {
			// failure encountered, add line 1
			DataLocation location = buildLocation(context,line1);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, L4_LINE_3 , "1", location);
		} else if (line1.getGroupOfLines().size() > 1) {
			// failure encountered, add line 1
			DataLocation location = buildLocation(context,line1);

			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context, L4_LINE_3 , "2", location);
		}

	}

	private void check4Line4(Context context,  Line line1, ValidationParameters parameters) {
		if (line1.getRoutes().size() == 1)
			return;
		if (line1.getRoutes().size() == 2) {
			Route r1 = line1.getRoutes().get(0);
			Route r2 = line1.getRoutes().get(1);
			if (r1.getOppositeRoute() == r2 && r2.getOppositeRoute() == r1)
				return;
		}
		// failure encountered, add line 1
		DataLocation location = buildLocation(context,line1);
		ValidationReporter reporter = ValidationReporter.Factory.getInstance();

		if (line1.getRoutes().size() == 0) {
			reporter.addCheckPointReportError(context,L4_LINE_4, "1", location);
		} else {
			reporter.addCheckPointReportError(context,L4_LINE_4, "2", location, Integer.toString(line1.getRoutes().size()));

		}
	}

}
