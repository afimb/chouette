package mobi.chouette.exchange.netex.parser;

import java.util.Collection;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.TransportMode;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.TransportModeConverter;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.exchange.netex.NetexChouetteIdGenerator;
import mobi.chouette.exchange.netex.NetexChouetteIdObjectUtil;
import mobi.chouette.exchange.netex.NetexTransportModeConverter;
import mobi.chouette.exchange.netex.importer.NetexImportParameters;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class LineParser implements Parser, Constant {

	private static final String CHILD_TAG = "lines";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Line")) {
				parseLine(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseLine(Context context) throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "Line");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());
		
		NetexImportParameters configuration = (NetexImportParameters) context.get(CONFIGURATION);
		NetexChouetteIdGenerator chouetteIdGenerator = (NetexChouetteIdGenerator) context.get(CHOUETTEID_GENERATOR);
		NetexTransportModeConverter ntmc = NetexTransportModeConverter.getInstance();
		TransportModeConverter tmc = (TransportModeConverter) context.get(TRANSPORT_MODE_CONVERTER);
		String id = xpp.getAttributeValue(null, ID);
		Line line = NetexChouetteIdObjectUtil.getLine(referential, chouetteIdGenerator.toChouetteId(id, configuration.getDefaultCodespace(),Line.class));

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		line.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals(NAME)) {
				line.setName(xpp.nextText());
			} else if (xpp.getName().equals("ShortName")) {
				line.setPublishedName(xpp.nextText());
			} else if (xpp.getName().equals("Description")) {
				line.setComment(xpp.nextText());
			} else if (xpp.getName().equals("TransportMode")) {
				String value = xpp.nextText();
				if( value != null) {
					// If base default format different than netex
					if (!configuration.getDefaultFormat().equalsIgnoreCase("Netex")) {
						if(value != null) {
							TransportMode trSrc = new TransportMode(value, "unspecified");
							TransportMode tmpTM = ntmc.specificToGenericMode(trSrc);
							TransportMode tM = tmc.genericToSpecificMode(tmpTM);
					
							line.setTransportMode(tM.getMode());
							line.setTransportSubMode(tM.getSubMode());
						}
					}
				}
			} else if (xpp.getName().equals("PublicCode")) {
				line.setNumber(xpp.nextText());
			} else if (xpp.getName().equals("PrivateCode")) {
				line.setRegistrationNumber(xpp.nextText());
			} else if (xpp.getName().equals("routes")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("RouteRef")) {
						String ref = xpp.getAttributeValue(null, REF);
						Route route = NetexChouetteIdObjectUtil.getRoute(referential, chouetteIdGenerator.toChouetteId(ref, configuration.getDefaultCodespace(),Route.class));
						route.setLine(line);
						XPPUtil.skipSubTree(log, xpp);
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}

		line.setNetwork(getPTNetwork(referential));
		line.setCompany(getCompany(referential));

		// for (GroupOfLine groupOfLine :
		// referential.getGroupOfLines().values())
		// {
		// line.addGroupOfLine(groupOfLine);
		// }
		line.setFilled(true);

	}

	private Company getCompany(Referential referential) {
		Company result = null;
		Collection<Company> list = referential.getCompanies().values();
		Company[] array = list.toArray(new Company[list.size()]);
		if (array != null && array.length == 1) {
			result = array[0];
		}
		return result;
	}

	private Network getPTNetwork(Referential referential) {
		Network result = null;
		Collection<Network> list = referential.getPtNetworks().values();
		Network[] array = list.toArray(new Network[list.size()]);
		if (array != null && array.length == 1) {
			result = array[0];
		}
		return result;
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
