/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validator;

import java.io.IOException;

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
import mobi.chouette.exchange.validation.SharedDataValidatorCommand;
import mobi.chouette.exchange.validation.ValidationData;

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

		try {
			if (!data.getLineIds().isEmpty()) {
				data.getLines().clear();
				data.getLines().addAll(lineDAO.findByObjectId(data.getLineIds()));
			}
			if (!data.getNetworkIds().isEmpty()) {
				data.getNetworks().clear();
				data.getNetworks().addAll(ptNetworkDAO.findByObjectId(data.getNetworkIds()));
			}
			if (!data.getCompanyIds().isEmpty()) {
				data.getCompanies().clear();
				data.getCompanies().addAll(companyDAO.findByObjectId(data.getCompanyIds()));
			}
			if (!data.getGroupOfLineIds().isEmpty()) {
				data.getGroupOfLines().clear();
				data.getGroupOfLines().addAll(groupOfLineDAO.findByObjectId(data.getGroupOfLineIds()));
			}
			if (!data.getStopAreaIds().isEmpty()) {
				data.getStopAreas().clear();
				data.getStopAreas().addAll(stopAreaDAO.findByObjectId(data.getStopAreaIds()));
			}
			if (!data.getAccessPointIds().isEmpty()) {
				data.getAccessPoints().clear();
				data.getAccessPoints().addAll(accessPointDAO.findByObjectId(data.getAccessPointIds()));
			}
			if (!data.getAccessLinkIds().isEmpty()) {
				data.getAccessLinks().clear();
				data.getAccessLinks().addAll(accessLinkDAO.findByObjectId(data.getAccessLinkIds()));
			}
			if (!data.getTimetableIds().isEmpty()) {
				data.getTimetables().clear();
				data.getTimetables().addAll(timetableDAO.findByObjectId(data.getTimetableIds()));
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
