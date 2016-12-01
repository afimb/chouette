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
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;

import javax.naming.InitialContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;

@Log4j
public class NetexInitReferentialCommand implements Command, Constant {

    public static final String COMMAND = "NetexInitReferentialCommand";

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
            log.info("Initializing referentials for file : " + url);

            context.put(NETEX_REFERENTIAL, new NetexReferential());
            NetexImporter importer = (NetexImporter) context.get(IMPORTER);
            Document dom = importer.parseFileToDom(file);
            PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(dom);
            context.put(NETEX_LINE_DATA_JAVA, lineDeliveryStructure);
            context.put(NETEX_LINE_DATA_DOM, dom);

            result = SUCCESS;
        } catch (Exception e) {
            reporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
            log.error("Netex referential initialization failed ", e);
            throw e;
        } finally {
            log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        }
        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {
        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = new NetexInitReferentialCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexInitReferentialCommand.class.getName(),
                new NetexInitReferentialCommand.DefaultCommandFactory());
    }

}
