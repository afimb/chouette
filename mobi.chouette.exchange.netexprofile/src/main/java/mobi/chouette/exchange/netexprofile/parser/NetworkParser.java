package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.GroupOfLines;
import org.rutebanken.netex.model.GroupsOfLinesInFrame_RelStructure;
import org.rutebanken.netex.model.LineRefStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

@Log4j
public class NetworkParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "NetworkContext";

    @Override
    public void initReferentials(Context context) throws Exception {
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        org.rutebanken.netex.model.Network netexNetwork = (org.rutebanken.netex.model.Network) context.get(NETEX_LINE_DATA_CONTEXT);

        mobi.chouette.model.Network chouetteNetwork = ObjectFactory.getPTNetwork(chouetteReferential, netexNetwork.getId());

        chouetteNetwork.setSourceIdentifier("NeTEx");
        chouetteNetwork.setName(netexNetwork.getName().getValue());
        chouetteNetwork.setRegistrationNumber(netexNetwork.getId().split(":")[2]);

        GroupsOfLinesInFrame_RelStructure groupsOfLinesStruct = netexNetwork.getGroupsOfLines();

        if (groupsOfLinesStruct != null) {
            List<GroupOfLines> groupsOfLines = groupsOfLinesStruct.getGroupOfLines();

            for (GroupOfLines groupOfLines : groupsOfLines) {
                GroupOfLine groupOfLine = ObjectFactory.getGroupOfLine(chouetteReferential, groupOfLines.getId());
                groupOfLine.setName(groupOfLines.getName().getValue());

                List<JAXBElement<? extends LineRefStructure>> lineRefStructs = groupOfLines.getMembers().getLineRef();

                for (JAXBElement<? extends LineRefStructure> lineRefRelStruct : lineRefStructs) {
                    String lineIdRef = lineRefRelStruct.getValue().getRef();
                    Line line = ObjectFactory.getLine(chouetteReferential, lineIdRef);

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
