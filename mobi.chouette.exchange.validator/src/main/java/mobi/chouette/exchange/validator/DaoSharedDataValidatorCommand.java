/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.dao.StopAreaDAO;
import mobi.chouette.dao.TimetableDAO;
import mobi.chouette.exchange.importer.updater.UpdaterUtils;
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.Timetable;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 *
 */
@Log4j
@Stateless(name = DaoSharedDataValidatorCommand.COMMAND)
public class DaoSharedDataValidatorCommand implements Command, Constant {
	public static final String COMMAND = "DaoSharedDataValidatorCommand";

	@Resource
	private SessionContext daoContext;

	@EJB
	private LineDAO lineDAO;

	@EJB
	private NetworkDAO ptNetworkDAO;

	@EJB
	private CompanyDAO companyDAO;

	@EJB
	private GroupOfLineDAO groupOfLineDAO;

	@EJB
	private StopAreaDAO stopAreaDAO;

	@EJB
	private TimetableDAO timetableDAO;

	@EJB
	private ConnectionLinkDAO connectionLinkDAO;

	@EJB
	private AccessLinkDAO accessLinkDAO;

	@EJB
	private AccessPointDAO accessPointDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
		if (!context.containsKey(SOURCE)) {
			context.put(SOURCE, SOURCE_DATABASE);
		}

		try {
			if (!data.getLineIds().isEmpty()) {
				data.getLines().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getLineIds());
				data.getLines().addAll((List<Line>) lineDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getNetworkIds().isEmpty()) {
				data.getNetworks().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getNetworkIds());
				data.getNetworks().addAll((List<Network>) ptNetworkDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getCompanyIds().isEmpty()) {
				data.getCompanies().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getCompanyIds());
				data.getCompanies().addAll((List<Company>) companyDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getGroupOfLineIds().isEmpty()) {
				data.getGroupOfLines().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getGroupOfLineIds());
				data.getGroupOfLines().addAll(
						(List<GroupOfLine>) groupOfLineDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getStopAreaIds().isEmpty()) {
				data.getStopAreas().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getStopAreaIds());
				data.getStopAreas().addAll((List<StopArea>) stopAreaDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getAccessPointIds().isEmpty()) {
				data.getAccessPoints().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getAccessPointIds());
				data.getAccessPoints().addAll(
						(List<AccessPoint>) accessPointDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getAccessLinkIds().isEmpty()) {
				data.getAccessLinks().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getAccessLinkIds());
				data.getAccessLinks().addAll((List<AccessLink>) accessLinkDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}
			if (!data.getTimetableIds().isEmpty()) {
				data.getTimetables().clear();
				Map<String, List<String>> chouetteIdsByCodeSpace = UpdaterUtils.dispatchChouetteIdsByCodeSpace(data
						.getTimetableIds());
				data.getTimetables().addAll((List<Timetable>) timetableDAO.findByChouetteId(chouetteIdsByCodeSpace));

			}

			Command validateSharedData = CommandFactory.create(initialContext,
					SharedDataValidatorCommand.class.getName());
			result = validateSharedData.execute(context);
			// daoContext.setRollbackOnly();
		} finally {
			log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}

		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = null;
			try {
				String name = "java:app/mobi.chouette.exchange.validator/" + COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				// try another way on test context
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
		CommandFactory.factories.put(DaoSharedDataValidatorCommand.class.getName(), new DefaultCommandFactory());
	}

}
