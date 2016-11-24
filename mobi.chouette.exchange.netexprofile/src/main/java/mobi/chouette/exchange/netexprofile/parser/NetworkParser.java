package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.util.ObjectIdCreator;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.NetworkValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;

@Log4j
public class NetworkParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "NetworkContext";
    public static final String NETWORK_ID = "networkId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        NetworkValidator validator = (NetworkValidator) ValidatorFactory.create(NetworkValidator.class.getName(), context);
        org.rutebanken.netex.model.Network network = (org.rutebanken.netex.model.Network) context.get(NETEX_LINE_DATA_CONTEXT);

        String objectId = network.getId();

        // 1. initialize organisation reference
        JAXBElement<? extends OrganisationRefStructure> organisationRefStruct = network.getTransportOrganisationRef();
        if (organisationRefStruct != null) {
            String organisationId = organisationRefStruct.getValue().getRef();
            if (StringUtils.isNotEmpty(organisationId)) {
                validator.addOrganisationReference(context, objectId, organisationId);
            }
        }

        NetexObjectUtil.addNetworkReference(referential, objectId, network);
        validator.addObjectReference(context, network);
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        Collection<org.rutebanken.netex.model.Network> netexNetworks = netexReferential.getNetworks().values();
        for (org.rutebanken.netex.model.Network netexNetwork : netexNetworks) {

            String netexNetworkId = netexNetwork.getId();
            String chouetteNetworkId = ObjectIdCreator.createNetworkId(null, netexNetworkId);
            mobi.chouette.model.Network chouetteNetwork = ObjectFactory.getPTNetwork(chouetteReferential, chouetteNetworkId);
            addNetworkIdRef(context, netexNetworkId, chouetteNetworkId);

            chouetteNetwork.setSourceIdentifier("NeTEx");

            // mandatory
            String networkName = netexNetwork.getName().getValue();
            chouetteNetwork.setName(networkName);

            chouetteNetwork.setRegistrationNumber(netexNetworkId.split(":")[2]);

            // mandatory
            GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = netexNetwork.getGroupsOfLines();
            if (groupsOfLinesStruct != null) {
                List<GroupOfLines> groupsOfLines = groupsOfLinesStruct.getGroupOfLines();
                for (GroupOfLines groupOfLines : groupsOfLines) {
                    GroupOfLine groupOfLine = ObjectFactory.getGroupOfLine(chouetteReferential, groupOfLines.getId());
                    groupOfLine.setFilled(true);

                    // mandatory name
                    MultilingualString name = groupOfLines.getName();
                    if (name != null) {
                        String nameValue = name.getValue();
                        if (StringUtils.isNotEmpty(nameValue)) {
                            groupOfLine.setName(nameValue);
                        }
                    }

                    // mandatory members
                    LineRefs_RelStructure membersStruct = groupOfLines.getMembers();
                    if (membersStruct != null) {
                        List<JAXBElement<? extends LineRefStructure>> lineRefRelStructs = membersStruct.getLineRef();
                        for (JAXBElement<? extends LineRefStructure> lineRefRelStruct : lineRefRelStructs) {
                            LineRefStructure lineRefStruct = lineRefRelStruct.getValue();
                            String lineIdRef = lineRefStruct.getRef();
                            Line line = ObjectFactory.getLine(chouetteReferential, lineIdRef);
                            if (line != null) {
                                groupOfLine.addLine(line);
                            }
                        }
                    }
                }
            }

            chouetteNetwork.setFilled(true);
        }
    }

    private void addNetworkIdRef(Context context, String objectId, String networkId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(NETWORK_ID, networkId);
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
