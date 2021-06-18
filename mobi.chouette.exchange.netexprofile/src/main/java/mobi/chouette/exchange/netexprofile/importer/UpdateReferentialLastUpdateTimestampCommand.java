package mobi.chouette.exchange.netexprofile.importer;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.ReferentialLastUpdateDAO;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.ReportConstant;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *  Update the last update timestamp for the current referential.
 *  This occurs when a new dataset is successfully imported, or when a dataset is transfered.
 */
@Log4j
@Stateless(name = UpdateReferentialLastUpdateTimestampCommand.COMMAND)
public class UpdateReferentialLastUpdateTimestampCommand implements Command, ReportConstant {

    public static final String COMMAND = "UpdateReferentialLastUpdateTimestampCommand";

    @EJB
    private ReferentialLastUpdateDAO referentialLastUpdateDAO;

    @Override
    public boolean execute(Context context) throws Exception {

        ActionReport report = (ActionReport) context.get(REPORT);
        ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
        if(report != null && validationReport != null && STATUS_OK.equals(report.getResult()) && !ValidationReporter.VALIDATION_RESULT.ERROR.equals(validationReport.getResult())) {
            // in case of a transfer, the new timestamp for the target referential is the timestamp of the source referential
            LocalDateTime lastUpdateTimestamp = (LocalDateTime) context.get(REFERENTIAL_LAST_UPDATE_TIMESTAMP);
            // in case of an import, the new timestamp is the current time
            if(lastUpdateTimestamp == null) {
                lastUpdateTimestamp= LocalDateTime.now();
            }
            referentialLastUpdateDAO.setLastUpdateTimestamp(lastUpdateTimestamp);
        }
        return true;
    }


    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = null;
            try {
                String name = "java:app/mobi.chouette.exchange.netexprofile/" + COMMAND;
                result = (Command) context.lookup(name);
            } catch (NamingException e) {
                String name = "java:module/" + COMMAND;
                try {
                    result = (Command) context.lookup(name);
                } catch (NamingException e1) {
                    log.error(e);
                }
            }
            return result;
        }
    }


    static {
        CommandFactory.factories.put(UpdateReferentialLastUpdateTimestampCommand.class.getName(),
                new UpdateReferentialLastUpdateTimestampCommand.DefaultCommandFactory());
    }
}
