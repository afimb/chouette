package mobi.chouette.exchange.importer.updater;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;

import javax.ejb.Stateless;

@Stateless(name = StopAreaLookup.BEAN_NAME)
@Log4j
public class StopAreaLookup implements Updater<StopArea> {

    public static final String BEAN_NAME = "StopAreaLookup";

    @Override
    public void update(Context context, StopArea oldValue, StopArea newValue) throws Exception {

        log.info("Update StopArea. OldValue: " + oldValue + ", newValue: " + newValue);

    }
}
