package mobi.chouette.exchange.importer.updater;

import java.util.Date;

import javax.ejb.Stateless;

import mobi.chouette.common.Context;
import mobi.chouette.model.Footnote;

@Stateless(name = FootnoteUpdater.BEAN_NAME)
public class FootnoteUpdater implements Updater<Footnote> {

	public static final String BEAN_NAME = "FootnoteUpdater";

	@Override
	public void update(Context context, Footnote oldValue, Footnote newValue) {

		if (newValue.getCode() != null
				&& !newValue.getCode().equals(oldValue.getCode())) {
			oldValue.setCode(newValue.getCode());
		}

		if (newValue.getKey() != null
				&& !newValue.getKey().equals(oldValue.getKey())) {
			oldValue.setKey(newValue.getKey());
		}

		if (newValue.getLabel() != null
				&& !newValue.getLabel().equals(oldValue.getLabel())) {
			oldValue.setLabel(newValue.getLabel());
		}

		// Updated now anyhow
		oldValue.setUpdatedAt(new Date());
		
	}

}
