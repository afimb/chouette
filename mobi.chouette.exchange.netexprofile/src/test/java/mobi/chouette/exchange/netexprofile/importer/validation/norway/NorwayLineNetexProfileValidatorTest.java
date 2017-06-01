package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Codespace;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class NorwayLineNetexProfileValidatorTest {


	@Test public void testValidateSimpleFile() throws Exception {
		
		NetexImporter importer = new NetexImporter();

		Context context = createContext(importer);
		

		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT	, vr);

		Set<Codespace> validCodespaces = new HashSet<>();
		Codespace validCodespace = new Codespace();
		validCodespace.setXmlns("AVI");
		validCodespace.setXmlnsUrl("http://avinor.no/");
		validCodespaces.add(validCodespace);
		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

		Document dom = importer.parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		PublicationDeliveryStructure lineDeliveryStructure =importer.unmarshal(dom);

		// Parse (convert to chouette objects)
		context.put(Constant.NETEX_DATA_JAVA, lineDeliveryStructure);
		context.put(Constant.NETEX_DATA_DOM, dom);
		
		NetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.initializeCheckPoints(context);
		validator.validate(context);
		boolean valid = true;
		for(CheckPointReport cp : vr.getCheckPoints()) {
				if(cp.getState() == ValidationReporter.RESULT.NOK) {
					System.err.println(cp);
					valid = false;
				}
			}
		
		// TODO add more checks here
		Assert.assertFalse(valid);;
	}

	@Test public void testValidateWithCommonFile() throws Exception {
		
		NetexImporter importer = new NetexImporter();
		Context context = createContext(importer);

		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT	, vr);
		
		Referential referential =new Referential();
		context.put(Constant.REFERENTIAL,referential);

		Set<Codespace> validCodespaces = new HashSet<>();

		Codespace nsrCodespace = new Codespace();
		nsrCodespace.setXmlns(AbstractNorwayNetexProfileValidator.NSR_XMLNS);
		nsrCodespace.setXmlnsUrl(AbstractNorwayNetexProfileValidator.NSR_XMLNSURL);
		validCodespaces.add(nsrCodespace);

		Codespace avinorCodespace = new Codespace();
		avinorCodespace.setXmlns("AVI");
		avinorCodespace.setXmlnsUrl("http://www.rutebanken.org/ns/avi");
		validCodespaces.add(avinorCodespace);

		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

		Document commonDom = importer.parseFileToDom(new File("src/test/data/norway_line_commonfile/_avinor_common_elements.xml"));
		PublicationDeliveryStructure commonStructure =importer.unmarshal(commonDom);
		context.put(Constant.NETEX_DATA_JAVA, commonStructure);
		context.put(Constant.NETEX_DATA_DOM, commonDom);

		NetexProfileValidator commonValidator = new NorwayCommonNetexProfileValidator();
		commonValidator.initializeCheckPoints(context);
		commonValidator.validate(context);
		boolean valid = true;
		for(CheckPointReport cp : vr.getCheckPoints()) {
			if(cp.getState() == ValidationReporter.RESULT.NOK && cp.getSeverity() == SEVERITY.ERROR) {
				System.err.println(cp);
				valid = false;
			}
		}
	
		// TODO add more checks here
		Assert.assertTrue(valid,"Common file validation errors detected");
		
		Document lineDom = importer.parseFileToDom(new File("src/test/data/norway_line_commonfile/Norwegian-DY121-Stavanger-Bergen.xml"));
		PublicationDeliveryStructure lineStructure =importer.unmarshal(lineDom);
		context.put(Constant.NETEX_DATA_JAVA, lineStructure);
		context.put(Constant.NETEX_DATA_DOM, lineDom);

		NetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.initializeCheckPoints(context);
		validator.validate(context);
		
		for(CheckPointReport cp : vr.getCheckPoints()) {
			if(cp.getState() == ValidationReporter.RESULT.NOK && cp.getSeverity() == SEVERITY.ERROR) {
					System.err.println(cp);
					valid = false;
				}
			}
		
		// TODO add more checks here
		Assert.assertTrue(valid,"Line file validation errors detected");
	}

	protected Context createContext(NetexImporter importer) {
		Context context = new Context();
		context.put(Constant.IMPORTER, importer);

		ActionReport actionReport = new ActionReport();
		context.put(Constant.REPORT, actionReport);
		
		
		ValidationData data =new ValidationData();
		context.put(Constant.VALIDATION_DATA,data);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		context.put(Constant.NETEX_XPATH, xpath);
		return context;
	}

}
