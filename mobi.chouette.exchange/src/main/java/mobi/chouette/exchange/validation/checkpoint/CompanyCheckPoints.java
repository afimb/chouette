package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Company;

@Log4j
public class CompanyCheckPoints extends AbstractValidation<Company> implements Validator<Company> {

	@Override
	public ValidationConstraints validate(Context context, Company target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<Company> beans = new ArrayList<>(data.getCompanies());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		ValidationReport report = (ValidationReport) context.get(VALIDATION_REPORT);
		if (isEmpty(beans))
			return null;

		boolean test4_1 = (parameters.getCheckCompany() != 0);
		if (test4_1) {
			initCheckPoint(report, L4_COMPANY_1, CheckPoint.SEVERITY.ERROR);
			prepareCheckPoint(report, L4_COMPANY_1);
		} else // no other tests for this object
		{
			return null;
		}

		for (int i = 0; i < beans.size(); i++) {
			Company bean = beans.get(i);

			// 4-Company-1 : check columns constraints
			if (test4_1)
				check4Generic1(context,report, bean, L4_COMPANY_1, parameters, log);

		}
		return null;
	}

}
