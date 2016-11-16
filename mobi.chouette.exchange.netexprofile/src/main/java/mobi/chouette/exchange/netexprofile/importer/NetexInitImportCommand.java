package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.naming.InitialContext;
import java.io.IOException;

@Log4j
public class NetexInitImportCommand implements Command, Constant {

    public static final String COMMAND = "NetexInitImportCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);

        try {
            log.info("Context on NetexInitImportCommand=" + ToStringBuilder.reflectionToString(context));

            NetexImporter importer = new NetexImporter();
            context.put(IMPORTER, importer);

            NetexProfileValidator profileValidator = importer.getProfileValidator(context);
            if (profileValidator != null) {
                context.put(NETEX_PROFILE_VALIDATOR, profileValidator);
            }

            context.put(REFERENTIAL, new Referential());

            if (context.get(VALIDATION) != null) {
                context.put(VALIDATION_DATA, new ValidationData());
            }

            ActionReporter reporter = ActionReporter.Factory.getInstance();
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.NETWORK, "networks", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.STOP_AREA, "stop areas", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.COMPANY, "companies", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.CONNECTION_LINK, "connection links", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.ACCESS_POINT, "access points", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
            reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.TIMETABLE, "calendars", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);

            result = SUCCESS;
        } catch (Exception e) {
            log.error(e, e);
            throw e;
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }

        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexInitImportCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexInitImportCommand.class.getName(), new DefaultCommandFactory());
    }

}
