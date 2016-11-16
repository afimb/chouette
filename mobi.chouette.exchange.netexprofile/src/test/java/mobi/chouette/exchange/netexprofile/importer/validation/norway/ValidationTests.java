package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.JobDataTest;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.NetexInitReferentialCommand;
import mobi.chouette.exchange.netexprofile.importer.NetexValidationCommand;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.CheckPointErrorReport;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.util.Referential;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ValidationTests implements Constant, ReportConstant {

    private static final String PATH = "src/test/data/norway_line_netex_err/";

    protected static InitialContext initialContext;

    protected void init() {
        Locale.setDefault(Locale.ENGLISH);
        if (initialContext == null) {
            try {
                initialContext = new InitialContext();
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
    }

    protected Context initImportContext() {
        init();
        ContextHolder.setContext("chouette_gui"); // set tenant schema

        Context context = new Context();
        context.put(INITIAL_CONTEXT, initialContext);
        context.put(REPORT, new ActionReport());
        context.put(VALIDATION_REPORT, new ValidationReport());

        NetexprofileImportParameters configuration = new NetexprofileImportParameters();
        configuration.setName("name");
        configuration.setUserName("userName");
        configuration.setNoSave(true);
        configuration.setOrganisationName("organisation");
        configuration.setReferentialName("test");
        context.put(CONFIGURATION, configuration);

        context.put(REFERENTIAL, new Referential());
        context.put(IMPORTER, new NetexImporter());
        context.put(NETEX_PROFILE_VALIDATOR, new NorwayLineNetexProfileValidator());

        JobDataTest test = new JobDataTest();
        context.put(JOB_DATA, test);

        test.setPathName("target/referential/test");
        File file = new File("target/referential/test");

        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        file.mkdirs();
        test.setReferential("chouette_gui");
        test.setAction(IMPORTER);
        test.setType("netexprofile");
        context.put("testng", "true");
        context.put(OPTIMIZED, Boolean.FALSE);
        return context;
    }

    protected void verifyValidation(String testFile, String mandatoryErrorTest, CheckPointReport.SEVERITY severity,
                                    ValidationReporter.RESULT status) throws Exception {
        Context context = initImportContext();

        NetexInitReferentialCommand initializer = (NetexInitReferentialCommand) CommandFactory.create(
                initialContext, NetexInitReferentialCommand.class.getName());
        File file = new File(PATH, testFile);
        initializer.setFileURL("file://" + file.getAbsolutePath());
        initializer.execute(context);

        NetexValidationCommand validator = (NetexValidationCommand) CommandFactory.create(
                initialContext, NetexValidationCommand.class.getName());
        validator.execute(context);

        checkMandatoryTest(context, mandatoryErrorTest, severity, status);
    }

    private void checkMandatoryTest(Context context, String mandatoryTest,
                                    CheckPointReport.SEVERITY severity, ValidationReporter.RESULT state) {
        ValidationReport valReport = (ValidationReport) context.get(VALIDATION_REPORT);

        if (mandatoryTest.equals("NONE")) {
            for (CheckPointReport phase : valReport.getCheckPoints()) {
                Assert.assertFalse(phase.getState().equals(ValidationReporter.RESULT.NOK),
                        phase.getName() + " must have status " + state);
            }
        } else {
            CheckPointReport foundItem = null;

            for (CheckPointReport cp : valReport.getCheckPoints()) {
                if (cp.getName().equals(mandatoryTest)) {
                    foundItem = cp;
                    break;
                }
            }

            Assert.assertNotNull(foundItem, mandatoryTest + " must be reported");
            Assert.assertEquals(foundItem.getSeverity(), severity, mandatoryTest + " must have severity " + severity);
            Assert.assertEquals(foundItem.getState(), state, mandatoryTest + " must have status " + state);

            if (foundItem.getState().equals(ValidationReporter.RESULT.NOK)) {
                String detailKey = mandatoryTest.replaceAll("-", "_").toLowerCase();
                Assert.assertNotEquals(foundItem.getCheckPointErrorCount(), 0, "details should be present");
                List<CheckPointErrorReport> details = checkReportForTest(valReport, mandatoryTest, -1);

                for (CheckPointErrorReport detail : details) {
                    Assert.assertTrue(detail.getKey().startsWith(detailKey), "details key should start with test key : expected " + detailKey + ", found : " + detail.getKey());
                }
            }
        }
    }

    protected List<CheckPointErrorReport> checkReportForTest(ValidationReport report, String key, int detailSize) {
        Assert.assertFalse(report.getCheckPoints().isEmpty(), " report must have items");
        Assert.assertNotNull(report.findCheckPointReportByName(key), " report must have 1 item on key " + key);
        CheckPointReport checkPointReport = report.findCheckPointReportByName(key);

        if (detailSize >= 0) {
            Assert.assertEquals(checkPointReport.getCheckPointErrorCount(), detailSize, " checkpoint must have " + detailSize + " detail");
        }

        List<CheckPointErrorReport> details = new ArrayList<>();

        for (Integer errorkey : checkPointReport.getCheckPointErrorsKeys()) {
            details.add(report.getCheckPointErrors().get(errorkey));
        }

        return details;
    }

}
