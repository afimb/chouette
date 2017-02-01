package mobi.chouette.exchange.netexprofile.exporter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.exchange.neptune.exporter.NeptuneLineProducerCommand;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Line;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

@Log4j
@Stateless(name = DaoNetexLineProducerCommand.COMMAND)
public class DaoNetexLineProducerCommand implements Command, Constant {

    public static final String COMMAND = "DaoNetexLineProducerCommand";

    @Resource
    private SessionContext daoContext;

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

            InitialContext initialContext = (InitialContext) context.get(INITIAL_CONTEXT);
            Command export = CommandFactory.create(initialContext, NeptuneLineProducerCommand.class.getName());

            context.put(LINE, line);
            result = export.execute(context);
            daoContext.setRollbackOnly();
            lineDAO.clear();

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
                String name = "java:app/mobi.chouette.exchange.netexprofile/" + COMMAND;
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
        CommandFactory.factories.put(DaoNetexLineProducerCommand.class.getName(), new DaoNetexLineProducerCommand.DefaultCommandFactory());
    }

}
