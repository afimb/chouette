package mobi.chouette.exchange.netexprofile.exporter;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.joda.time.LocalDateTime;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.dao.CodespaceDAO;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.Codespace;
import mobi.chouette.model.util.Referential;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j
@Stateless(name = NetexInitExportCommand.COMMAND)
public class NetexInitExportCommand implements Command, Constant {

    public static final String COMMAND = "NetexInitExportCommand";

    @Resource
    private SessionContext daoContext;

    @EJB
    private CodespaceDAO codespaceDAO;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;

        Monitor monitor = MonitorFactory.start(COMMAND);

        try {
            JobData jobData = (JobData) context.get(JOB_DATA);
            jobData.setOutputFilename("export_" + jobData.getType() + "_" + jobData.getId() + ".zip");
            context.put(REFERENTIAL, new Referential());
            context.put(NETEX_REFERENTIAL, new NetexReferential());

            Metadata metadata = new Metadata();
            metadata.setDate(LocalDateTime.now());
            metadata.setFormat("application/xml");
            metadata.setTitle("Export NeTEx ");

            try {
                metadata.setRelation(new URL("http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/netex/"));
            } catch (MalformedURLException e1) {
                log.error("problem with http://www.normes-donnees-tc.org/format-dechange/donnees-theoriques/netex/ url", e1);
            }

            context.put(METADATA, metadata);

            Path path = Paths.get(jobData.getPathName(), OUTPUT);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            List<Codespace> referentialCodespaces = codespaceDAO.findAll();
            if (referentialCodespaces.isEmpty()) {
                log.error("no valid codespaces present for referential");
                return ERROR;
            }

            Set<Codespace> validCodespaces = new HashSet<>(referentialCodespaces);
            context.put(NETEX_VALID_CODESPACES, validCodespaces);

            daoContext.setRollbackOnly();
            codespaceDAO.clear();

            result = SUCCESS;
        } catch (Exception e) {
            log.error(e, e);
            throw e;
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
        CommandFactory.factories.put(NetexInitExportCommand.class.getName(), new NetexInitExportCommand.DefaultCommandFactory());
    }

}
