package mobi.chouette.exchange.importer;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.AccessLinkDAO;
import mobi.chouette.dao.AccessPointDAO;
import mobi.chouette.dao.ConnectionLinkDAO;
import mobi.chouette.dao.StopAreaDAO;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;

@Log4j
@Stateless(name = CleanStopAreaRepositoryCommand.COMMAND)
public class CleanStopAreaRepositoryCommand implements Command {

    public static final String COMMAND = "CleanStopAreaRepositoryCommand";

    @EJB
    private StopAreaDAO stopAreaDAO;

    @EJB
    private AccessPointDAO accessPointDAO;

    @EJB
    private AccessLinkDAO accessLinkDAO;

    @EJB
    private ConnectionLinkDAO connectionLinkDAO;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean execute(Context context) throws Exception {

        boolean result = ERROR;
        Monitor monitor = MonitorFactory.start(COMMAND);

        try {
            accessLinkDAO.truncate();
            accessPointDAO.truncate();
            connectionLinkDAO.truncate();
            stopAreaDAO.truncate();
            result = SUCCESS;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
        log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
        return result;
    }

    public static class DefaultCommandFactory extends CommandFactory {

        @Override
        protected Command create(InitialContext context) throws IOException {
            Command result = null;
            try {
                String name = "java:app/mobi.chouette.exchange/" + COMMAND;
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
        CommandFactory.factories.put(CleanStopAreaRepositoryCommand.class.getName(), new DefaultCommandFactory());
    }
}
