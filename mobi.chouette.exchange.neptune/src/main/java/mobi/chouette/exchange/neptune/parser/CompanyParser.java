package mobi.chouette.exchange.neptune.parser;

import java.util.Date;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.model.Company;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class CompanyParser implements Parser, Constant {
	private static final String CHILD_TAG = "PTNetwork";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(XPP);
		Referential referential = (Referential) context.get(REFERENTIAL);
		
		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);

		Company company = null;
		while (xpp.nextTag() == XmlPullParser.START_TAG) {

			if (xpp.getName().equals("objectId")) {
				String objectId = NeptuneUtils.getText(xpp.nextText());
				company = ObjectFactory.getCompany(referential, objectId);
			} else if (xpp.getName().equals("objectVersion")) {
				Integer version = NeptuneUtils.getInt(xpp.nextText());
				company.setObjectVersion(version);
			} else if (xpp.getName().equals("creationTime")) {
				Date creationTime = NeptuneUtils.getSQLDateTime(xpp.nextText());
				company.setCreationTime(creationTime);
			} else if (xpp.getName().equals("creatorId")) {
				company.setCreatorId(NeptuneUtils.getText(xpp.nextText()));
				
			} else if (xpp.getName().equals("name")) {
				company.setName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("shortName")) {
				company.setShortName(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("organisationalUnit")) {
				company.setOrganisationalUnit(xpp.nextText());
			} else if (xpp.getName().equals("OperatingDepartmentName")) {
				company.setOperatingDepartmentName(NeptuneUtils.getText(xpp
						.nextText()));
			} else if (xpp.getName().equals("organisationalUnit")) {
				company.setOrganisationalUnit(NeptuneUtils.getText(xpp
						.nextText()));
			} else if (xpp.getName().equals("code")) {
				company.setCode(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("phone")) {
				company.setPhone(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("fax")) {
				company.setFax(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("email")) {
				company.setEmail(NeptuneUtils.getText(xpp.nextText()));
			} else if (xpp.getName().equals("registration")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals("registrationNumber")) {
						company.setRegistrationNumber(NeptuneUtils.getText(xpp
								.nextText()));
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	static {
		ParserFactory.register(CompanyParser.class.getName(),
				new ParserFactory() {
					private CompanyParser instance = new CompanyParser();

					@Override
					protected Parser create() {
						return instance;
					}
				});
	}
}
