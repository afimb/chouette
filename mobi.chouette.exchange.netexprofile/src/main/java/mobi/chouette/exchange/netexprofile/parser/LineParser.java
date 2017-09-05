package mobi.chouette.exchange.netexprofile.parser;

import javax.xml.bind.JAXBElement;

import org.rutebanken.netex.model.AllVehicleModesOfTransportEnumeration;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.GroupOfLinesRefStructure;
import org.rutebanken.netex.model.LinesInFrame_RelStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class LineParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		Context parsingContext = (Context) context.get(PARSING_CONTEXT);
		Context networkContext = (Context) parsingContext.get(NetworkParser.LOCAL_CONTEXT);
		LinesInFrame_RelStructure linesInFrameStruct = (LinesInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

		for (JAXBElement<? extends DataManagedObjectStructure> lineElement : linesInFrameStruct.getLine_()) {
			org.rutebanken.netex.model.Line netexLine = (org.rutebanken.netex.model.Line) lineElement.getValue();
			mobi.chouette.model.Line chouetteLine = ObjectFactory.getLine(referential, netexLine.getId());
			chouetteLine.setObjectVersion(NetexParserUtils.getVersion(netexLine));

			if (netexLine.getRepresentedByGroupRef() != null) {
				GroupOfLinesRefStructure representedByGroupRef = netexLine.getRepresentedByGroupRef();
				String groupIdRef = representedByGroupRef.getRef();
				String dataTypeName = groupIdRef.split(":")[1];

				if (dataTypeName.equals(NetexObjectIdTypes.NETWORK)) {
					for (mobi.chouette.model.Network network : referential.getSharedPTNetworks().values()) {
						if (network.getObjectId().equals(groupIdRef) && network.isFilled()) {
							chouetteLine.setNetwork(network);
							break;
						}
					}
				} else if (dataTypeName.equals(NetexObjectIdTypes.GROUP_OF_LINES)) {
					for (GroupOfLine groupOfLine : referential.getSharedGroupOfLines().values()) {
						if (groupOfLine.getObjectId().equals(groupIdRef) && groupOfLine.isFilled()) {
							chouetteLine.getGroupOfLines().add(groupOfLine);

							if (chouetteLine.getNetwork() == null) {
								Context groupOfLinesObjectContext = (Context) networkContext.get(groupIdRef);
								String networkId = (String) groupOfLinesObjectContext.get(NetworkParser.NETWORK_ID);
								mobi.chouette.model.Network network = ObjectFactory.getPTNetwork(referential, networkId);
								chouetteLine.setNetwork(network);
							}

							break;
						}
					}
				} else {
					log.error("Invalid id reference, could not retrieve correct instance");
					throw new RuntimeException("Invalid id reference, could not retrieve correct instance");
				}
			}

			// TODO find out how to handle in chouette? can be: new, delete, revise or delta
			// ModificationEnumeration modification = netexLine.getModification();

			chouetteLine.setName(ConversionUtil.getValue(netexLine.getName()));
			chouetteLine.setPublishedName(ConversionUtil.getValue(netexLine.getShortName()));
			chouetteLine.setComment(ConversionUtil.getValue(netexLine.getDescription()));

			AllVehicleModesOfTransportEnumeration transportMode = netexLine.getTransportMode();
			TransportModeNameEnum transportModeName = NetexParserUtils.toTransportModeNameEnum(transportMode.value());
			chouetteLine.setTransportModeName(transportModeName);
			chouetteLine.setTransportSubModeName(NetexParserUtils.toTransportSubModeNameEnum(netexLine.getTransportSubmode()));
			chouetteLine.setUrl(netexLine.getUrl());
			chouetteLine.setNumber(netexLine.getPublicCode());

			PrivateCodeStructure privateCode = netexLine.getPrivateCode();
			if (privateCode != null) {
				chouetteLine.setRegistrationNumber(privateCode.getValue());
			}

			if (netexLine.getOperatorRef() != null) {
				String operatorRefValue = netexLine.getOperatorRef().getRef();
				Company company = ObjectFactory.getCompany(referential, operatorRefValue);
				chouetteLine.setCompany(company);
			}
			// TODO find out how to handle in chouette
			// Boolean monitored = netexLine.isMonitored();

//			AccessibilityAssessment accessibilityAssessment = netexLine.getAccessibilityAssessment();
//			if (accessibilityAssessment != null) {
//				LimitationStatusEnumeration mobilityImpairedAccess = accessibilityAssessment.getMobilityImpairedAccess();
//				chouetteLine.setMobilityRestrictedSuitable(ParserUtils.getBoolean(mobilityImpairedAccess.value()));
//				Suitabilities_RelStructure suitabilitiesStruct = accessibilityAssessment.getSuitabilities();
//
//				if (suitabilitiesStruct != null) {
//					List<Suitability> suitabilities = suitabilitiesStruct.getSuitability();
//					List<UserNeedEnum> userNeeds = new ArrayList<>();
//
//					for (Suitability suitability : suitabilities) {
//						// TODO: implement
//						// UserNeedEnum userNeed = ParserUtils.getEnum(UserNeedEnum.class, need...);
//						// See: https://rutebanken.atlassian.net/wiki/display/PUBLIC/framework#framework-Suitability and
//						// https://rutebanken.atlassian.net/wiki/display/PUBLIC/framework#framework-AccessibilityLimitation
//					}
//					chouetteLine.setUserNeeds(userNeeds);
//				}
//			}

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
