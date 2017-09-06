package mobi.chouette.exchange.netexprofile.exporter;

import java.io.IOException;

import javax.naming.InitialContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.util.Referential;

@Log4j
public class NetexDisposeExportCommand implements Command, Constant {

    public static final String COMMAND = "NetexDisposeExportCommand";

    @Override
    public boolean execute(Context context) throws Exception {
        boolean result = ERROR;

        Monitor monitor = MonitorFactory.start(COMMAND);

        try {
            Referential referential = (Referential) context.get(REFERENTIAL);
            if (referential != null) {
                referential.dispose();
            }
            NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
            if (netexReferential != null) {
                netexReferential.dispose();
            }
            ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);
            if (exportableNetexData != null) {
                exportableNetexData.dispose();
            }

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
            return new NetexDisposeExportCommand();
        }
    }

    static {
        CommandFactory.factories.put(NetexDisposeExportCommand.class.getName(), new NetexDisposeExportCommand.DefaultCommandFactory());
    }

}
