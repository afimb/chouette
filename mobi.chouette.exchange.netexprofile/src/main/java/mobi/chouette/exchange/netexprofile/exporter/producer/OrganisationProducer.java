package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.rutebanken.netex.model.Organisation_VersionStructure;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.model.Company;
import mobi.chouette.model.type.OrganisationTypeEnum;

public class OrganisationProducer extends NetexProducer implements NetexEntityProducer<Organisation_VersionStructure, Company> {

	@Override
	public Organisation_VersionStructure produce(Context context, Company company) {
		
		Organisation_VersionStructure organisation = null;
		
		if(OrganisationTypeEnum.Operator.equals(company.getOrganisationType())) {
			Operator operator = netexFactory.createOperator();

			if (isSet(company.getPublicPhone(), company.getPublicEmail(), company.getPublicUrl())) {
				ContactStructure contactStructure = netexFactory.createContactStructure();
				contactStructure.setPhone(company.getPublicPhone());
				contactStructure.setPhone(company.getPublicEmail());
				contactStructure.setUrl(company.getPublicUrl());
				operator.setCustomerServiceContactDetails(contactStructure);
			}
			organisation = operator;
		} else if(OrganisationTypeEnum.Authority.equals(company.getOrganisationType())) {
			organisation = netexFactory.createAuthority();
		} else {
			organisation = netexFactory.createGeneralOrganisation();
		}

		NetexProducerUtils.populateId(company, organisation);

		organisation.setPublicCode(company.getCode());
		organisation.setCompanyNumber(company.getRegistrationNumber());
		organisation.setName(ConversionUtil.getMultiLingualString(company.getName()));
		organisation.setLegalName(ConversionUtil.getMultiLingualString(company.getLegalName()));
		organisation.setShortName(ConversionUtil.getMultiLingualString(company.getShortName()));

		if (isSet(company.getPhone(), company.getUrl())) {
			ContactStructure contactStructure = netexFactory.createContactStructure();
			contactStructure.setPhone(company.getPhone());
			contactStructure.setUrl(company.getUrl());
			organisation.setContactDetails(contactStructure);
		}

		if (isSet(company.getOrganisationType())) {
			OrganisationTypeEnumeration organisationTypeEnumeration = NetexProducerUtils.getOrganisationTypeEnumeration(company.getOrganisationType());
			organisation.getOrganisationType().add(organisationTypeEnumeration);
		} else {
			organisation.getOrganisationType().add(OrganisationTypeEnumeration.OTHER);
		}

		return organisation;
	}
}
