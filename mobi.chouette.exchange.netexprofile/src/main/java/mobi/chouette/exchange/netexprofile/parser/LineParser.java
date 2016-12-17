package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.model.Company;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.UserNeedEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class LineParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "LineContext";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        LinesInFrame_RelStructure linesInFrameStruct = (LinesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> lineElements = linesInFrameStruct.getLine_();

        for (JAXBElement<? extends DataManagedObjectStructure> lineElement : lineElements) {
            org.rutebanken.netex.model.Line line = (org.rutebanken.netex.model.Line) lineElement.getValue();
            NetexObjectUtil.addLineReference(referential, line.getId(), line);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        LinesInFrame_RelStructure linesInFrameStruct = (LinesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);
        List<JAXBElement<? extends DataManagedObjectStructure>> lineElements = linesInFrameStruct.getLine_();

        for (JAXBElement<? extends DataManagedObjectStructure> lineElement : lineElements) {
            org.rutebanken.netex.model.Line netexLine = (org.rutebanken.netex.model.Line) lineElement.getValue();
            mobi.chouette.model.Line chouetteLine = ObjectFactory.getLine(chouetteReferential, netexLine.getId());

            // TODO remember to clear all referential data between file runs, if clearing correctly we will only have 1 Network in referential at this point
            for (mobi.chouette.model.Network network : chouetteReferential.getPtNetworks().values()) {
                if (network.isFilled()) {
                    chouetteLine.setNetwork(network);
                }
            }

            //ModificationEnumeration modification = netexLine.getModification(); // how to handle in chouette? can be: new, delete, revise or delta

            String lineName = netexLine.getName().getValue();
            chouetteLine.setName(lineName);
            chouetteLine.setPublishedName(lineName);

            MultilingualString description = netexLine.getDescription();
            if (description != null) {
                String descriptionValue = description.getValue();

                if (StringUtils.isNotEmpty(descriptionValue)) {
                    chouetteLine.setComment(descriptionValue);
                }
            }

            AllVehicleModesOfTransportEnumeration transportMode = netexLine.getTransportMode();
            TransportModeNameEnum transportModeName = NetexUtils.toTransportModeNameEnum(transportMode.value());
            chouetteLine.setTransportModeName(transportModeName);

            // optional, how to handle in chouette model?
            //TransportSubmodeStructure transportSubmode = netexLine.getTransportSubmode();

            String url = netexLine.getUrl();
            if (StringUtils.isNotEmpty(url)) {
                chouetteLine.setUrl(url);
            }

            chouetteLine.setNumber(netexLine.getPublicCode());

            PrivateCodeStructure privateCode = netexLine.getPrivateCode();
            if (privateCode != null) {
                String privateCodeValue = privateCode.getValue();

                if (StringUtils.isNotEmpty(privateCodeValue)) {
                    chouetteLine.setRegistrationNumber(privateCodeValue);
                }
            }

            String operatorRefValue = netexLine.getOperatorRef().getRef();
            Company company = ObjectFactory.getCompany(chouetteReferential, operatorRefValue);
            chouetteLine.setCompany(company);

            //Boolean monitored = netexLine.isMonitored(); // how to handle in chouette?

            // TODO: create separate parser for every Suitability instances?
            AccessibilityAssessment accessibilityAssessment = netexLine.getAccessibilityAssessment();

            if (accessibilityAssessment != null) {
                LimitationStatusEnumeration mobilityImpairedAccess = accessibilityAssessment.getMobilityImpairedAccess();
                chouetteLine.setMobilityRestrictedSuitable(ParserUtils.getBoolean(mobilityImpairedAccess.value()));

                Suitabilities_RelStructure suitabilitiesStruct = accessibilityAssessment.getSuitabilities();

                if (suitabilitiesStruct != null) {
                    List<Suitability> suitabilities = suitabilitiesStruct.getSuitability();
                    List<UserNeedEnum> userNeeds = new ArrayList<UserNeedEnum>();

                    for (Suitability suitability : suitabilities) {
                        // TODO: implement parsing
                        // UserNeedEnum userNeed = ParserUtils.getEnum(UserNeedEnum.class, need...);
                        // See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/framework#framework-Suitability and https://rutebanken.atlassian.net/wiki/display/PUBLIC/framework#framework-AccessibilityLimitation
                    }
                    chouetteLine.setUserNeeds(userNeeds);
                }
            }

            // TODO: add remaining, optional fields here... see: https://rutebanken.atlassian.net/wiki/display/PUBLIC/network#network-Line

            chouetteLine.setFilled(true);
        }
    }

    static {
        ParserFactory.register(LineParser.class.getName(), new ParserFactory() {
            private LineParser instance = new LineParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
