package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Branding;

public class BrandingProducer extends NetexProducer {

	public void addBranding(ExportableNetexData exportableNetexData, Branding branding) {

		org.rutebanken.netex.model.Branding netexBranding = netexFactory.createBranding();
		NetexProducerUtils.populateId(branding, netexBranding);

		if (!exportableNetexData.getSharedNotices().containsKey(branding.getId())) {
			netexBranding.setName(ConversionUtil.getMultiLingualString(branding.getName()));
			netexBranding.setDescription(ConversionUtil.getMultiLingualString(branding.getDescription()));
			netexBranding.setUrl(branding.getUrl());
			netexBranding.setImage(branding.getImage());

			exportableNetexData.getSharedBrandings().put(netexBranding.getId(), netexBranding);
		}

	}
}
