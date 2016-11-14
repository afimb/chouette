package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public class NorwayLineNetexProfileValidatorTest {

/*
	@Test public void testValidateFile() throws Exception {
		
		Context context = new Context();
		ValidationReport vr = new ValidationReport();
		context.put(Constant.VALIDATION_REPORT	, vr);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		NetexImporter importer = new NetexImporter();
		Document dom = importer.parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		PublicationDeliveryStructure lineDeliveryStructure =importer.unmarshal(dom);

		// Parse (convert to chouette objects)
		context.put(Constant.NETEX_LINE_DATA_JAVA, lineDeliveryStructure);
		context.put(Constant.NETEX_LINE_DATA_DOM, dom);
		
		NorwayLineNetexProfileValidator validator = new NorwayLineNetexProfileValidator();
		validator.addCheckpoints(context);
		boolean valid = validator.validate(context);
		if(!valid) {
			for(CheckPointReport cp : vr.getCheckPoints()) {
				if(cp.getState() == ValidationReporter.RESULT.NOK) {
					System.err.println(cp);
				}
			}
		}
		
		// TODO add more checks here
		Assert.assertTrue(valid);;
	}
*/
}
