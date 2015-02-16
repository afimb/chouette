package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;


public class AbstractValidator implements Constant
{

	protected static String prefix = "2-NEPTUNE-";

	protected  Context getObjectContext(Context context, String localContextName, String objectId)
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(localContextName);
		Context objectContext = (Context) localContext.get(objectId);
		if (objectContext == null) 
		{
			objectContext = new Context();
			localContext.put(objectId,objectContext);
		}
		return objectContext;
		
	}

	
	protected void addItemToValidation(
			Context context, String prefix, String name, int count,  String... severities)
	{
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		for (int i = 1; i <= count; i++)
		{
			String key = prefix + name + "-" + i;
			if (validationReport.findCheckPointByName(key) == null )
			{
				if (severities[i - 1].equals("W"))
				{
					validationReport.getCheckPoints().add(new CheckPoint(key,  CheckPoint.RESULT.UNCHECK,
							CheckPoint.SEVERITY.WARNING));
				} else
				{
					validationReport.getCheckPoints().add(new CheckPoint(key,  CheckPoint.RESULT.UNCHECK,
							CheckPoint.SEVERITY.ERROR));
				}
			}
		}
		return ;
	}

	/**
	 * add a detail on a checkpoint
	 * 
	 * @param checkPointKey
	 * @param item
	 */
	protected void addValidationError(Context context, String checkPointKey, Detail item)
	{
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		checkPoint.addDetail(item);

	}


	/**
	 * pass checkpoint to ok if uncheck
	 * 
	 * @param checkPointKey
	 */
	protected void prepareCheckPoint(Context context,String checkPointKey)
	{
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		if (checkPoint.getDetails().isEmpty())
			checkPoint.setState(CheckPoint.RESULT.OK);
	}




}
