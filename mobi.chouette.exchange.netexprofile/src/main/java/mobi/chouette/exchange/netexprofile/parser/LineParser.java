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
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Network;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class LineParser implements Parser, Constant {

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
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
					Network ptNetwork = ObjectFactory.getPTNetwork(referential, groupIdRef);
					chouetteLine.setNetwork(ptNetwork);
				} else if (dataTypeName.equals(NetexObjectIdTypes.GROUP_OF_LINES)) {
					GroupOfLine group = ObjectFactory.getGroupOfLine(referential, groupIdRef);
					group.addLine(chouetteLine);
					String networkId = netexReferential.getGroupOfLinesToNetwork().get(groupIdRef);
					if (networkId != null) {
						Network ptNetwork = ObjectFactory.getPTNetwork(referential, networkId);
						chouetteLine.setNetwork(ptNetwork);
					}
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
