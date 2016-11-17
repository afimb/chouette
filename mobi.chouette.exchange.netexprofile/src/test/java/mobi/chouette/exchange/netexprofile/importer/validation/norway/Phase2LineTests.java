package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.testng.annotations.Test;

public class Phase2LineTests extends ValidationTests {

    @Test(groups = { "Phase 2 line" }, description = "Missing mandatory element : 'Name'")
    public void verifyTest_2_Line_1() throws Exception {
        verifyValidation( "2-NETEX-Line-1.xml", "2-NETEX-Line-1",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

    @Test(groups = {"Phase 2 line"}, description = "Missing mandatory element : 'TransportMode'")
    public void verifyTest_2_Line_2() throws Exception {
        verifyValidation( "2-NETEX-Line-2.xml", "2-NETEX-Line-2",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

    @Test(groups = {"Phase 2 line"}, description = "Missing mandatory element : 'PublicCode'")
    public void verifyTest_2_Line_3() throws Exception {
        verifyValidation( "2-NETEX-Line-3.xml", "2-NETEX-Line-3",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

    @Test(groups = {"Phase 2 line"}, description = "Missing mandatory element : 'OperatorRef'")
    public void verifyTest_2_Line_4() throws Exception {
        verifyValidation( "2-NETEX-Line-4.xml", "2-NETEX-Line-4",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

    @Test(groups = {"Phase 2 line"}, description = "Missing route reference in context")
    public void verifyTest_2_Line_5() throws Exception {
        verifyValidation( "2-NETEX-Line-5.xml", "2-NETEX-Line-5",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

}
