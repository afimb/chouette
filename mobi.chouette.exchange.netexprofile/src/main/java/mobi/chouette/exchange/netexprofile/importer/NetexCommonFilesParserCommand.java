package mobi.chouette.exchange.netexprofile.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

@Log4j
public class NetexCommonFilesParserCommand implements Command, Constant {

	public static final String COMMAND = "NetexCommonFilesParserCommand";

	@Getter
	@Setter
	private List<Path> files;

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		Monitor monitor = MonitorFactory.start(COMMAND);

		// report service
		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		Map<IdVersion, List<String>> commonIds = new HashMap<>();
		context.put(NETEX_COMMON_FILE_IDENTIFICATORS, commonIds);

		for(Path p : files) {
			File file = p.toFile();
			String fileName = file.getName();
			context.put(FILE_NAME, fileName);
			try {

				log.info("parsing file : " + file.getAbsolutePath());

				// Fetch other common data, append to this

				Referential referential = (Referential) context.get(REFERENTIAL);
				if (referential != null) {
					referential.clear(true);
				}

				NetexImporter importer = (NetexImporter) context.get(IMPORTER);
				Document dom = importer.parseFileToDom(file);

				// Find id-fields and check for duplicates
				collectEntityIdentificators(context, fileName, dom, commonIds);
				parseToJava(context, importer, dom);

				// report service
				// TODO if has duplicates  - do not report as OK
				actionReporter.setFileState(context, fileName, IO_TYPE.INPUT, ActionReporter.FILE_STATE.OK);

				result = SUCCESS;
			} catch (Exception e) {
				// report service
				log.error("parsing failed ", e);
				actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, e.toString());
			} finally {
				log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
			}
		}
		
		for(IdVersion id : commonIds.keySet()) {
			List<String> filenameList = commonIds.get(id);
			if(filenameList.size() > 1) {
				for(String fileName : filenameList) {
					// TODO better error code
					actionReporter.addFileErrorInReport(context, fileName, ActionReporter.FILE_ERROR_CODE.INTERNAL_ERROR, "Duplicate id "+id);
				}
				result = ERROR;
			}
		}
		
		return result;
	}

	protected void parseToJava(Context context, NetexImporter importer, Document dom) throws JAXBException {
		PublicationDeliveryStructure commonDeliveryStructure = importer.unmarshal(dom);

		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonDeliveries = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
		if (commonDeliveries == null) {
			commonDeliveries = new ArrayList<>();
			context.put(NETEX_COMMON_DATA, commonDeliveries);
		}

		commonDeliveries.add(commonDeliveryStructure);
	}

	protected void collectEntityIdentificators(Context context, String fileName, Document parseFileToDom, Map<IdVersion, List<String>> commonIds)
			throws XPathExpressionException {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);

		XPath xpath = (XPath) context.get(NETEX_LINE_DATA_XPATH);
		NodeList nodes = (NodeList) xpath.evaluate("//n:*[not(name()='Codespace') and @id]", parseFileToDom, XPathConstants.NODESET);
		int idCount = nodes.getLength();
		for (int i = 0; i < idCount; i++) {
			
			Node n = nodes.item(i);
			String elementName = n.getNodeName();
			String id = n.getAttributes().getNamedItem("id").getNodeValue();
			String version = null;
			Node versionAttribute = n.getAttributes().getNamedItem("version");
			if(versionAttribute != null) {
				version = versionAttribute.getNodeValue();
			}
			IdVersion idVersion = new IdVersion(id, version,elementName,fileName,(Integer)n.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME),(Integer)n.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME));

			data.getDataLocations().put(idVersion.getId(), DataLocationHelper.findDataLocation(idVersion));

			List<String> list = commonIds.get(idVersion);
			if(list == null) {
				list = new ArrayList<String>();
				commonIds.put(idVersion,list);
			}
			list.add(fileName);
		}
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
