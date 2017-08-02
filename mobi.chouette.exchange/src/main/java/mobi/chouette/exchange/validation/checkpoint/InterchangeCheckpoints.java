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
public class InterchangeCheckpoints extends AbstractValidation<Interchange> implements Validator<Interchange> {

	@Override
	public void validate(Context context, Interchange target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Interchange> beans = new ArrayList<>(data.getInterchanges());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return;

		initCheckPoint(context, INTERCHANGE_1, SEVERITY.E);
		prepareCheckPoint(context, INTERCHANGE_1);

		
		
		boolean test4_1 = (parameters.getCheckInterchange() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_INTERCHANGE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_INTERCHANGE_1);
		} else // no other tests for this object
		{
			return;
		}

		for (int i = 0; i < beans.size(); i++) {
			Interchange bean = beans.get(i);

			checkInterchangeMandatoryFields(context,bean);

			// 4-Interchange-1 : check columns constraints
			if (test4_1) {
				check4Generic1(context, bean, L4_INTERCHANGE_1, parameters, log);
			}
		}
		return;
	}

	private void checkInterchangeMandatoryFields(Context context, Interchange interchange) {

		// TODO code only support local interchanges (within dataspace)
		boolean valid = true;
		if(interchange.getConsumerStopPoint() == null) {
			valid = false;
		}
		if(interchange.getFeederStopPoint() == null) {
			valid = false;
		}
		if(interchange.getConsumerVehicleJourney() == null) {
			valid = false;
		}
		if(interchange.getFeederVehicleJourney() == null) {
			valid = false;
		}
		
		if(!valid) {
			DataLocation location = buildLocation(context,interchange);
			ValidationReporter reporter = ValidationReporter.Factory.getInstance();
			reporter.addCheckPointReportError(context,INTERCHANGE_1, location);
		}
		
	}

}
