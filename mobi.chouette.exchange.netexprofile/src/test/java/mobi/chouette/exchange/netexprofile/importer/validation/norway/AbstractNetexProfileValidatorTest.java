package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexImporter;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;

public class AbstractNetexProfileValidatorTest {
	@Test
	public void testCheckExternalReference() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		
		
		Context context = new Context();
		ValidationReport vr = new ValidationReport();
		vr.addCheckPoint(new CheckPoint("1", RESULT.UNCHECK	, SEVERITY.ERROR));
		context.put(Constant.VALIDATION_REPORT	, vr);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		Document dom = new NetexImporter().parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		AbstractNetexProfileValidator.validateExternalReferenceCorrect(context, xpath , dom , "//n:StopPlaceRef/@n:id", new ExternalReferenceValidator() {
			
			@Override
			public Collection<String> validateReferenceIds(Collection<String> externalIds) {
				Assert.assertEquals(externalIds.size(), 4);
				
				Collection<String> invalidIds = new ArrayList<>();
				invalidIds.add(externalIds.iterator().next());
				
				return invalidIds;
			}
		},"1");
		
		
		// TODO assert on validation report content
	}
	
	@Test public void testCheckElementCount() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		Context context = new Context();
		ValidationReport vr = new ValidationReport();
		vr.addCheckPoint(new CheckPoint("1", RESULT.UNCHECK	, SEVERITY.ERROR));
		context.put(Constant.VALIDATION_REPORT	, vr);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		Document dom = new NetexImporter().parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		AbstractNetexProfileValidator.validateElementPresent(context, xpath, dom, "//n:Network", "NO_NETWORK", "No network element found","1");
	}

	
}
