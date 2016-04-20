package mobi.chouette.exchange.regtopp.validation;

import static mobi.chouette.exchange.regtopp.validation.Constant.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;

public class RegtoppValidationRules {

	public List<CheckPoint> checkPoints(RegtoppImportParameters parameters) {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();

		checkPoints.addAll(commonCheckPoints());
		return checkPoints;
	}

	private Collection<? extends CheckPoint> commonCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();

		checkPoints.add(new CheckPoint(REGTOPP_FILE_TIX, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_FILE_TMS, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_FILE_HPL, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		return checkPoints;
	}
}
