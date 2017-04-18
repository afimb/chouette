package mobi.chouette.exchange.netexprofile.exporter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.StopPlaceProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.StopPlace;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j
public class NetexSharedDataProducerCommand implements Command, Constant {

    public static final String COMMAND = "NetexSharedDataProducerCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);
        ActionReporter reporter = ActionReporter.Factory.getInstance();

        try {
            Referential referential = (Referential) context.get(REFERENTIAL);
            if (referential == null) {
                return ERROR;
            }

            Map<String, StopArea> sharedStopAreas = referential.getSharedStopAreas();
            if (sharedStopAreas != null && !sharedStopAreas.isEmpty()) {
                NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);
                //TimeZone timezone = TimeZone.getTimeZone(configuration.getTimeZone());
                //String prefix = configuration.getObjectIdPrefix(); // TODO derive prefix from producer instead

                //reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.STOP_AREA, stopAreas.size());

                NetexSharedDataProducer producer = new NetexSharedDataProducer();
                producer.produce(context);

                reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.STOP_AREA,
                        "stop areas", ActionReporter.OBJECT_STATE.OK, IO_TYPE.OUTPUT);
                reporter.setStatToObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.STOP_AREA,
                        ActionReporter.OBJECT_TYPE.STOP_AREA, referential.getSharedStopAreas().size());

                result = SUCCESS;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }

        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexSharedDataProducerCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexSharedDataProducerCommand.class.getName(), new DefaultCommandFactory());
    }

}
