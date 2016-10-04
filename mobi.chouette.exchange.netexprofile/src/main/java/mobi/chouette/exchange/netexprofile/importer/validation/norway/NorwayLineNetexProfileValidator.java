package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import javafx.scene.input.InputMethodTextRun;
import no.rutebanken.netex.model.*;
import org.w3c.dom.Document;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import no.rutebanken.netex.model.PublicationDeliveryStructure.DataObjects;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNetexProfileValidator {
	private static final String _1_NETEX_SERVICEFRAME = "1-NETEX-SERVICEFRAME";
	private static final String _2_NETEX_SERVICEFRAME_NETWORK = "1-NETEX-SERVICEFRAME-NETWORK";
	private static final String _1_NETEX_TIMETABLEFRAME = "1-NETEX-TIMETABLEFRAME";
	private static final String _1_NETEX_SERVICECALENDARFRAME = "1-NETEX-SERVICECALENDARFRAME";
	private static final String _2_NETEX_STOPPLACE_REF = "1-NETEX-STOPPLACE-REF";
	private static final String _2_NETEX_SITEFRAME_STOPPLACE = "1-NETEX-SITEFRAME-STOPPLACE";

	protected void validate(Context context, PublicationDeliveryStructure lineDeliveryStructure, Document dom, XPath xpath) throws XPathExpressionException {

		StopRegistryIdValidator stopRegisterValidator = new StopRegistryIdValidator();

		// TODO add profile validation elements based on xpath elements
		validateElementPresent(context, xpath, dom, "//n:ServiceFrame", "1", "No ServiceFrame", _1_NETEX_SERVICEFRAME);
		//validateMinOccursOfElement(context, xpath, dom, "count(//n:ServiceFrame/n:Network)", 0, _2_NETEX_SERVICEFRAME_NETWORK);

		validateElementNotPresent(context, xpath, dom, "//n:SiteFrame/n:stopPlaces/n:StopPlace", "1", "Should not contain StopPlaces", _2_NETEX_SITEFRAME_STOPPLACE);
		validateExternalReferenceCorrect(context, xpath, dom, "//n:StopPlaceRef/@ref", stopRegisterValidator, _2_NETEX_STOPPLACE_REF);

		validateElementPresent(context, xpath, dom, "//n:TimetableFrame", "1", "No TimetableFrame", _1_NETEX_TIMETABLEFRAME);
		validateElementPresent(context, xpath, dom, "//n:ServiceCalendarFrame", "1", "No ServiceCalendarFrame", _1_NETEX_SERVICECALENDARFRAME);

		// TODO add profile validation elements based on external reference data

		// TODO add profile validation elements based on java codxe
		DataObjects dataObjects = lineDeliveryStructure.getDataObjects();
		List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = dataObjects.getCompositeFrameOrCommonFrame();
        List<TimetableFrame> timetableFrames = getFrames(TimetableFrame.class, compositeFrameOrCommonFrame);
	}

	@Override
	public void addCheckpoints(Context context) {
		addCheckpoints(context, _2_NETEX_SITEFRAME_STOPPLACE, SEVERITY.WARNING);
		addCheckpoints(context, _1_NETEX_SERVICEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _2_NETEX_SERVICEFRAME_NETWORK, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_TIMETABLEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_SERVICECALENDARFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _2_NETEX_STOPPLACE_REF, SEVERITY.ERROR);
	}

	private <T> List<T> getFrames(Class<T> clazz, List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame) {
		List<T> foundFrames = new ArrayList<>();
		for (JAXBElement<? extends Common_VersionFrameStructure> frame : compositeFrameOrCommonFrame) {
            if (frame.getValue() instanceof CompositeFrame) {
				CompositeFrame compositeFrame = (CompositeFrame) frame.getValue();
				Frames_RelStructure frames = compositeFrame.getFrames();
				List<JAXBElement<? extends Common_VersionFrameStructure>> commonFrames = frames.getCommonFrame();
                for (JAXBElement<? extends Common_VersionFrameStructure> commonFrame : commonFrames) {
                    T value = (T) commonFrame.getValue();
                    if (value.getClass().equals(clazz)) {
						foundFrames.add(value);
					}
				}
			} else if (frame.getValue().equals(clazz)) {
				foundFrames.add((T) frame.getValue());
			}
		}
		return foundFrames;
	}

}
