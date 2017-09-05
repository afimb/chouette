package mobi.chouette.exchange.netexprofile.exporter.producer;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;

import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

import mobi.chouette.common.Context;
import mobi.chouette.model.Company;

public class OperatorProducer extends NetexProducer implements NetexEntityProducer<Operator, Company> {

	@Override
	public Operator produce(Context context, Company company) {
		Operator operator = netexFactory.createOperator();

		NetexProducerUtils.populateId(company, operator);

		operator.setPublicCode(company.getCode());
		operator.setCompanyNumber(company.getRegistrationNumber());
		operator.setName(getMultilingualString(company.getName()));
		operator.setLegalName(getMultilingualString(company.getLegalName()));
		operator.setShortName(getMultilingualString(company.getShortName()));

		if (isSet(company.getPhone(), company.getUrl())) {
			ContactStructure contactStructure = netexFactory.createContactStructure();
			contactStructure.setPhone(company.getPhone());
			contactStructure.setUrl(company.getUrl());
			operator.setContactDetails(contactStructure);
		}
		if (isSet(company.getPublicPhone(), company.getPublicEmail(), company.getPublicUrl())) {
			ContactStructure contactStructure = netexFactory.createContactStructure();
			contactStructure.setPhone(company.getPublicPhone());
			contactStructure.setPhone(company.getPublicEmail());
			contactStructure.setUrl(company.getPublicUrl());
			operator.setCustomerServiceContactDetails(contactStructure);
		}

		if (isSet(company.getOrganisationType())) {
			OrganisationTypeEnumeration organisationTypeEnumeration = NetexProducerUtils.getOrganisationTypeEnumeration(company.getOrganisationType());
			operator.getOrganisationType().add(organisationTypeEnumeration);
		} else {
			operator.getOrganisationType().add(OrganisationTypeEnumeration.OPERATOR);
		}

		return operator;
	}
}
