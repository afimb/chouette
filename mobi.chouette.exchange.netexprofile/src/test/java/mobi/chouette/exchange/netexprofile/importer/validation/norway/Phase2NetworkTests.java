package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.testng.annotations.Test;

public class Phase2NetworkTests extends ValidationTests {

    @Test(groups = {"Phase 2 network"}, description = "Mandatory element 'groupsOfLines' missing in Network")
    public void verifyTest_2_Network_1() throws Exception {
        verifyValidation( "2-NETEX-Network-2.xml", "2-NETEX-Network-2",
                CheckPointReport.SEVERITY.ERROR, ValidationReporter.RESULT.NOK);
    }

}