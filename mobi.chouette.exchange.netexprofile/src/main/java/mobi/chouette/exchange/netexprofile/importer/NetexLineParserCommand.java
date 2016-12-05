package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.NamingUtil;
import mobi.chouette.model.util.Referential;

import javax.naming.InitialContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static mobi.chouette.exchange.report.ActionReporter.FILE_ERROR_CODE;

@Log4j
public class NetexLineParserCommand implements Command, Constant {

    public static final String COMMAND = "NetexLineParserCommand";

    @Getter
    @Setter
    private String fileURL;

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);
        context.put(FILE_URL, fileURL);

        ActionReporter reporter = ActionReporter.Factory.getInstance();
        File file = new File(new URL(fileURL).toURI());
        String fileName = file.getName();
        reporter.addFileReport(context, fileName, IO_TYPE.INPUT);
        context.put(FILE_NAME, fileName);

        try {
            URL url = new URL(fileURL);
            log.info("Parsing file : " + url);

            Referential referential = (Referential) context.get(REFERENTIAL);
            if (referential != null) {
                referential.clear(true);
            }

            PublicationDeliveryParser parser = (PublicationDeliveryParser) ParserFactory.create(PublicationDeliveryParser.class.getName());
            parser.initReferentials(context);
            parser.parse(context);

            Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
            addStats(context, reporter, validationContext, referential);
            result = SUCCESS;
        } catch (Exception e) {
            reporter.addFileErrorInReport(context, fileName, FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
            log.error("Parsing failed ", e);
            throw e;
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }

        return result;
    }

    private void addStats(Context context, ActionReporter reporter, Context validationContext, Referential referential) {
        Line line = referential.getLines().values().iterator().next();
        reporter.addObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, NamingUtil.getName(line), ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
        reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.LINE, 1);

/*
        {
            Context localContext = (Context) validationContext.get(ChouetteRouteValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.ROUTE, count);
        }
        {
            Context localContext = (Context) validationContext.get(ConnectionLinkValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.CONNECTION_LINK,
                    count);
        }
        {
            Context localContext = (Context) validationContext.get(TimetableValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.TIMETABLE, count);
        }
        {
            Context localContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.STOP_AREA, count);
        }
        {
            Context localContext = (Context) validationContext.get(AccessPointValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.ACCESS_POINT,
                    count);
        }
        {
            Context localContext = (Context) validationContext.get(VehicleJourneyValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.VEHICLE_JOURNEY,
                    count);
        }
        {
            Context localContext = (Context) validationContext.get(JourneyPatternValidator.LOCAL_CONTEXT);
            int count = (localContext != null) ? localContext.size() : 0;
            reporter.setStatToObjectReport(context, line.getObjectId(), ActionReporter.OBJECT_TYPE.LINE, ActionReporter.OBJECT_TYPE.JOURNEY_PATTERN,
                    count);
        }
*/
    }

    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexLineParserCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexLineParserCommand.class.getName(),
                new DefaultCommandFactory());
    }
}
