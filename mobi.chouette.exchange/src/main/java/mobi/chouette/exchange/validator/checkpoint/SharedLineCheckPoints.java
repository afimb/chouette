package mobi.chouette.exchange.validator.checkpoint;

import java.util.ArrayList;
import java.util.List;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.validator.ValidationConstraints;
import mobi.chouette.exchange.validator.ValidationData;
import mobi.chouette.exchange.validator.Validator;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.Location;
import mobi.chouette.exchange.validator.report.ValidationReport;
import mobi.chouette.model.Line;

public class SharedLineCheckPoints extends AbstractValidation<Line> implements Validator<Line> {

	@Override
	public ValidationConstraints validate(Context context, Line target) {
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
			check3Line1(beans, report, i, line1);
		}
		return null;

	}

	/**
	 * @param beans
	 * @param report
	 * @param lineRank
	 * @param line1
	 */
	private void check3Line1(List<Line> beans, ValidationReport report, int lineRank, Line line1) {
		if (beans.size() <= 1)
			return;
		boolean error_1 = false; // if true, add detail for this line
		if (line1.getPtNetwork() == null)
			return;
		prepareCheckPoint(report, LINE_1);
		for (int j = lineRank + 1; j < beans.size(); j++) {
			Line line2 = beans.get(j);
			if (line2.getPtNetwork() == null)
				continue;

			if (line2.getPtNetwork().equals(line1.getPtNetwork())) {
				if (line1.getName().equals(line2.getName()) && line1.getNumber().equals(line2.getNumber())) {
					// failure ! add only line2 location
					Location location = new Location(line2);
					Location networkLocation = new Location(line2.getPtNetwork());
					Detail detail = new Detail(LINE_1, location, networkLocation);
					addValidationError(report, LINE_1, detail);

					error_1 = true; // to add detail for line1
				}
			}

		}
		if (error_1) {
			// failure encountered, add line 1
			Location location = new Location(line1);
			Location networkLocation = new Location(line1.getPtNetwork());

			Detail detail = new Detail(LINE_1, location, networkLocation);
			addValidationError(report, LINE_1, detail);
		}
	}



}
