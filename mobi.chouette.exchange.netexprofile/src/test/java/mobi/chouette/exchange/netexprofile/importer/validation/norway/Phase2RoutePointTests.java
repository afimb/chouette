package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.testng.annotations.Test;

public class Phase2RoutePointTests extends ValidationTests {

    @Test(groups = {"Phase 2 route point"}, description = "Non-existent stop point reference")
    public void verifyTest_2_RoutePoint_3() throws Exception {
        verifyValidation( "2-NETEX-RoutePoint-3.xml", "2-NETEX-RoutePoint-3",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

}
