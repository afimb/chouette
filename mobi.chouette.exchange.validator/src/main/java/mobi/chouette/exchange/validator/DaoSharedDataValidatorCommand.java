/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package mobi.chouette.exchange.validator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import mobi.chouette.model.Line;

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
		if (!context.containsKey(SOURCE))
		{
			context.put(SOURCE, SOURCE_DATABASE);
		}

		try {
			if (!data.getLineIds().isEmpty()) {
				data.getLines().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getLineIds());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getLines().addAll(lineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getNetworkIds().isEmpty()) {
				data.getNetworks().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getNetworks());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getNetworks().addAll(ptNetworkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getCompanyIds().isEmpty()) {
				data.getCompanies().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getCompanies());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getCompanies().addAll(companyDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getGroupOfLineIds().isEmpty()) {
				data.getGroupOfLines().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getGroupOfLines());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getGroupOfLines().addAll(groupOfLineDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getStopAreaIds().isEmpty()) {
				data.getStopAreas().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getStopAreaIds());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getStopAreas().addAll(stopAreaDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getAccessPointIds().isEmpty()) {
				data.getAccessPoints().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getAccessPointIds());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getAccessPoints().addAll(accessPointDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getAccessLinkIds().isEmpty()) {
				data.getAccessLinks().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getAccessLinkIds());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getAccessLinks().addAll(accessLinkDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
			}
			if (!data.getTimetableIds().isEmpty()) {
				data.getTimetables().clear();
				Map<String,List<String>> objectIdsByCodeSpace = UpdaterUtils.getObjectIdsByCodeSpace(data.getTimetableIds());
				
				for (Entry<String, List<String>> entry : objectIdsByCodeSpace.entrySet())
				{
					data.getTimetables().addAll(timetableDAO.findByChouetteId(entry.getKey(), entry.getValue()));
				}
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
