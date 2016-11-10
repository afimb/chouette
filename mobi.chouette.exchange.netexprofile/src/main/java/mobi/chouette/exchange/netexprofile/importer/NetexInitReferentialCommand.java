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
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.report.ActionReporter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;

import javax.naming.InitialContext;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j
public class NetexInitReferentialCommand implements Command, Constant {

    public static final String COMMAND = "NetexInitReferentialCommand";

    @Getter
    @Setter
    private File file;

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);

        // action report needed for this command?
        //ActionReporter actionReporter = ActionReporter.Factory.getInstance();

        try {
            log.info("Initializing referentials for file : ");

            NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

            if (referential == null) {
                referential = new NetexReferential();
                context.put(NETEX_REFERENTIAL, referential);
            } else {
                referential.clear();
            }

            // start of init, check out where Referential is set for the first time...

            //context.put(NETEX_REFERENTIAL, new NetexReferential());

            NetexImporter importer = (NetexImporter) context.get(IMPORTER);
            Document dom = importer.parseFileToDom(file);
            PublicationDeliveryStructure lineDeliveryStructure = importer.unmarshal(dom);

            context.put(NETEX_LINE_DATA_JAVA, lineDeliveryStructure);
            context.put(NETEX_LINE_DATA_DOM, dom);

            // end of init

            PublicationDeliveryParser parser = (PublicationDeliveryParser) ParserFactory.create(PublicationDeliveryParser.class.getName());
            parser.initReferentials(context);

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
            Command result = new NetexInitReferentialCommand();
            return result;
        }
    }

    static {
        CommandFactory.factories.put(NetexInitReferentialCommand.class.getName(),
                new NetexInitReferentialCommand.DefaultCommandFactory());
    }

}
