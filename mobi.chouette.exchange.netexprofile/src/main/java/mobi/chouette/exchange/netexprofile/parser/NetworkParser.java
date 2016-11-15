package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NetworkValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.Network;
import org.rutebanken.netex.model.OrganisationRefStructure;

import javax.xml.bind.JAXBElement;

@Log4j
public class NetworkParser implements NetexParser {

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        NetworkValidator validator = (NetworkValidator) ValidatorFactory.create(NetworkValidator.class.getName(), context);
        Network network = (Network) context.get(NETEX_LINE_DATA_CONTEXT);

        String objectId = network.getId();

        // 1. initialize organisation reference
        JAXBElement<? extends OrganisationRefStructure> organisationRefStruct = network.getTransportOrganisationRef();
        if (organisationRefStruct != null) {
            String organisationId = organisationRefStruct.getValue().getRef();
            if (StringUtils.isNotEmpty(organisationId)) {
                validator.addOrganisationReference(context, objectId, organisationId);
            }
        }

        validator.addObjectReference(context, network);
    }

    @Override
    public void parse(Context context) throws Exception {

    }

    static {
        ParserFactory.register(NetworkParser.class.getName(), new ParserFactory() {
            private NetworkParser instance = new NetworkParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
