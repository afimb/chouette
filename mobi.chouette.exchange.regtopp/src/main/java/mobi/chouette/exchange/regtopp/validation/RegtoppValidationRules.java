package mobi.chouette.exchange.regtopp.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;

public class RegtoppValidationRules implements Constant {

	public List<CheckPoint> checkPoints(RegtoppImportParameters parameters) {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();

		// TODO update with real checkpoints
		checkPoints.addAll(commonCheckPoints());
		return checkPoints;
	}

	private Collection<? extends CheckPoint> commonCheckPoints() {
		List<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
		checkPoints.add(new CheckPoint(REGTOPP_SYSTEM, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));

		return checkPoints;
	}
}
