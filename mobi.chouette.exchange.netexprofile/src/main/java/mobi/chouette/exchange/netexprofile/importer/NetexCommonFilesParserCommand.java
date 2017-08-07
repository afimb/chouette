package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.util.Referential;

@Log4j
public class NetexCommonFilesParserCommand implements Command, Constant {

    public static final String COMMAND = "NetexCommonFilesParserCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;

        Monitor monitor = MonitorFactory.start(COMMAND);
        ActionReporter actionReporter = ActionReporter.Factory.getInstance();
        context.put(NETEX_WITH_COMMON_DATA, Boolean.TRUE);

        String fileName = (String) context.get(FILE_NAME);

        try {

            Referential referential = (Referential) context.get(REFERENTIAL);
            if (referential != null) {
                referential.clear(true);
            }
            NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
            if (netexReferential != null) {
                netexReferential.clear();
            }

            PublicationDeliveryParser parser = (PublicationDeliveryParser) ParserFactory.create(PublicationDeliveryParser.class.getName());
            parser.parse(context);

            // report service
            actionReporter.setFileState(context, fileName, IO_TYPE.INPUT, ActionReporter.FILE_STATE.OK);

            result = SUCCESS;
        } catch (Exception e) {
        	log.error("Error parsing common file",e);
            actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }

        return result;
    }
    
  

    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexCommonFilesParserCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexCommonFilesParserCommand.class.getName(), new DefaultCommandFactory());
    }

}
