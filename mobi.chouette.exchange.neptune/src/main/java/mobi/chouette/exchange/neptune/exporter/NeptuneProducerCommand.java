package mobi.chouette.exchange.neptune.exporter;

import java.io.IOException;
import java.sql.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.Line;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

@Log4j
@Stateless(name = NeptuneProducerCommand.COMMAND)
public class NeptuneProducerCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneProducerCommand";


	@EJB
	private LineDAO lineDAO;


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);

		try {

			Long lineId = (Long) context.get(LINE_ID);
			Line line = lineDAO.find(lineId);
			NeptuneExportParameters configuration = (NeptuneExportParameters) context
					.get(CONFIGURATION);

			ExportableData collection = new ExportableData();
			
			Date startDate = null;
			if(configuration.getStartDate() != null){
				startDate = new Date(configuration.getStartDate().getTime());
			}
			
			Date endDate = null;
			if(configuration.getEndDate() != null){
				endDate = new Date(configuration.getEndDate().getTime());
			}
			
			NeptuneDataCollector collector = new NeptuneDataCollector();
			collector.collect(collection, line, startDate, endDate);
			context.put(EXPORTABLE_DATA, collection);

			ChouettePTNetworkProducer producer = new ChouettePTNetworkProducer();
			producer.produce(context);

			result = SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
				String name = "java:app/mobi.chouette.exchange.neptune/"
						+ COMMAND;
				result = (Command) context.lookup(name);
			} catch (NamingException e) {
				log.error(e);
			}
			return result;
		}
	}

	static {
		CommandFactory.factories.put(NeptuneProducerCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
