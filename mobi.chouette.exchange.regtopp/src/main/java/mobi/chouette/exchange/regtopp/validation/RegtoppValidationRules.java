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

		checkPoints.add(new CheckPoint(REGTOPP_FILE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
//		checkPoints.add(new CheckPoint(REGTOPP_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_FILE_WITH_NO_ENTRY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MISSING_MANDATORY_FILES, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MULTIPLE_ADMIN_CODES, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_TIX, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TIX_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TIX_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TIX_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TIX_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_HPL, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_HPL_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_HPL_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_HPL_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_HPL_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_DKO, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DKO_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DKO_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DKO_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DKO_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_GAV, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_GAV_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_GAV_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_GAV_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_GAV_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_TMS, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TMS_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TMS_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TMS_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TMS_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_DST, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DST_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DST_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DST_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_DST_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_MRK, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MRK_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MRK_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MRK_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_MRK_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_LIN, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_LIN_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_LIN_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_LIN_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_LIN_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_TDA, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TDA_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TDA_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TDA_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_TDA_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_FILE_STP, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_STP_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_STP_INVALID_MANDATORY_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_STP_INVALID_OPTIONAL_ID_REFERENCE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
		checkPoints.add(new CheckPoint(REGTOPP_STP_DUPLICATE_KEY, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		checkPoints.add(new CheckPoint(REGTOPP_VLP_INVALID_FIELD_VALUE, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));

		return checkPoints;
	}
}
