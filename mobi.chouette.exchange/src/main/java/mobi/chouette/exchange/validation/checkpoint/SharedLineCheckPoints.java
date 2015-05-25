package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;

public class SharedLineCheckPoints extends AbstractValidation<Line> implements Validator<Line> {

	@Override
	public ValidationConstraints validate(Context context, Line target) {
		// CAUTION : here line is a shared clone with only used content
		// if more data needed, change cloneLine in ValidationDataCollector class
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Line> beans = new ArrayList<>(data.getLines());
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;
		// init checkPoints : add here all defined check points for this kind of
		// object
		initCheckPoint(report, LINE_1, CheckPoint.SEVERITY.WARNING);

		// 3-Line-1 : check if two lines have same name

		// checkPoint is applicable

		// en cas d'erreur, on reporte autant de detail que de lignes en
		// erreur
		for (int i = 0; i < beans.size(); i++) {
			Line line1 = beans.get(i);
			// 3-Line-1 : check if two lines have same name
			check3Line1(context,beans, report, i, line1);
		}
		return null;

	}

	/**
	 * @param beans
	 * @param report
	 * @param lineRank
	 * @param line1
	 */
	private void check3Line1(Context context,List<Line> beans, ValidationReport report, int lineRank, Line line1) {
		if (beans.size() <= 1)
			return;
		boolean error_1 = false; // if true, add detail for this line
		if (line1.getNetwork() == null)
			return;
		prepareCheckPoint(report, LINE_1);
		for (int j = lineRank + 1; j < beans.size(); j++) {
			Line line2 = beans.get(j);
			if (line2.getNetwork() == null)
				continue;

			if (line2.getNetwork().equals(line1.getNetwork())) {
				if (checkEquals(line1.getName(),line2.getName()) && checkEquals(line1.getNumber(),line2.getNumber())) {
					// failure ! add only line2 location
					Location location = buildLocation(context,line2);
					Location networkLocation = buildLocation(context,line2.getNetwork());
					Detail detail = new Detail(LINE_1, location, networkLocation);
					addValidationError(report, LINE_1, detail);

					error_1 = true; // to add detail for line1
				}
			}

		}
		if (error_1) {
			// failure encountered, add line 1
			Location location = buildLocation(context,line1);
			Location networkLocation = buildLocation(context,line1.getNetwork());

			Detail detail = new Detail(LINE_1, location, networkLocation);
			addValidationError(report, LINE_1, detail);
		}
	}


}
