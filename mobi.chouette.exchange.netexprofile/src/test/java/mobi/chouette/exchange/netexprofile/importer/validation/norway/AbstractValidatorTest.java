package mobi.chouette.exchange.netexprofile.importer.validation.norway;

public class AbstractValidatorTest {
/*
	@Test
	public void testCheckExternalReference() throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {
		
		
		Context context = new Context();
		ValidationReporter vr = ValidationReporter.Factory.getInstance();
		vr.addItemToValidationReport(context, "1", "E");
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
		ValidationReporter vr = ValidationReporter.Factory.getInstance();
		vr.addItemToValidationReport(context, "1", "E");
		context.put(Constant.VALIDATION_REPORT	, vr);
		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext()) ;
		Document dom = new NetexImporter().parseFileToDom(new File("src/test/data/WF739-201608311015.xml"));
		AbstractNetexProfileValidator.validateElementPresent(context, xpath, dom, "//n:Network", "NO_NETWORK", "No network element found","1");
	}
*/

	
}
