package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.AbstractNetexProfileValidator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.CheckPointReport;
import mobi.chouette.exchange.validation.report.CheckPointReport.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.Frames_RelStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.xpath.XPath;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class NorwayLineNetexProfileValidator extends AbstractNetexProfileValidator {
	private static final String _1_NETEX_SERVICEFRAME = "1-NETEX-SERVICEFRAME";
	private static final String _2_NETEX_SERVICEFRAME_NETWORK = "1-NETEX-SERVICEFRAME-NETWORK";
	private static final String _1_NETEX_TIMETABLEFRAME = "1-NETEX-TIMETABLEFRAME";
	private static final String _1_NETEX_SERVICECALENDARFRAME = "1-NETEX-SERVICECALENDARFRAME";
	private static final String _2_NETEX_STOPPLACE_REF = "1-NETEX-STOPPLACE-REF";
	private static final String _2_NETEX_SITEFRAME_STOPPLACE = "1-NETEX-SITEFRAME-STOPPLACE";

    private static final String _2_NETEX_TIMETABLEFRAME_VEHICLEJOURNEY = "2-NETEX-TIMETABLEFRAME-VEHICLEJOURNEY";

	protected void validate(Context context, PublicationDeliveryStructure lineDeliveryStructure, Document dom, XPath xpath) throws Exception {

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

		try {
			Referential referential = (Referential) context.get(REFERENTIAL);
			Context validationContext = (Context) context.get(VALIDATION_CONTEXT);

			if (validationContext != null) {
				PublicationDeliveryValidator publicationDeliveryValidator = (PublicationDeliveryValidator)
						ValidatorFactory.create(PublicationDeliveryValidator.class.getName(), context);
				publicationDeliveryValidator.validate(context, null);
				//LineValidator lineValidator = (LineValidator) ValidatorFactory.create(LineValidator.class.getName(), context);
				//lineValidator.validate(context, null);
			}
		} catch (Exception e) {
			log.error("Netex profile validation failed ", e);
			throw e;
		} finally {
			AbstractValidator.resetContext(context);
			//log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		}
    }

	// TODO check this out instead
	private boolean checkValid(Context context) {
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		for (CheckPointReport checkPointReport : report.getCheckPoints()) {
			if (checkPointReport.getSeverity().equals(CheckPointReport.SEVERITY.ERROR)
					&& checkPointReport.getState().equals(ValidationReporter.RESULT.NOK)) {
				return ERROR;
			}
		}
		return SUCCESS;
	}

	@Override
	public void addCheckpoints(Context context) {
		addCheckpoints(context, _2_NETEX_SITEFRAME_STOPPLACE, SEVERITY.WARNING);
		addCheckpoints(context, _1_NETEX_SERVICEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _2_NETEX_SERVICEFRAME_NETWORK, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_TIMETABLEFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _1_NETEX_SERVICECALENDARFRAME, SEVERITY.ERROR);
		addCheckpoints(context, _2_NETEX_STOPPLACE_REF, SEVERITY.ERROR);

        addCheckpoints(context, _2_NETEX_TIMETABLEFRAME_VEHICLEJOURNEY, SEVERITY.ERROR);
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
