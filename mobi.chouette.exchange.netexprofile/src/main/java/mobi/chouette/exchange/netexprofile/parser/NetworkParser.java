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
import org.rutebanken.netex.model.GroupsOfLinesInFrame_RelStructure;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class NetworkParser implements Parser, Constant {

    @Override
    public void parse(Context context) throws Exception {
        Referential referential = (Referential) context.get(REFERENTIAL);
        org.rutebanken.netex.model.Network netexNetwork = (org.rutebanken.netex.model.Network) context.get(NETEX_LINE_DATA_CONTEXT);

        mobi.chouette.model.Network chouetteNetwork = ObjectFactory.getPTNetwork(referential, netexNetwork.getId());

        Integer version = Integer.valueOf(netexNetwork.getVersion());
        chouetteNetwork.setObjectVersion(version != null ? version : 0);

/*
        OffsetDateTime changed = netexNetwork.getChanged();
        if (changed != null) {
            chouetteNetwork.setVersionDate(NetexUtils.getDate(changed.toString()));
        }
*/

        chouetteNetwork.setSourceIdentifier("NeTEx");
        chouetteNetwork.setName(netexNetwork.getName().getValue());

        PrivateCodeStructure privateCodeStruct = netexNetwork.getPrivateCode();
        if (privateCodeStruct != null) {
            chouetteNetwork.setRegistrationNumber(privateCodeStruct.getValue());
        } else {
            chouetteNetwork.setRegistrationNumber(netexNetwork.getId().split(":")[2]);
        }

        if (netexNetwork.getDescription() != null) {
            chouetteNetwork.setComment(netexNetwork.getDescription().getValue());
        }

        GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = netexNetwork.getGroupsOfLines();

        if (groupsOfLinesStruct != null) {
            List<GroupOfLines> groupsOfLines = groupsOfLinesStruct.getGroupOfLines();

            for (GroupOfLines groupOfLines : groupsOfLines) {
                GroupOfLine groupOfLine = ObjectFactory.getGroupOfLine(referential, groupOfLines.getId());
                groupOfLine.setName(groupOfLines.getName().getValue());

                List<JAXBElement<? extends LineRefStructure>> lineRefStructs = groupOfLines.getMembers().getLineRef();

                for (JAXBElement<? extends LineRefStructure> lineRefRelStruct : lineRefStructs) {
                    String lineIdRef = lineRefRelStruct.getValue().getRef();
                    Line line = ObjectFactory.getLine(referential, lineIdRef);

                    if (line != null) {
                        groupOfLine.addLine(line);
                    }
                }

                groupOfLine.setFilled(true);
            }
        }

        chouetteNetwork.setFilled(true);
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
