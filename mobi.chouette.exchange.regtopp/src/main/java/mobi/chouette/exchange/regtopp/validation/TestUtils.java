package mobi.chouette.exchange.regtopp.validation;

import mobi.chouette.exchange.TestDescription;

import java.util.ArrayList;
import java.util.List;

import static mobi.chouette.exchange.regtopp.validation.Constant.*;

public class TestUtils {
	protected List<TestDescription> testList = null;
	
	private static TestUtils singleton = null;
	
	private TestUtils() {
			testList = new ArrayList<>();

		/**


		 public static final String REGTOPP_HPL_INVALID_FIELD_VALUE = "2-REGTOPP-HPL-1";
		 public static final String REGTOPP_HPL_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-HPL-2";
		 public static final String REGTOPP_HPL_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-HPL-3";
		 public static final String REGTOPP_HPL_DUPLICATE_KEY = "2-REGTOPP-HPL-4";

		 public static final String REGTOPP_DKO_INVALID_FIELD_VALUE = "2-REGTOPP-DKO-1";
		 public static final String REGTOPP_DKO_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-DKO-2";
		 public static final String REGTOPP_DKO_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-DKO-3";
		 public static final String REGTOPP_DKO_DUPLICATE_KEY = "2-REGTOPP-DKO-4";

		 public static final String REGTOPP_GAV_INVALID_FIELD_VALUE = "2-REGTOPP-GAV-1";
		 public static final String REGTOPP_GAV_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-GAV-2";
		 public static final String REGTOPP_GAV_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-GAV-3";
		 public static final String REGTOPP_GAV_DUPLICATE_KEY = "2-REGTOPP-GAV-4";

		 public static final String REGTOPP_TMS_INVALID_FIELD_VALUE = "2-REGTOPP-TMS-1";
		 public static final String REGTOPP_TMS_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-TMS-2";
		 public static final String REGTOPP_TMS_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-TMS-3";
		 public static final String REGTOPP_TMS_DUPLICATE_KEY = "2-REGTOPP-TMS-4";

		 public static final String REGTOPP_DST_INVALID_FIELD_VALUE = "2-REGTOPP-DST-1";
		 public static final String REGTOPP_DST_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-DST-2";
		 public static final String REGTOPP_DST_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-DST-3";
		 public static final String REGTOPP_DST_DUPLICATE_KEY = "2-REGTOPP-DST-4";

		 public static final String REGTOPP_MRK_INVALID_FIELD_VALUE = "2-REGTOPP-MRK-1";
		 public static final String REGTOPP_MRK_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-MRK-2";
		 public static final String REGTOPP_MRK_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-MRK-3";
		 public static final String REGTOPP_MRK_DUPLICATE_KEY = "2-REGTOPP-MRK-4";

		 public static final String REGTOPP_LIN_INVALID_FIELD_VALUE = "2-REGTOPP-LIN-1";
		 public static final String REGTOPP_LIN_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-LIN-2";
		 public static final String REGTOPP_LIN_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-LIN-3";
		 public static final String REGTOPP_LIN_DUPLICATE_KEY = "2-REGTOPP-LIN-4";

		 public static final String REGTOPP_TDA_INVALID_FIELD_VALUE = "2-REGTOPP-TDA-1";
		 public static final String REGTOPP_TDA_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-TDA-2";
		 public static final String REGTOPP_TDA_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-TDA-3";
		 public static final String REGTOPP_TDA_DUPLICATE_KEY = "2-REGTOPP-TDA-4";

		 public static final String REGTOPP_STP_INVALID_FIELD_VALUE = "2-REGTOPP-STP-1";
		 public static final String REGTOPP_STP_INVALID_MANDATORY_ID_REFERENCE = "2-REGTOPP-STP-2";
		 public static final String REGTOPP_STP_INVALID_OPTIONAL_ID_REFERENCE = "2-REGTOPP-STP-3";
		 public static final String REGTOPP_STP_DUPLICATE_KEY = "2-REGTOPP-STP-4";

		 public static final String REGTOPP_VLP_INVALID_FIELD_VALUE = "2-REGTOPP-VLP-1";
		 */

		testList.add(new TestDescription(1, REGTOPP_FILE, "ERROR"));
		testList.add(new TestDescription(1, REGTOPP_MULTIPLE_ADMIN_CODES, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_WITH_NO_ENTRY, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_MISSING_MANDATORY_FILES, "WARNING"));

		testList.add(new TestDescription(1, REGTOPP_FILE_TIX, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_HPL, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_DKO, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_GAV, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_TMS, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_DST, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_MRK, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_LIN, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_TDA, "WARNING"));
		testList.add(new TestDescription(1, REGTOPP_FILE_STP, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_TIX_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TIX_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TIX_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TIX_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_HPL_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_HPL_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_HPL_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_HPL_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_DKO_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DKO_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DKO_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DKO_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_GAV_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_GAV_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_GAV_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_GAV_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_TMS_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TMS_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TMS_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TMS_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_DST_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DST_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DST_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_DST_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_MRK_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_MRK_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_MRK_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_MRK_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_LIN_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_LIN_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_LIN_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_LIN_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_TDA_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TDA_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TDA_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_TDA_DUPLICATE_KEY, "WARNING"));

		testList.add(new TestDescription(2, REGTOPP_STP_INVALID_FIELD_VALUE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_STP_INVALID_MANDATORY_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_STP_INVALID_OPTIONAL_ID_REFERENCE, "WARNING"));
		testList.add(new TestDescription(2, REGTOPP_STP_DUPLICATE_KEY, "WARNING"));
	}
	
	
	public List<TestDescription> getTestUtilsList() {
		return testList;
	}
	
	public static TestUtils getInstance() {
		if(singleton == null) {
			singleton = new TestUtils();
		}
		
		return singleton;
	}
}
