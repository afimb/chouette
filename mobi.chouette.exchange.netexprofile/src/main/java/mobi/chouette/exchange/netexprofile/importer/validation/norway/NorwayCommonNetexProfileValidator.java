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
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

public class NorwayCommonNetexProfileValidator extends AbstractNorwayNetexProfileValidator implements NetexProfileValidator {

	public static final String NAME = "NorwayCommonNetexProfileValidator";

	@Override
	public void validate(Context context) throws Exception {
		XPathCompiler xpath = (XPathCompiler) context.get(NETEX_XPATH_COMPILER);

		XdmNode commonDom = (XdmNode) context.get(Constant.NETEX_DATA_DOM);

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
		
		XdmValue compositeFrames = selectNodeSet("/PublicationDelivery/dataObjects/CompositeFrame", xpath, commonDom);
		if (compositeFrames.size() > 0) {
			// Using composite frames
			for (XdmItem compositeFrame : compositeFrames) {
				validateCompositeFrame(context, xpath, (XdmNode) compositeFrame);
			}
		} else {
			// Not using composite frames
			validateWithoutCompositeFrame(context, xpath, commonDom);

		}

		return;
	}

	

	protected void validateWithoutCompositeFrame(Context context, XPathCompiler xpath, XdmNode dom) throws XPathExpressionException, SaxonApiException {
		// Validate that we have exactly one ResourceFrame
		// validateElementPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/ResourceFrame", _1_NETEX_RESOURCE_FRAME);
		XdmValue resourceFrames = selectNodeSet("/PublicationDelivery/dataObjects/ResourceFrame", xpath, dom);
		for (XdmItem item : resourceFrames) {
			validateResourceFrame(context, xpath, (XdmNode) item, null);
		}
		// Validate at least 1 ServiceFrame is present
		// validateAtLeastElementPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/ServiceFrame", 1, _1_NETEX_SERVICE_FRAME);
		XdmValue serviceFrames = selectNodeSet("/PublicationDelivery/dataObjects/ServiceFrame", xpath, dom);
		for (XdmItem serviceFrame : serviceFrames) {
			validateServiceFrame(context, xpath, (XdmNode) serviceFrame, null);
		}

		// Validate no TimetableFrame defines in common files
		validateElementNotPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/TimetableFrame", _1_NETEX_COMMON_TIMETABLE_FRAME);

		// No siteframe allowed
		validateElementNotPresent(context, xpath, dom, "/PublicationDelivery/dataObjects/SiteFrame", _1_NETEX_SITE_FRAME);

		// Validate that at least one frame has validityConditions
		validateAtLeastElementPresent(context, xpath, dom,
				"/PublicationDelivery/dataObjects/ServiceFrame[validityConditions] | /PublicationDelivery/dataObjects/TimetableFrame[validityConditions] | /PublicationDelivery/dataObjects/ServiceCalendarFrame[validityConditions] ",
				1, _1_NETEX_NO_VALIDITYCONDITIONS_ON_FRAMES_OUTSIDE_COMPOSITEFRAME);

		// If more than one of a kind, all must have validity conditions
		validateElementNotPresent(context, xpath, dom, "//ServiceCalendarFrame[not(validityConditions) and count(//ServiceCalendarFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//ServiceFrame[not(validityConditions) and count(//ServiceFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);
		validateElementNotPresent(context, xpath, dom, "//TimetableFrame[not(validityConditions) and count(//TimetableFrame) > 1]",
				_1_NETEX_MULTIPLE_FRAMES_OF_SAME_TYPE_WITHOUT_VALIDITYCONDITIONS);

	}

	private void validateCompositeFrame(Context context, XPathCompiler xpath, XdmNode dom) throws XPathExpressionException, SaxonApiException {
		// Check that there are no overriding AvailabilityCondition which is identical to the one defined in the CompositeFrame
		validateCommonFrameConcepts(context, xpath, dom);

		XdmNode resourceFrame = (XdmNode) selectNode("frames/ResourceFrame", xpath, dom);
		if (resourceFrame != null) {
			validateResourceFrame(context, xpath, resourceFrame, null);
		}

		validateServiceFrame(context, xpath, dom, "frames/ServiceFrame");

		// Validate no TimetableFrame defines in common files
		validateElementNotPresent(context, xpath, dom, "frames/TimetableFrame", _1_NETEX_COMMON_TIMETABLE_FRAME);

		XdmNode serviceCalendarFrame = (XdmNode) selectNode("frames/ServiceCalendarFrame", xpath, dom);
		if (serviceCalendarFrame != null) {
			validateServiceCalendarFrame(context, xpath, dom, null);
		}

		validateElementNotPresent(context, xpath, dom, "frames/SiteFrame", _1_NETEX_SITE_FRAME);

		// validateExternalReferenceCorrect(context, xpath, dom, "//StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);
	}



	private void validateServiceFrame(Context context, XPathCompiler xpath, XdmNode dom, String subLevelPath) throws XPathExpressionException, SaxonApiException {
		XdmNode subLevel = dom;
		if (subLevelPath != null) {
			subLevel = (XdmNode) selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateServiceFrameCommonElements(context, xpath, subLevel);
			validateElementNotPresent(context, xpath, subLevel, "lines/Line", _1_NETEX_COMMON_SERVICE_FRAME_LINE);
			validateElementNotPresent(context, xpath, subLevel, "routes/Route", _1_NETEX_COMMON_SERVICE_FRAME_ROUTE);
			validateElementNotPresent(context, xpath, subLevel, "journeyPatterns/JourneyPattern | journeyPatterns/ServiceJourneyPattern",
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
				instance.addExternalReferenceValidator(new ServiceJourneyInterchangeIgnorer());

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
