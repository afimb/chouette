package mobi.chouette.exchange.validation.checkpoint;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;
import mobi.chouette.model.GroupOfLine;

@Log4j
public class GroupOfLineCheckPoints extends AbstractValidation<GroupOfLine> implements Validator<GroupOfLine> {

	@Override
	public void validate(Context context, GroupOfLine target) {
		ValidationData data = (ValidationData) context.get(VALIDATION_DATA);
		List<GroupOfLine> beans = new ArrayList<>(data.getGroupOfLines());
		ValidationParameters parameters = (ValidationParameters) context.get(VALIDATION);
		if (isEmpty(beans))
			return ;

		boolean test4_1 = (parameters.getCheckGroupOfLine() != 0);
		if (test4_1) {
			initCheckPoint(context, L4_GROUP_OF_LINE_1, SEVERITY.E);
			prepareCheckPoint(context, L4_GROUP_OF_LINE_1);
		} else // no other tests for this object
		{
			return ;
		}
		for (int i = 0; i < beans.size(); i++) {
			GroupOfLine bean = beans.get(i);

			// 4-GroupOfLine-1 : check columns constraints
			if (test4_1)
				check4Generic1(context, bean, L4_GROUP_OF_LINE_1, parameters, log);

		}
		return ;
	}

}
