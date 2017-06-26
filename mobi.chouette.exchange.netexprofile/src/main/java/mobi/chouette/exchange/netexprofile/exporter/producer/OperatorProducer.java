package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes;
import mobi.chouette.model.Company;
import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.isSet;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;

public class OperatorProducer extends NetexProducer implements NetexEntityProducer<Operator, Company> {

    @Override
    public Operator produce(Context context, Company company) {
        Operator operator = netexFactory.createOperator();

        operator.setVersion(company.getObjectVersion() > 0 ? String.valueOf(company.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION);

        String operatorId = netexId(company.objectIdPrefix(), NetexObjectIdTypes.OPERATOR, company.objectIdSuffix());
        operator.setId(operatorId);

        if (isSet(company.getCode())) {
            operator.setPublicCode(company.getCode());
        }

        if (isSet(company.getRegistrationNumber())) {
            operator.setCompanyNumber(company.getRegistrationNumber());
        }

        if (isSet(company.getName())) {
            operator.setName(getMultilingualString(company.getName()));
        }

        if (isSet(company.getLegalName())) {
            operator.setLegalName(getMultilingualString(company.getLegalName()));
        }

        if (isSet(company.getShortName())) {
            operator.setShortName(getMultilingualString(company.getShortName()));
        }

        if (isSet(company.getPhone(), company.getUrl())) {
            ContactStructure contactStructure = netexFactory.createContactStructure();
            if (isSet(company.getPhone())) {
                contactStructure.setPhone(company.getPhone());
            }
            if (isSet(company.getUrl())) {
                contactStructure.setUrl(company.getUrl());
            }
            operator.setContactDetails(contactStructure);
        }
        if (isSet(company.getPublicPhone(), company.getPublicEmail(), company.getPublicUrl())) {
            ContactStructure contactStructure = netexFactory.createContactStructure();
            if (isSet(company.getPublicPhone())) {
                contactStructure.setPhone(company.getPublicPhone());
            }
            if (isSet(company.getPublicEmail())) {
                contactStructure.setPhone(company.getPublicEmail());
            }
            if (isSet(company.getPublicUrl())) {
                contactStructure.setUrl(company.getPublicUrl());
            }
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
