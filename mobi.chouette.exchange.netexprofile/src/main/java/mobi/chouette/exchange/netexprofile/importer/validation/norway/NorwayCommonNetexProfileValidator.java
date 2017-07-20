package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.StopPlaceRegistryIdValidator.DefaultExternalReferenceValidatorFactory;
import mobi.chouette.exchange.netexprofile.util.NetexIdExtractorHelper;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.Codespace;

public class NorwayCommonNetexProfileValidator extends AbstractNorwayNetexProfileValidator implements NetexProfileValidator {

	public static final String NAME = "NorwayCommonNetexProfileValidator";

	@Override
	public void validate(Context context) throws Exception {
		XPath xpath = (XPath) context.get(NETEX_XPATH);

		Document commonDom = (Document) context.get(Constant.NETEX_DATA_DOM);

        Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(mobi.chouette.exchange.netexprofile.Constant.NETEX_COMMON_FILE_IDENTIFICATORS);
        String fileName = (String) context.get(FILE_NAME);

		
        // Find id-fields and to check for duplicates later
		Set<IdVersion> localIdsInCommonFile = new HashSet<>(NetexIdExtractorHelper.collectEntityIdentificators(context, xpath, commonDom, new HashSet<>(Arrays.asList("Codespace"))));
        ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		for(IdVersion id : localIdsInCommonFile) {
            data.getDataLocations().put(id.getId(), DataLocationHelper.findDataLocation(id));
            List<String> list = commonIds.get(id);
            if (list == null) {
                list = new ArrayList<String>();
                commonIds.put(id, list);
            }
            list.add(fileName);
		}

		
		@SuppressWarnings("unchecked")
		Set<Codespace> validCodespaces = (Set<Codespace>) context.get(NETEX_VALID_CODESPACES);

		// Validate elements in common files
		verifyAcceptedCodespaces(context, xpath, commonDom, validCodespaces);
		
		// Verify that local ids er ok
		Set<IdVersion> localIds = new HashSet<>(NetexIdExtractorHelper.collectEntityIdentificators(context, xpath, commonDom, new HashSet<>(Arrays.asList("Codespace"))));
		List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(context, xpath, commonDom, null);
		verifyIdStructure(context, localIds, ID_STRUCTURE_REGEXP, validCodespaces);

		verifyReferencesToCorrectEntityTypes(context,localRefs);
		verifyUseOfVersionOnLocalElements(context, localIds);
		verifyUseOfVersionOnRefsToLocalElements(context, localIds, localRefs);
		verifyExternalRefs(context, localRefs,localIds, new HashSet<IdVersion>());
		
		NodeList compositeFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame", xpath, commonDom);
		if (compositeFrames.getLength() > 0) {
			// Using composite frames
			for (int i = 0; i < compositeFrames.getLength(); i++) {
				validateCompositeFrame(context, xpath, compositeFrames.item(i));
			}
		} else {
			// Not using composite frames
			validateWithoutCompositeFrame(context, xpath, commonDom);

		}

		return;
	}

	

	protected void validateWithoutCompositeFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		// Validate that we have exactly one ResourceFrame
		// validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ResourceFrame", _1_NETEX_RESOURCE_FRAME);
		NodeList resourceFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:ResourceFrame", xpath, dom);
		for (int i = 0; i < resourceFrames.getLength(); i++) {
			validateResourceFrame(context, xpath, resourceFrames.item(i), null);
		}
		// Validate at least 1 ServiceFrame is present
		// validateAtLeastElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", 1, _1_NETEX_SERVICE_FRAME);
		NodeList serviceFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", xpath, dom);
		for (int i = 0; i < serviceFrames.getLength(); i++) {
			validateServiceFrame(context, xpath, serviceFrames.item(i), null);
		}

		// Validate no TimetableFrame defines in common files
		validateElementNotPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:TimetableFrame", _1_NETEX_COMMON_TIMETABLE_FRAME);

		// No siteframe allowed
		validateElementNotPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:SiteFrame", _1_NETEX_SITE_FRAME);

		// Validate that at least one frame has validityConditions
		validateAtLeastElementPresent(context, xpath, dom,
				"/n:PublicationDelivery/n:dataObjects/n:ServiceFrame[n:validityConditions] | /n:PublicationDelivery/n:dataObjects/n:TimetableFrame[n:validityConditions] | /n:PublicationDelivery/n:dataObjects/n:ServiceCalendarFrame[n:validityConditions] ",
				1, _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME);

		// If more than one of a kind, all must have validity conditions
		validateElementNotPresent(context, xpath, dom, "//n:ServiceCalendarFrame[not(n:validityConditions) and count(//n:ServiceCalendarFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//n:ServiceFrame[not(n:validityConditions) and count(//n:ServiceFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//n:TimetableFrame[not(n:validityConditions) and count(//n:TimetableFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);

	}

	private void validateCompositeFrame(Context context, XPath xpath, Node dom) throws XPathExpressionException {
		// Check that there are no overriding AvailabilityCondition which is identical to the one defined in the CompositeFrame
		validateElementPresent(context, xpath, dom, "n:validityConditions", _1_NETEX_COMPOSITE_FRAME_VALIDITYCONDTITIONS);
		validateElementNotPresent(context, xpath, dom, "n:frames//n:validityConditions", _1_NETEX_VALIDITYCONDITIONS_ON_FRAMES_INSIDE_COMPOSITEFRAME);

		validateElementPresent(context, xpath, dom, "n:codespaces/n:Codespace[n:Xmlns = '" + NSR_XMLNS + "' and n:XmlnsUrl = '" + NSR_XMLNSURL + "']",
				_1_NETEX_CODESPACE);

		Node resourceFrame = selectNode("n:frames/n:ResourceFrame", xpath, dom);
		if (resourceFrame != null) {
			validateResourceFrame(context, xpath, resourceFrame, null);
		}

		validateServiceFrame(context, xpath, dom, "n:frames/n:ServiceFrame");

		// Validate no TimetableFrame defines in common files
		validateElementNotPresent(context, xpath, dom, "n:frames/n:TimetableFrame", _1_NETEX_COMMON_TIMETABLE_FRAME);

		Node serviceCalendarFrame = selectNode("n:frames/n:ServiceCalendarFrame", xpath, dom);
		if (serviceCalendarFrame != null) {
			validateServiceCalendarFrame(context, xpath, dom, null);
		}

		validateElementNotPresent(context, xpath, dom, "n:frames/n:SiteFrame", _1_NETEX_SITE_FRAME);

		// validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);
	}

	private void validateServiceFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateServiceFrameCommonElements(context, xpath, subLevel);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line", _1_NETEX_COMMON_SERVICE_FRAME_LINE);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route", _1_NETEX_COMMON_SERVICE_FRAME_ROUTE);
			validateElementNotPresent(context, xpath, subLevel, "n:journeyPatterns/n:JourneyPattern | n:journeyPatterns/n:ServiceJourneyPattern",
					_1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN);
		}
	}

	public static class DefaultValidatorFactory extends NetexProfileValidatorFactory {
		@Override
		protected NetexProfileValidator create(Context context) throws ClassNotFoundException {
			NetexProfileValidator instance = (NetexProfileValidator) context.get(NAME);
			if (instance == null) {
				instance = new NorwayCommonNetexProfileValidator();

				// Shitty, should use inversion of control pattern and dependency injection
				if("true".equals(context.get("testng"))) {
					instance.addExternalReferenceValidator(new DummyStopReferentialIdValidator());
				} else {
					StopPlaceRegistryIdValidator stopRegistryValidator = (StopPlaceRegistryIdValidator) DefaultExternalReferenceValidatorFactory
							.create(StopPlaceRegistryIdValidator.class.getName(), context);
					instance.addExternalReferenceValidator(stopRegistryValidator);
				}
				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		NetexProfileValidatorFactory.factories.put(NorwayCommonNetexProfileValidator.class.getName(),
				new NorwayCommonNetexProfileValidator.DefaultValidatorFactory());
	}

	@Override
	public boolean isCommonFileValidator() {
		return true;
	}

}
