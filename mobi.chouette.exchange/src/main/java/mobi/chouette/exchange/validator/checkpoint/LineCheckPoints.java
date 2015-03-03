package mobi.chouette.exchange.validator.checkpoint;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validator.ValidationConstraints;
import mobi.chouette.exchange.validator.ValidationData;
import mobi.chouette.exchange.validator.Validator;
import mobi.chouette.exchange.validator.parameters.ValidationParameters;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.Location;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;

@Log4j
public class LineCheckPoints extends AbstractValidation<Line> implements Validator<Line> {

	@Override
	public ValidationConstraints validate(Context context, Line target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		Line bean = data.getCurrentLine();
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (bean == null)
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		initCheckPoint(report, LINE_2, CheckPoint.SEVERITY.ERROR);

		// 3-Line-2 : check if line has routes
		// 4-Line-2 : check if line has valid transport mode
		// 4-Line-3 : check if line has one group and only one
		// 4-Line-4 : check if line has one route or one pair (inbound/outbound)

		boolean test4_1 = (parameters.getCheckLine() != 0);
		boolean test4_2 = parameters.getCheckAllowedTransportModes() == 1;
		boolean test4_3 = parameters.getCheckLinesInGroups() == 1;
		boolean test4_4 = parameters.getCheckLineRoutes() == 1;

		// checkPoint is applicable
		prepareCheckPoint(report, LINE_2);
		if (test4_1) {
			initCheckPoint(report, L4_LINE_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_LINE_1);
		}
		if (test4_2) {
			initCheckPoint(report, L4_LINE_2, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_LINE_2);
		}
		if (test4_3) {
			initCheckPoint(report, L4_LINE_3, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_LINE_3);
		}
		if (test4_4) {
			initCheckPoint(report, L4_LINE_4, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_LINE_4);
		}

		// en cas d'erreur, on reporte autant de detail que de lignes en
		// erreur
		
			// 3-Line-2 : check if line has routes
			check3Line2(report, bean);
			// 4-Line-1 : check columns constraints
			if (test4_1)
				check4Generic1(report, bean, L4_LINE_1, parameters, context, log);
			// 4-Line-2 : check if line has valid transportMode
			if (test4_2)
				check4Line2(report, bean, parameters);
			// 4-Line-3 : check if line has one group and only one
			if (test4_3)
				check4Line3(report, bean, parameters);
			// 4-Line-4 : check if line has one route or one pair
			// (inbound/outbound)
			if (test4_4)
				check4Line4(report, bean, parameters);

		return null;

	}


	/**
	 * @param report
	 * @param line1
	 */
	private void check3Line2(ValidationReport report, Line line1) {
		if (isEmpty(line1.getRoutes())) {
			// failure encountered, add line 1
			Location location = new Location(line1);

			Detail detail = new Detail(LINE_2, location);
			addValidationError(report, LINE_2, detail);
		}
	}

	private void check4Line2(ValidationReport report, Line line1, ValidationParameters parameters) {
		if (getModeParameters(parameters, line1.getTransportModeName().name(), log).getAllowedTransport() != 1) {
			// failure encountered, add line 1
			Location location = new Location(line1);

			Detail detail = new Detail(L4_LINE_2, location, line1.getTransportModeName().name());
			addValidationError(report, L4_LINE_2, detail);
		}

	}

	private void check4Line3(ValidationReport report, Line line1, ValidationParameters parameters) {
		if (line1.getGroupOfLines().size() == 0) {
			// failure encountered, add line 1
			Location location = new Location(line1);

			Detail detail = new Detail(L4_LINE_3 + "_1", location);
			addValidationError(report, L4_LINE_3, detail);
		} else if (line1.getGroupOfLines().size() > 1) {
			// failure encountered, add line 1
			Location location = new Location(line1);

			Detail detail = new Detail(L4_LINE_3 + "_2", location);
			addValidationError(report, L4_LINE_3, detail);
		}

	}

	private void check4Line4(ValidationReport report, Line line1, ValidationParameters parameters) {
		if (line1.getRoutes().size() == 1)
			return;
		if (line1.getRoutes().size() == 2) {
			Route r1 = line1.getRoutes().get(0);
			Route r2 = line1.getRoutes().get(1);
			if (r1.getOppositeRoute() == r2 && r2.getOppositeRoute() == r1)
				return;
		}
		// failure encountered, add line 1
		Location location = new Location(line1);

		if (line1.getRoutes().size() == 0) {
			Detail detail = new Detail(L4_LINE_4 + "_1", location);
			addValidationError(report, L4_LINE_4, detail);
		} else {
			Detail detail = new Detail(L4_LINE_4 + "_2", location, Integer.toString(line1.getRoutes().size()));
			addValidationError(report, L4_LINE_4, detail);

		}
	}

}
