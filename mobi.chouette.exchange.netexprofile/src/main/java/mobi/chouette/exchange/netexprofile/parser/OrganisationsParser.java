package mobi.chouette.exchange.netexprofile.parser;

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

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class OrganisationsParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		@SuppressWarnings("unchecked")
		List<PublicationDeliveryStructure> commonData = (List<PublicationDeliveryStructure>) context.get(NETEX_COMMON_DATA);
		PublicationDeliveryStructure lineData = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
		Referential referential = (Referential) context.get(REFERENTIAL);

		// This will be the "agreed" object to parse. Other objects are pure reference data in case needed
		OrganisationsInFrame_RelStructure contextData = (OrganisationsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		List<JAXBElement<? extends DataManagedObjectStructure>> organisations = contextData.getOrganisation_();
		for (JAXBElement<? extends DataManagedObjectStructure> organisation : organisations) {
			Organisation_VersionStructure organisationStructure = (Organisation_VersionStructure) organisation.getValue();
			parseOrganisation(referential, organisationStructure);
		}
	}

	private void parseOrganisation(Referential referential, Organisation_VersionStructure organisationStructure) {
		Company company = ObjectFactory.getCompany(referential, organisationStructure.getId());
		// @TODO: Find out what to do with this object
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
