package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Interchange;

@Log4j
public class InterchangeCheckPoints extends AbstractValidation<Interchange> implements Validator<Interchange> {

	@Override
	public void validate(Context context, Interchange target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Interchange> beans = new ArrayList<>(data.getInterchanges());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;

		initCheckPoint(context, INTERCHANGE_1, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_1);
		initCheckPoint(context, INTERCHANGE_2, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_2);
		initCheckPoint(context, INTERCHANGE_3, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_3);
		initCheckPoint(context, INTERCHANGE_4, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_4);

		boolean sourceFile = context.get(SOURCE).equals(SOURCE_FILE);

		boolean test4_1 = (parameters.getCheckInterchange() != 0) && !sourceFile;
		if (test4_1) {
			initCheckPoint(context, L4_INTERCHANGE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_INTERCHANGE_1);
		} else // no other tests for this object
		{
			return;
		}

		for (int i = 0; i < beans.size(); i++) {
			Interchange bean = beans.get(i);

			checkInterchangeMandatoryFields(context, bean, true);

			// 4-Interchange-1 : check columns constraints
			if (test4_1) {
				check4Generic1(context, bean, L4_INTERCHANGE_1, parameters, log);
			}
		}
		return;
	}

	private void checkInterchangeMandatoryFields(Context context, Interchange interchange, boolean onlyWithinReferential) {

		ValidationReporter reporter = ValidationReporter.Factory.getInstance();
		if (interchange.getFeederStopPoint() == null) {
			DataLocation source = buildLocation(context, interchange);
			reporter.addCheckPointReportError(context, INTERCHANGE_1, source, interchange.getFeederStopPointObjectid());
		}
		if (interchange.getFeederVehicleJourney() == null) {
			DataLocation source = buildLocation(context, interchange);
			reporter.addCheckPointReportError(context, INTERCHANGE_2, source, interchange.getFeederVehicleJourneyObjectid());
		}

		// TODO code only support local interchanges (within dataspace)

		if (interchange.getConsumerStopPoint() == null) {
			String consumerScheduledStopPointId = interchange.getConsumerStopPointObjectid();
			if (!onlyWithinReferential || consumerScheduledStopPointId == null || consumerScheduledStopPointId.startsWith(interchange.objectIdPrefix())) {
				DataLocation source = buildLocation(context, interchange);
				reporter.addCheckPointReportError(context, INTERCHANGE_3, source, consumerScheduledStopPointId);
			}
		}
		if (interchange.getConsumerVehicleJourney() == null) {
			String consumerVehicleJourneyId = interchange.getConsumerVehicleJourneyObjectid();
			if (!onlyWithinReferential || consumerVehicleJourneyId == null || consumerVehicleJourneyId.startsWith(interchange.objectIdPrefix())) {
				DataLocation source = buildLocation(context, interchange);
				reporter.addCheckPointReportError(context, INTERCHANGE_4, source, consumerVehicleJourneyId);
			}
		}


	}

}
