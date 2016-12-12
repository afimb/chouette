package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.model.util.Referential;

import javax.naming.InitialContext;
import java.io.IOException;

@Log4j
public class NetexValidationCommand implements Command, Constant {

    public static final String COMMAND = "NetexValidationCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        String fileName = (String) context.get(FILE_NAME);

        try {
            Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
            Referential referential = (Referential) context.get(REFERENTIAL);

            NetexProfileValidator validator = (NetexProfileValidator) context.get(NETEX_PROFILE_VALIDATOR);
            validator.validate(context);

            result = !reporter.hasFileValidationErrors(context, fileName);
        } catch (Exception e) {
            log.error("Netex validation failed ", e);
            throw e;
        } finally {
            AbstractNetexProfileValidator.resetContext(context);
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }
        if (result == ERROR) {
            log.error("Netex compliance failed");
            reporter.addFileErrorInReport(context, fileName,
                    ActionReporter.FILE_ERROR_CODE.INVALID_FORMAT, "Netex compliance failed");
        }

        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {
        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexValidationCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexValidationCommand.class.getName(),
                new NetexValidationCommand.DefaultCommandFactory());
    }

}
