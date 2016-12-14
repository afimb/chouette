package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.LineValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.Company;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.UserNeedEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j
public class LineParser extends AbstractParser {

    public static final String LOCAL_CONTEXT = "LineContext";
    public static final String LINE_ID = "lineId";
    public static final String OPERATOR_ID = "operatorId";

    @Override
    public void initReferentials(Context context) throws Exception {
        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        LineValidator validator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(), context);
        LinesInFrame_RelStructure linesInFrameStruct = (LinesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        List<JAXBElement<? extends DataManagedObjectStructure>> lineElements = linesInFrameStruct.getLine_();

        for (JAXBElement<? extends DataManagedObjectStructure> lineElement : lineElements) {
            org.rutebanken.netex.model.Line line = (org.rutebanken.netex.model.Line) lineElement.getValue();
            String objectId = line.getId();

            // 1. initialize operator reference
            String operatorId = NetexObjectUtil.getOperatorRefOfLine(line);
            if (StringUtils.isNotEmpty(operatorId)) {
                validator.addOperatorReference(context, objectId, operatorId);
            }

            // 2. initialize route references
            List<String> routeIds = NetexObjectUtil.getRouteRefsOfLine(line);
            for (String routeId : routeIds) {
                validator.addRouteReference(context, objectId, routeId);
            }

            NetexObjectUtil.addLineReference(referential, objectId, line);
            validator.addObjectReference(context, line);
        }
    }

    @Override
    public void parse(Context context) throws Exception {
        Referential chouetteReferential = (Referential) context.get(REFERENTIAL);
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context organisationContext = (Context) parsingContext.get(OrganisationParser.LOCAL_CONTEXT);

        RouteParser routeParser = (RouteParser) ParserFactory.create(RouteParser.class.getName());

        Collection<org.rutebanken.netex.model.Line> lines = netexReferential.getLines().values();

        for (org.rutebanken.netex.model.Line netexLine : lines) {

            // TODO generate chouette line id here like this:
            //String chouetteLineId = ObjectIdCreator.createLineId(configuration, lineId, calendarStartDate);

            String netexLineId = netexLine.getId();
            String chouetteLineId = netexLine.getId();
            mobi.chouette.model.Line chouetteLine = ObjectFactory.getLine(chouetteReferential, chouetteLineId);
            addLineIdRef(context, netexLineId, chouetteLineId);

            // mandatory,
            // TODO how to solve references with networks, if there are multiple networks in the same file/frame? probably need some id reference between lines and network?
            for (mobi.chouette.model.Network network : chouetteReferential.getPtNetworks().values()) {
                if (network.isFilled()) {
                    chouetteLine.setNetwork(network);
                }
            }

            //ModificationEnumeration modification = netexLine.getModification(); // how to handle in chouette? can be: new, delete, revise or delta

            // mandatory, disabled null checks, because consistency check done in validator
            String lineName = netexLine.getName().getValue();
            chouetteLine.setName(lineName);
            chouetteLine.setPublishedName(lineName);

            // optional
            MultilingualString description = netexLine.getDescription();
            if (description != null) {
                String descriptionValue = description.getValue();
                if (StringUtils.isNotEmpty(descriptionValue)) {
                    chouetteLine.setComment(descriptionValue);
                }
            }

            // mandatory
            AllVehicleModesOfTransportEnumeration transportMode = netexLine.getTransportMode();
            TransportModeNameEnum transportModeName = NetexUtils.toTransportModeNameEnum(transportMode.value());
            chouetteLine.setTransportModeName(transportModeName);

            // optional, how to handle in chouette model?
            //TransportSubmodeStructure transportSubmode = netexLine.getTransportSubmode();

            // optional
            String url = netexLine.getUrl();
            if (StringUtils.isNotEmpty(url)) {
                chouetteLine.setUrl(url);
            }

            // mandatory
            String publicCode = netexLine.getPublicCode();
            if (StringUtils.isNotEmpty(publicCode)) {
                chouetteLine.setNumber(publicCode);
            }

            // optional
            PrivateCodeStructure privateCode = netexLine.getPrivateCode();
            if (privateCode != null) {
                String privateCodeValue = privateCode.getValue();
                if (StringUtils.isNotEmpty(privateCodeValue)) {
                    chouetteLine.setRegistrationNumber(privateCodeValue);
                }
            }

            // TODO find out what kind of organisation to use for a line, is it authority or operator, regtopp uses authority for lines, and operators for vehicle journeys
            // but in the incoming netex, the operator is used as a reference from line, and not the authority

            // mandatory, no need to check for nulls, because consistency check performed during validation
            OperatorRefStructure operatorRef = netexLine.getOperatorRef();
            String operatorRefValue = operatorRef.getRef();

            Company company = ObjectFactory.getCompany(chouetteReferential, operatorRefValue);
            chouetteLine.setCompany(company);

            // mandatory
            //Boolean monitored = netexLine.isMonitored(); // how to handle in chouette?

            // this causes errors in TimetableParser, line 136 wich will be of size = 0
            RouteRefs_RelStructure routeRefsStruct = netexLine.getRoutes();
            List<RouteRefStructure> routeRefs = routeRefsStruct.getRouteRef();

            for (RouteRefStructure routeRef : routeRefs) {
                String objectId = routeRef.getRef();
                routeParser.addLineIdRef(context, objectId, chouetteLineId);
            }

            // mandatory
            // TODO: create separate parser for every Suitability instances
            AccessibilityAssessment accessibilityAssessment = netexLine.getAccessibilityAssessment();
            if (accessibilityAssessment != null) {
                LimitationStatusEnumeration mobilityImpairedAccess = accessibilityAssessment.getMobilityImpairedAccess();
                chouetteLine.setMobilityRestrictedSuitable(ParserUtils.getBoolean(mobilityImpairedAccess.value()));

                Suitabilities_RelStructure suitabilitiesStruct = accessibilityAssessment.getSuitabilities();
                if (suitabilitiesStruct != null) {
                    List<Suitability> suitabilities = suitabilitiesStruct.getSuitability();
                    List<UserNeedEnum> userNeeds = new ArrayList<UserNeedEnum>();
                    for (Suitability suitability : suitabilities) {
                        // TODO: parse needs here...
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

    private void addLineIdRef(Context context, String objectId, String lineId) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(LINE_ID, lineId);
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
