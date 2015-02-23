package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.importer.ParserUtils;
import mobi.chouette.exchange.neptune.validation.PTNetworkValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.model.PTNetwork;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class PTNetworkParser implements Parser, Constant {
	private static final String CHILD_TAG = "PTNetwork";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		int columnNumber =  xpp.getColumnNumber();
		int lineNumber =  xpp.getLineNumber();
		
		PTNetworkValidator validator = (PTNetworkValidator) ValidatorFactory.create(PTNetworkValidator.class.getName(), context);
				
		PTNetwork network = null;
		String objectId = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				objectId = ParserUtils.getText(xpp.nextText());
				network = ObjectFactory.getPTNetwork(referential, objectId);
				network.setFilled(true);
				validator.addLocation(context, objectId, lineNumber, columnNumber);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = ParserUtils.getInt(xpp.nextText());
				network.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = ParserUtils.getSQLDateTime(xpp.nextText());
				network.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				network.setCreatorId(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("versionDate")) {
				Date versionDate = ParserUtils.getSQLDate(xpp.nextText());
				network.setVersionDate(versionDate);
			} else if (xpp.getName().equals("description")) {
				network.setDescription(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("name")) {
				network.setName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("registration")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("registrationNumber")) {
						network.setRegistrationNumber(ParserUtils.getText(xpp
								.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else if (xpp.getName().equals("sourceName")) {
				network.setSourceName(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("sourceIdentifier")) {
				network.setSourceIdentifier(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("sourceType")) {
				PTNetworkSourceTypeEnum type = PTNetworkSourceTypeEnum
						.valueOf(ParserUtils.getText(xpp.nextText()));
				network.setSourceType(type);
			} else if (xpp.getName().equals("comment")) {
				network.setComment(ParserUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("lineId")) {
				String lineId = ParserUtils.getText(xpp.nextText());
				// TODO : revoir l'assemblage Network Line
//				Line line = ObjectFactory.getLine(referential, lineId);
//				line.setPTNetwork(network);
				validator.addLineId(context, objectId, lineId);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(PTNetworkParser.class.getName(),
				new ParserFactory() {
					private PTNetworkParser instance = new PTNetworkParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
