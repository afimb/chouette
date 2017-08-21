package mobi.chouette.exchange.netexprofile.importer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CodespaceDAO;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayCommonNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NorwayLineNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.Codespace;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = NetexInitImportCommand.COMMAND)
public class NetexInitImportCommand implements Command, Constant {

	public static final String COMMAND = "NetexInitImportCommand";

	@EJB
	private CodespaceDAO codespaceDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {
			NetexprofileImportParameters parameters = (NetexprofileImportParameters) context.get(CONFIGURATION);

			
			NetexImporter importer = new NetexImporter();
			context.put(IMPORTER, importer);
			context.put(NETEX_XPATH_COMPILER, importer.getXPathCompiler());
			context.put(REFERENTIAL, new Referential());
			context.put(NETEX_REFERENTIAL, new NetexReferential());
			context.put(VALIDATION_DATA, new ValidationData());

			Map<String, NetexProfileValidator> availableProfileValidators = new HashMap<>();

			// Register profiles for Norway

			NetexProfileValidator norwayLineValidator = NetexProfileValidatorFactory.create(NorwayLineNetexProfileValidator.class.getName(), context);
			NetexProfileValidator norwayCommonFileValidator = NetexProfileValidatorFactory.create(NorwayCommonNetexProfileValidator.class.getName(), context);

			registerProfileValidator(availableProfileValidators, norwayLineValidator);
			registerProfileValidator(availableProfileValidators, norwayCommonFileValidator);

			context.put(NETEX_PROFILE_VALIDATORS, availableProfileValidators);

			List<Codespace> referentialCodespaces = codespaceDAO.findAll();
			if (referentialCodespaces.isEmpty()) {
				log.error("No valid codespaces present for referential "+parameters.getReferentialName());
			}

			Set<Codespace> validCodespaces = new HashSet<>(referentialCodespaces);
			context.put(NETEX_VALID_CODESPACES, validCodespaces);

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
			availableProfileValidators.put(supportedProfile + (profileValidator.isCommonFileValidator() ? "-common" : ""), profileValidator);
		}

	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.netexprofile/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				String name = "java:module/" + COMMAND;
				try {
					result = (Command) context.lookup(name);
				} catch (NamingException e1) {
					log.error(e);
				}
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NetexInitImportCommand.class.getName(), new NetexInitImportCommand.DefaultCommandFactory());
	}

}
