package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.model.Company;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

@Log4j
public class OrganisationsParser implements Parser, Constant {

	private XPathFactory factory = XPathFactory.newInstance();

	@Override
	public void parse(Context context) throws Exception {
		Referential referential = (Referential) context.get(REFERENTIAL);
		Document document = (Document) context.get(Constant.NETEX_LINE_DATA_DOM);
		document.getDocumentElement().normalize();
		Element root = document.getDocumentElement();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext());

		NodeList list = (NodeList)xpath.evaluate("//n:ResourceFrame/n:organisations", root, XPathConstants.NODESET);
		for (int i = 0; i < list.getLength(); i++) {
			Element el = (Element) list.item(i);
			NodeList children = el.getChildNodes();
			for (int k = 0; k < children.getLength(); k++) {
				Node child = children.item(k);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					Element organisationRoot = (Element) child;
					parseOrganisation(referential, organisationRoot);
				}
			}
		}
	}

	private void parseOrganisation(Referential referential, Element organisationRoot) {
		Company company = ObjectFactory.getCompany(referential, organisationRoot.getAttribute(ID));
		String companyNumber = organisationRoot.getElementsByTagName("CompanyNumber").item(0).getTextContent();
		company.setRegistrationNumber(companyNumber);

		String name = organisationRoot.getElementsByTagName(NAME).item(0).getTextContent();
		company.setShortName(name);

		String legalName = organisationRoot.getElementsByTagName("LegalName").item(0).getTextContent();
		company.setName(legalName);

		// String organisationType = organisationRoot.getElementsByTagName("OrganisationType").item(0).getTextContent(); // do we need this, not represented in chouette Company model?

		Element contactDetailsElement = (Element) organisationRoot.getElementsByTagName("ContactDetails").item(0);
		parseContactStructure(contactDetailsElement, company);

		// TODO: uncomment to support customer service contact structure, for now disabled, because chouette model does not support multiple contact structures for a Company
/*
					Element customerServiceContactDetailsElement = (Element) eElement.getElementsByTagName("CustomerServiceContactDetails").item(0);
					if (customerServiceContactDetailsElement != null) {
						parseContactStructure(customerServiceContactDetailsElement, null);
					}
*/
	}

	private void parseContactStructure(Element contactStructureElement, Company company) {
		Node optionalEmailNode = contactStructureElement.getElementsByTagName("Email").item(0);
		if (optionalEmailNode != null) {
			String email = optionalEmailNode.getTextContent();
			company.setEmail(email != null ? email : "");
		}
		Node optionalPhoneNode = contactStructureElement.getElementsByTagName("Phone").item(0);
		if (optionalPhoneNode != null) {
			String phone = optionalPhoneNode.getTextContent();
			company.setPhone(phone != null ? phone : "");
		}
		Node optionalFaxNode = contactStructureElement.getElementsByTagName("Fax").item(0);
		if (optionalFaxNode != null) {
			String fax = optionalFaxNode.getTextContent();
			company.setFax(fax != null ? fax : "");
		}
		Node optionalUrlNode = contactStructureElement.getElementsByTagName("Url").item(0);
		if (optionalUrlNode != null) {
			String url = optionalUrlNode.getTextContent();
			company.setUrl(url != null ? url : "");
		}
	}

	static {
		ParserFactory.register(OrganisationsParser.class.getName(), new ParserFactory() {
			private OrganisationsParser instance = new OrganisationsParser();

			@Override
			protected Parser create() {
				return instance;
			}
		});
	}

}
