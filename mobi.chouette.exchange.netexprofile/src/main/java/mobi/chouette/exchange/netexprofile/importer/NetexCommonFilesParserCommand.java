package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.parser.PublicationDeliveryParser;
import mobi.chouette.exchange.netexprofile.util.NetexIdExtractorHelper;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.naming.InitialContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j
public class NetexCommonFilesParserCommand implements Command, Constant {

    public static final String COMMAND = "NetexCommonFilesParserCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;

        Monitor monitor = MonitorFactory.start(COMMAND);
        String fileName = (String) context.get(FILE_NAME);
        ActionReporter actionReporter = ActionReporter.Factory.getInstance();
        context.put(NETEX_WITH_COMMON_DATA, Boolean.TRUE);

        Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_FILE_IDENTIFICATORS);

        try {

            Referential referential = (Referential) context.get(REFERENTIAL);
            if (referential != null) {
                referential.clear(true);
            }
            NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
            if (netexReferential != null) {
                netexReferential.clear();
            }

            Document dom = (Document) context.get(NETEX_DATA_DOM);

            XPath xpath = (XPath) context.get(NETEX_XPATH);

            // Find id-fields and to check for duplicates later
    		Set<IdVersion> localIds = NetexIdExtractorHelper.collectEntityIdentificators(context, xpath, dom, new HashSet<>(Arrays.asList("Codespace")));
            ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
    		for(IdVersion id : localIds) {
                data.getDataLocations().put(id.getId(), DataLocationHelper.findDataLocation(id));
                List<String> list = commonIds.get(id);
                if (list == null) {
                    list = new ArrayList<String>();
                    commonIds.put(id, list);
                }
                list.add(fileName);
    		}
    		
            PublicationDeliveryParser parser = (PublicationDeliveryParser) ParserFactory.create(PublicationDeliveryParser.class.getName());
            parser.parse(context);

            // report service
            actionReporter.setFileState(context, fileName, IO_TYPE.INPUT, ActionReporter.FILE_STATE.OK);

            result = SUCCESS;
        } catch (Exception e) {
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
