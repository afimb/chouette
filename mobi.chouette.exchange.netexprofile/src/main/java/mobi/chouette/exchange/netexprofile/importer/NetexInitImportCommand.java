package mobi.chouette.exchange.netexprofile.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.util.ProfileValidatorCodespace;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayCommonNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayLineNetexProfileValidator;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.naming.InitialContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j
public class NetexInitImportCommand implements Command, Constant {

	public static final String COMMAND = "NetexInitImportCommand";

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			log.info("Context on NetexInitImportCommand=" + ToStringBuilder.reflectionToString(context));

			NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);

			NetexImporter importer = new NetexImporter();
			context.put(IMPORTER, importer);

			XPath xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(new NetexNamespaceContext());
			context.put(NETEX_XPATH, xpath);

			context.put(REFERENTIAL, new Referential());
			context.put(NETEX_REFERENTIAL, new NetexReferential());
			context.put(VALIDATION_DATA, new ValidationData());

			Map<String, NetexProfileValidator> availableProfileValidators = new HashMap<>();

			// Register profiles for Norway
			registerProfileValidator(availableProfileValidators, new NorwayLineNetexProfileValidator());
			registerProfileValidator(availableProfileValidators, new NorwayCommonNetexProfileValidator());

			context.put(NETEX_PROFILE_VALIDATORS, availableProfileValidators);
			
			// Decode codespace definition if provided
			if (configuration.getValidCodespaces() != null) {
				Set<ProfileValidatorCodespace> validCodespaces = new HashSet<>();
				String[] validCodespacesTuples = StringUtils.split(configuration.getValidCodespaces(), ",");
				for (int i = 0; i < validCodespacesTuples.length; i += 2) {
					validCodespaces.add(new ProfileValidatorCodespace(validCodespacesTuples[i], validCodespacesTuples[i + 1]));
				}
				if (validCodespaces.size() > 0) {
					context.put(NETEX_VALID_CODESPACES, validCodespaces);
				}
			}

			ActionReporter reporter = ActionReporter.Factory.getInstance();
			reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.NETWORK, "networks", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.STOP_AREA, "stop areas", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.COMPANY, "companies", ActionReporter.OBJECT_STATE.OK, IO_TYPE.INPUT);
			reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.CONNECTION_LINK, "connection links", ActionReporter.OBJECT_STATE.OK,
					IO_TYPE.INPUT);
			reporter.addObjectReport(context, "merged", ActionReporter.OBJECT_TYPE.ACCESS_POINT, "access points", ActionReporter.OBJECT_STATE.OK,
					IO_TYPE.INPUT);
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

	private void registerProfileValidator(Map<String, NetexProfileValidator> availableProfileValidators, NetexProfileValidator profileValidator) {
	for (String supportedProfile : profileValidator.getSupportedProfiles()) {
		availableProfileValidators.put(supportedProfile+ (profileValidator.isCommonFileValidator() ? "-common": ""), profileValidator);
	}

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
