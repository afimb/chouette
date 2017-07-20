package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexProfileValidatorFactory;
import mobi.chouette.exchange.netexprofile.importer.validation.norway.StopPlaceRegistryIdValidator.DefaultExternalReferenceValidatorFactory;
import mobi.chouette.exchange.netexprofile.util.NetexIdExtractorHelper;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.Codespace;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.util.*;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNorwayNetexProfileValidator implements NetexProfileValidator {

	public static final String NAME = "NorwayLineNetexProfileValidator";

	@Override
	public void validate(Context context) throws Exception {
		XPath xpath = (XPath) context.get(NETEX_XPATH);

		Document dom = (Document) context.get(NETEX_DATA_DOM);

		@SuppressWarnings("unchecked")
		Set<Codespace> validCodespaces = (Set<Codespace>) context.get(NETEX_VALID_CODESPACES);
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);

		// StopPlaceRegistryIdValidator stopRegisterValidator = new StopPlaceRegistryIdValidator();

		@SuppressWarnings("unchecked")
		Map<IdVersion, List<String>> commonIds = (Map<IdVersion, List<String>>) context.get(NETEX_COMMON_FILE_IDENTIFICATORS);

		Set<IdVersion> localIds = new HashSet<>(NetexIdExtractorHelper.collectEntityIdentificators(context, xpath, dom, new HashSet<>(Arrays.asList("Codespace"))));
		List<IdVersion> localRefs = NetexIdExtractorHelper.collectEntityReferences(context, xpath, dom, null);

		for (IdVersion id : localIds) {
			data.getDataLocations().put(id.getId(), DataLocationHelper.findDataLocation(id));
		}

		verifyAcceptedCodespaces(context, xpath, dom, validCodespaces);
		verifyIdStructure(context, localIds, ID_STRUCTURE_REGEXP, validCodespaces);
		verifyNoDuplicatesWithCommonElements(context, localIds, commonIds);
		// Allow Frame ID duplicates - not used for anything
		verifyNoDuplicatesAcrossLineFiles(context, localIds,
				new HashSet<>(Arrays.asList("ResourceFrame", "SiteFrame", "CompsiteFrame", "TimetableFrame", "ServiceFrame", "ServiceCalendarFrame","RoutePoint","PointProjection","ScheduledStopPoint")));

		verifyUseOfVersionOnLocalElements(context, localIds);
		verifyUseOfVersionOnRefsToLocalElements(context, localIds, localRefs);
		verifyReferencesToCommonElements(context, localRefs, localIds, commonIds);
		verifyReferencesToCorrectEntityTypes(context, localRefs);
		verifyExternalRefs(context, localRefs, localIds,commonIds != null ? commonIds.keySet() : new HashSet<>());

		NodeList compositeFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:CompositeFrame", xpath, dom);
		if (compositeFrames.getLength() > 0) {
			// Using composite frames
			for (int i = 0; i < compositeFrames.getLength(); i++) {
				validateCompositeFrame(context, xpath, compositeFrames.item(i));
			}
		} else {
			// Not using composite frames
			validateWithoutCompositeFrame(context, xpath, dom);

		}
		return;
	}

	protected void validateWithoutCompositeFrame(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		// Validate that we have exactly one ResourceFrame
		validateElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ResourceFrame", _1_NETEX_RESOURCE_FRAME);
		validateResourceFrame(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ResourceFrame");

		// Validate at least 1 ServiceFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", 1, _1_NETEX_SERVICE_FRAME);
		NodeList serviceFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:ServiceFrame", xpath, dom);
		for (int i = 0; i < serviceFrames.getLength(); i++) {
			validateServiceFrame(context, xpath, serviceFrames.item(i), null);
		}

		// Validate at least 1 TimetableFrame is present
		validateAtLeastElementPresent(context, xpath, dom, "/n:PublicationDelivery/n:dataObjects/n:TimetableFrame", 1, _1_NETEX_TIMETABLE_FRAME);
		NodeList timetableFrames = selectNodeSet("/n:PublicationDelivery/n:dataObjects/n:TimetableFrame", xpath, dom);
		for (int i = 0; i < timetableFrames.getLength(); i++) {
			validateTimetableFrame(context, xpath, timetableFrames.item(i), null);
		}

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

		
		NodeList resourceFrames = selectNodeSet("n:frames/n:ResourceFrame", xpath, dom);
		for (int i = 0; i < resourceFrames.getLength(); i++) {
			validateResourceFrame(context, xpath, resourceFrames.item(i), null);
		}

		validateAtLeastElementPresent(context, xpath, dom, "n:frames/n:ServiceFrame",1, _1_NETEX_SERVICE_FRAME);
		NodeList serviceFrames = selectNodeSet("n:frames/n:ServiceFrame", xpath, dom);
		for (int i = 0; i < serviceFrames.getLength(); i++) {
			validateServiceFrame(context, xpath, serviceFrames.item(i), null);
		}

		validateAtLeastElementPresent(context, xpath, dom, "n:frames/n:TimetableFrame",1, _1_NETEX_TIMETABLE_FRAME);
		NodeList timetableFrames = selectNodeSet("n:frames/n:TimetableFrame", xpath, dom);
		for (int i = 0; i < timetableFrames.getLength(); i++) {
			validateTimetableFrame(context, xpath, timetableFrames.item(i), null);
		}

		NodeList serviceCalendarFrames = selectNodeSet("n:frames/n:ServiceCalendarFrame", xpath, dom);
		for (int i = 0; i < serviceCalendarFrames.getLength(); i++) {
			validateServiceCalendarFrame(context, xpath, serviceCalendarFrames.item(i), null);
		}

		validateElementNotPresent(context, xpath, dom, "n:frames/n:SiteFrame", _1_NETEX_SITE_FRAME);
	}

	private void validateServiceFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateServiceFrameCommonElements(context, xpath, subLevel);
			validateElementPresent(context, xpath, subLevel, "n:lines/n:Line", _1_NETEX_SERVICE_FRAME_LINE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line[not(n:PublicCode)]", _1_NETEX_SERVICE_FRAME_LINE_PUBLIC_CODE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line[not(n:TransportMode)]", _1_NETEX_SERVICE_FRAME_LINE_TRANSPORTMODE);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line/n:routes/n:Route", _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);
			validateElementNotPresent(context, xpath, subLevel, "n:lines/n:Line[not(n:RepresentedByGroupRef)]",
					_1_NETEX_SERVICE_FRAME_LINE_GROUPOFLINES_OR_NETWORK);

			validateAtLeastElementPresent(context, xpath, subLevel, "n:routes/n:Route", 1, _1_NETEX_SERVICE_FRAME_ROUTE_INDIRECTION);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:Name)]", _1_NETEX_SERVICE_FRAME_ROUTE_NAME);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:LineRef)]", _1_NETEX_SERVICE_FRAME_ROUTE_LINEREF);
			validateElementNotPresent(context, xpath, subLevel, "n:routes/n:Route[not(n:pointsInSequence)]", _1_NETEX_SERVICE_FRAME_ROUTE_POINTSINSEQUENCE);

	//		validateElementNotPresent(context, xpath, subLevel, "n:journeyPatterns/n:ServiceJourneyPattern", _1_NETEX_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN);
			validateAtLeastElementPresent(context, xpath, subLevel, "n:journeyPatterns/n:JourneyPattern | n:journeyPatterns/n:ServiceJourneyPattern", 1,
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN);

			
			validateElementNotPresent(context, xpath, subLevel,
					"n:journeyPatterns/n:ServiceJourneyPattern[not(n:RouteRef)] | n:journeyPatterns/n:JourneyPattern[not(n:RouteRef)]",
					_1_NETEX_SERVICE_FRAME_JOURNEY_PATTERN_ROUTE_REF);

			validateElementNotPresent(context, xpath, subLevel, "//n:pointsInSequence/n:StopPointInJourneyPattern[1][not(n:DestinationDisplayRef)]",
					_1_NETEX_COMMON_SERVICE_FRAME_SERVICE_JOURNEY_PATTERN_MISSING_DESTINATIONDISPLAY);

			validateElementNotPresent(context, xpath, subLevel, "n:destinationDisplays/n:DestinationDisplay[not(n:FrontText)]",
					_1_NETEX_SERVICE_FRAME_DESTINATION_DISPLAY_FRONTTEXT);
			validateElementNotPresent(context, xpath, subLevel, "//n:StopPointInJourneyPattern[n:ForAlighting = 'false' and n:ForBoarding = 'false']",
					_1_NETEX_SERVICE_FRAME_STOP_WITHOUT_BOARDING_OR_ALIGHTING);
		}
	}

	private void validateTimetableFrame(Context context, XPath xpath, Node dom, String subLevelPath) throws XPathExpressionException {
		Node subLevel = dom;
		if (subLevelPath != null) {
			subLevel = selectNode(subLevelPath, xpath, dom);
		}

		if (subLevel != null) {

			validateAtLeastElementPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney", 1, _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY);
			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney/n:calls", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_CALLS);

//			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:TransportMode)]",
//					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_TRANSPORT_MODE);

			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:passingTimes)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIMES);
			validateElementNotPresent(context, xpath, subLevel,
					"n:vehicleJourneys/n:ServiceJourney/n:passingTimes/n:TimetabledPassingTime[not(n:DepartureTime) and not(n:ArrivalTime)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_MISSING_DEPARTURE_OR_ARRIVAL);
			validateElementNotPresent(context, xpath, subLevel,
					"n:vehicleJourneys/n:ServiceJourney[not(n:passingTimes/n:TimetabledPassingTime[1]/n:DepartureTime)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_FIRST_DEPARTURE);
			validateElementNotPresent(context, xpath, subLevel,
					"n:vehicleJourneys/n:ServiceJourney[count(n:passingTimes/n:TimetabledPassingTime[last()]/n:ArrivalTime) = 0]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_LAST_ARRIVAL);
			validateElementNotPresent(context, xpath, subLevel, "//n:TimetabledPassingTime[n:DepartureTime = n:ArrivalTime]",
					_1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_PASSING_TIME_SAME_VALUE);

			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:JourneyPatternRef)]",
					_1_NETEX_TIMETABLE_FRAME_SERVICEJOURNEY_JOURNEYPATTERN_REF);
			
			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:OperatorRef) and not(../../../n:ServiceFrame/n:lines/n:Line/n:OperatorRef)]", _1_NETEX_TIMETABLE_FRAME_VEHICLEJOURNEY_OPERATORREF_OR_LINE_OPREATORREF);

			validateElementNotPresent(context, xpath, subLevel, "n:vehicleJourneys/n:ServiceJourney[not(n:dayTypes/n:DayTypeRef)]", _1_NETEX_TIMETABLE_FRAME_SERVICE_JOURNEY_DAYTYPEREF);

			

		}
	}

	public static class DefaultValidatorFactory extends NetexProfileValidatorFactory {
		@Override
		protected NetexProfileValidator create(Context context) throws ClassNotFoundException {
			NetexProfileValidator instance = (NetexProfileValidator) context.get(NAME);
			if (instance == null) {
				instance = new NorwayLineNetexProfileValidator();

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
		NetexProfileValidatorFactory.factories.put(NorwayLineNetexProfileValidator.class.getName(),
				new NorwayLineNetexProfileValidator.DefaultValidatorFactory());
	}

	@Override
	public boolean isCommonFileValidator() {
		return false;
	}

}
