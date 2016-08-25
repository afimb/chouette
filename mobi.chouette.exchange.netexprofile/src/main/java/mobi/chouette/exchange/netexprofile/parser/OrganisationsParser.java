package mobi.chouette.exchange.netexprofile.parser;

import java.util.List;

import javax.xml.bind.JAXBElement;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import no.rutebanken.netex.model.Organisation_VersionStructure;
import no.rutebanken.netex.model.OrganisationsInFrame_RelStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

@Log4j
public class OrganisationsParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {

		PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_JAVA);
		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context
				.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_DATA);

		Referential referential = (Referential) context.get(REFERENTIAL);

		// This will be the "agreed" object to parse. Other objects are pure reference data in case needed
		OrganisationsInFrame_RelStructure contextData = (OrganisationsInFrame_RelStructure) context
				.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_LINE_DATA_CONTEXT);

		List<JAXBElement<? extends DataManagedObjectStructure>> organisation_ = contextData.getOrganisation_();
		for (JAXBElement<? extends DataManagedObjectStructure> org : organisation_) {
			Organisation_VersionStructure v = (Organisation_VersionStructure) org.getValue();
			parseOrganisation(context, lineData, commonData, referential, v);
		}

	}

	private void parseOrganisation(Context context, PublicationDeliveryStructure lineData, List<PublicationDeliveryStructure> commonData,
			Referential referential, Organisation_VersionStructure v) {
		// TODO

		Company c = ObjectFactory.getCompany(referential, v.getId());

	}

	static {
		ParserFactory.register(OrganisationsParser.class.getName(), new ParserFactory() {
			private OrganisationsParser instance = new OrganisationsParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
