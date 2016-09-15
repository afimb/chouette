package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import no.rutebanken.netex.model.Common_VersionFrameStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure;
import no.rutebanken.netex.model.PublicationDeliveryStructure.DataObjects;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNetexProfileValidator {
	private static final String _1_NETEX_SERVICEFRAME = "1-NETEX-SERVICEFRAME";
	private static final String _1_NETEX_TIMETABLEFRAME = "1-NETEX-TIMETABLEFRAME";
	private static final String _1_NETEX_SERVICECALENDARFRAME = "1-NETEX-SERVICECALENDARFRAME";
	private static final String _2_NETEX_STOPPLACE_REF = "1-NETEX-STOPPLACE-REF";
	private static final String _1_NETEX_SITEFRAME_STOPPLACE = "1-NETEX-SITEFRAME-STOPPLACE";
	private static final String _1_NETEX_TIMETABLEFRAME_AVAILABILITYCONDITION = "1-NETEX-TIMETABLEFRAME-AVAILABILITYCONDITION";
	private static final String _1_NETEX_TIMETABLEFRAME_SERVICEJOURNEY = "1-NETEX-TIMETABLEFRAME-SERVICEJOURNEY";

	protected void validate(Context context, PublicationDeliveryStructure lineDeliveryStructure, Document dom, XPath xpath) throws XPathExpressionException {

		StopRegistryIdValidator stopRegisterValidator = new StopRegistryIdValidator();

		validateElementPresent(context, xpath, dom, "//n:ServiceFrame", "1", "No ServiceFrame", _1_NETEX_SERVICEFRAME);
		validateElementNotPresent(context, xpath, dom, "//n:SiteFrame/n:stopPlaces/n:StopPlace", "1", "Should not contain StopPlaces", _1_NETEX_SITEFRAME_STOPPLACE);
		validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);

		validateElementPresent(context, xpath, dom, "//n:TimetableFrame", "1", "No TimetableFrame", _1_NETEX_TIMETABLEFRAME);
		validateElementPresent(context, xpath, dom, "//n:TimetableFrame/n:validityConditions/n:AvailabilityCondition", "1", "No AvailabilityCondition", _1_NETEX_TIMETABLEFRAME_AVAILABILITYCONDITION);
		validateElementPresent(context, xpath, dom, "//n:TimetableFrame/n:vehicleJourneys/n:ServiceJourney", "1", "No ServiceJourney", _1_NETEX_TIMETABLEFRAME_SERVICEJOURNEY);

		validateElementPresent(context, xpath, dom, "//n:ServiceCalendarFrame", "1", "No ServiceCalendarFrame", _1_NETEX_SERVICECALENDARFRAME);

		// TODO add profile validation elements based on xpath elements
		// TODO add profile validation elements based on external reference data


		// TODO add profile validation elements based on java codxe
		DataObjects dataObjects = lineDeliveryStructure.getDataObjects();
		List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = dataObjects.getCompositeFrameOrCommonFrame();

	}

	@Override
	public void addCheckpoints(Context context) {
		addCheckpoints(context, _1_NETEX_SITEFRAME_STOPPLACE, SEVERITY.WARNING);
		addCheckpoints(context, _1_NETEX_SERVICEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_TIMETABLEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_SERVICECALENDARFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _2_NETEX_STOPPLACE_REF, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_TIMETABLEFRAME_AVAILABILITYCONDITION, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_TIMETABLEFRAME_SERVICEJOURNEY, SEVERITY.ERROR);
	}

}
