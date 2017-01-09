package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexCommonFilesParserCommand;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.util.ProfileValidatorCodespace;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.util.Referential;

public class NorwayLineNetexProfileValidatorTest {


	@Test public void testValidateSimpleFile() throws Exception {
		
		NetexImporter importer = new NetexImporter();

		Context context = new Context();
		context.put(Constant.IMPORTER, importer);

		ActionReport actionReport = new ActionReport();
		context.put(Constant.REPORT, actionReport);
		
		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT	, vr);
		
		ValidationData data =new ValidationData();
		context.put(Constant.VALIDATION_DATA,data);

		context.put(Constant.NETEX_COMMON_DATA_DOMS, new ArrayList<Document>());

		
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		context.put(Constant.NETEX_LINE_DATA_XPATH, xpath);
		
		Set<ProfileValidatorCodespace> validCodespaces = new HashSet<>();
		validCodespaces.add(new ProfileValidatorCodespace("AVI","http://avinor.no/"));
		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);

		Document dom = importer.parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		PublicationDeliveryStructure lineDeliveryStructure =importer.unmarshal(dom);

		// Parse (convert to chouette objects)
		context.put(Constant.NETEX_LINE_DATA_JAVA, lineDeliveryStructure);
		context.put(Constant.NETEX_LINE_DATA_DOM, dom);
		
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

		Context context = new Context();
		context.put(Constant.IMPORTER, importer);

		ActionReport actionReport = new ActionReport();
		context.put(Constant.REPORT, actionReport);
		
		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT	, vr);
		
		ValidationData data =new ValidationData();
		context.put(Constant.VALIDATION_DATA,data);

		Referential referential =new Referential();
		context.put(Constant.REFERENTIAL,referential);

		context.put(Constant.NETEX_COMMON_DATA_DOMS, new ArrayList<Document>());
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		context.put(Constant.NETEX_LINE_DATA_XPATH, xpath);

		Set<ProfileValidatorCodespace> validCodespaces = new HashSet<>();
		validCodespaces.add(new ProfileValidatorCodespace(NorwayLineNetexProfileValidator.NSR_XMLNS, NorwayLineNetexProfileValidator.NSR_XMLNSURL));
		validCodespaces.add(new ProfileValidatorCodespace("AVI","http://www.rutebanken.org/ns/avi"));
		context.put(Constant.NETEX_VALID_CODESPACES, validCodespaces);



		
		NetexCommonFilesParserCommand commonParser =new NetexCommonFilesParserCommand();
		Path path = FileSystems.getDefault().getPath("src/test/data/norway_line_commonfile/", "_avinor_common_elements.xml");
		List<Path> commonFiles = new ArrayList<>();
		commonFiles.add(path);
		commonParser.setFiles(commonFiles);
		commonParser.execute(context);

		// Parse (convert to chouette objects)
		Document lineDom = importer.parseFileToDom(new File("src/test/data/norway_line_commonfile/Norwegian-DY121-Stavanger-Bergen.xml"));
		PublicationDeliveryStructure lineStructure =importer.unmarshal(lineDom);
		context.put(Constant.NETEX_LINE_DATA_JAVA, lineStructure);
		context.put(Constant.NETEX_LINE_DATA_DOM, lineDom);

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
		Assert.assertTrue(valid);;
	}

}