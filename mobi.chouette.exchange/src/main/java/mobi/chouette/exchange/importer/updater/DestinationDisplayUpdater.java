package mobi.chouette.exchange.importer.updater;

import java.util.Date;

import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.DestinationDisplay;

@Stateless(name = DestinationDisplayUpdater.BEAN_NAME)
@Log4j
public class DestinationDisplayUpdater implements Updater<DestinationDisplay> {

	public static final String BEAN_NAME = "DestinationDisplayUpdater";

	@Override
	public void update(Context context, DestinationDisplay oldValue, DestinationDisplay newValue) {

		if (log.isDebugEnabled()) {
			log.debug("Updating " + oldValue + " with " + newValue);

		}
		if (newValue.getName() != null
				&& !newValue.getName().equals(oldValue.getName())) {
			oldValue.setName(newValue.getName());
		}

		if (newValue.getFrontText() != null
				&& !newValue.getFrontText().equals(oldValue.getFrontText())) {
			oldValue.setFrontText(newValue.getFrontText());
		}

		if (newValue.getSideText() != null
				&& !newValue.getSideText().equals(oldValue.getSideText())) {
			oldValue.setSideText(newValue.getSideText());
		}

		// Handle vias
		// TODO figure out how to recursively update destination displays
		oldValue.getVias().clear();
		oldValue.getVias().addAll(newValue.getVias());
		
		// Updated now anyhow
		oldValue.setUpdatedAt(new Date());

	}

}
