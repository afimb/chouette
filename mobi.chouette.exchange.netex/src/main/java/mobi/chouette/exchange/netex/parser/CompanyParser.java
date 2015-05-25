package mobi.chouette.exchange.netex.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.common.XPPUtil;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netex.Constant;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.xmlpull.v1.XmlPullParser;

@Log4j
public class CompanyParser implements Parser, Constant {

	private static final String CHILD_TAG = "organisations";

	@Override
	public void parse(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, CHILD_TAG);
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Operator")) {
				parseOperator(context);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseOperator(Context context) throws Exception {

		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);

		xpp.require(XmlPullParser.START_TAG, null, "Operator");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		String id = xpp.getAttributeValue(null, ID);
		Company company = ObjectFactory.getCompany(referential, id);

		Integer version = Integer.valueOf(xpp.getAttributeValue(null, VERSION));
		company.setObjectVersion(version != null ? version : 0);

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("PublicCode")) {
				company.setCode(xpp.nextText());
			} else if (xpp.getName().equals("CompanyNumber")) {
				company.setRegistrationNumber(xpp.nextText());
			} else if (xpp.getName().equals(NAME)) {
				company.setName(xpp.nextText());
			} else if (xpp.getName().equals("ShortName")) {
				company.setShortName(xpp.nextText());
			} else if (xpp.getName().equals("ContactDetails")) {
				parseContactDetails(context, company);
			} else if (xpp.getName().equals("parts")) {
				parseOrganisationParts(context, company);
			} else if (xpp.getName().equals("departments")) {
				parseDepartments(context, company);
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
		company.setFilled(true);

	}

	private void parseContactDetails(Context context, Company company)
			throws Exception{
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "ContactDetails");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Email")) {
				company.setEmail(xpp.nextText());
			} else if (xpp.getName().equals("Phone")) {
				company.setPhone(xpp.nextText());
			} else if (xpp.getName().equals("Fax")) {
				company.setFax(xpp.nextText());
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseOrganisationParts(Context context, Company company)
			throws Exception {
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "parts");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("OrganisationPart")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(NAME)) {
						company.setOrganisationalUnit(xpp.nextText());
					} else {
						XPPUtil.skipSubTree(log, xpp);
					}
				}
			} else {
				XPPUtil.skipSubTree(log, xpp);
			}
		}
	}

	private void parseDepartments(Context context, Company company)
			throws Exception{
		XmlPullParser xpp = (XmlPullParser) context.get(PARSER);

		xpp.require(XmlPullParser.START_TAG, null, "departments");
		context.put(COLUMN_NUMBER, xpp.getColumnNumber());
		context.put(LINE_NUMBER, xpp.getLineNumber());

		while (xpp.nextTag() == XmlPullParser.START_TAG) {
			if (xpp.getName().equals("Department")) {
				while (xpp.nextTag() == XmlPullParser.START_TAG) {
					if (xpp.getName().equals(NAME)) {
						company.setOperatingDepartmentName(xpp.nextText());
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
