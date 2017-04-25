package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.LineRefStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class NetworkParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "Network";
    static final String NETWORK_ID = "networkId";

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        org.rutebanken.netex.model.Network netexNetwork = (org.rutebanken.netex.model.Network) context.get(NETEX_LINE_DATA_CONTEXT);

        mobi.chouette.model.Network chouetteNetwork = ObjectFactory.getPTNetwork(referential, netexNetwork.getId());
        chouetteNetwork.setObjectVersion(NetexParserUtils.getVersion(netexNetwork));

        if (netexNetwork.getCreated() != null) {
            chouetteNetwork.setCreationTime(NetexParserUtils.convertToDate(netexNetwork.getCreated()));
        }
        if (netexNetwork.getChanged() != null) {
            chouetteNetwork.setVersionDate(NetexParserUtils.convertToDate(netexNetwork.getChanged()));
        }
        if (netexNetwork.getName() != null) {
            chouetteNetwork.setName(netexNetwork.getName().getValue());
        }
        if (netexNetwork.getDescription() != null) {
            chouetteNetwork.setDescription(netexNetwork.getDescription().getValue());
        }
        if (netexNetwork.getPrivateCode() != null) {
            chouetteNetwork.setRegistrationNumber(netexNetwork.getPrivateCode().getValue());
        }
        if (netexNetwork.getMainLineRef() != null) {
            Line line = ObjectFactory.getLine(referential, netexNetwork.getMainLineRef().getRef());
            chouetteNetwork.getLines().add(line);
        }

        if (netexNetwork.getGroupsOfLines() != null) {
            List<GroupOfLines> groupsOfLines = netexNetwork.getGroupsOfLines().getGroupOfLines();

            for (GroupOfLines groupOfLines : groupsOfLines) {
                GroupOfLine groupOfLine = ObjectFactory.getGroupOfLine(referential, groupOfLines.getId());
                groupOfLine.setName(groupOfLines.getName().getValue());

                if (groupOfLines.getMembers() != null) {
                    for (JAXBElement<? extends LineRefStructure> lineRefRelStruct : groupOfLines.getMembers().getLineRef()) {
                        String lineIdRef = lineRefRelStruct.getValue().getRef();
                        Line line = ObjectFactory.getLine(referential, lineIdRef);

                        if (line != null) {
                            groupOfLine.addLine(line);
                        }
                    }
                }

                addNetworkId(context, groupOfLines.getId(), netexNetwork.getId());
                groupOfLine.setFilled(true);
            }
        }

        chouetteNetwork.setFilled(true);
    }

    private void addNetworkId(Context context, String objectId, String networkId) {
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
