package mobi.chouette.exchange.neptune.validation;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;


public class AbstractValidator implements Constant
{

	protected static String prefix = "2-NEPTUNE-";

	public static void resetContext(Context context)
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext != null) 
		{
			for (Object object : validationContext.values()) 
			{
				Context localContext = (Context) object;
				localContext.clear();
			}
		}
	}

	protected static Context getObjectContext(Context context, String localContextName, String objectId)
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext == null)
		{
			validationContext = new Context();
			context.put(VALIDATION_CONTEXT, validationContext);
		}
		Context localContext = (Context) validationContext.get(localContextName);
		if (localContext == null)
		{
			localContext = new Context();
			validationContext.put(localContextName, localContext);
		}
		Context objectContext = (Context) localContext.get(objectId);
		if (objectContext == null) 
		{
			objectContext = new Context();
			localContext.put(objectId,objectContext);
		}
		return objectContext;

	}


	protected static void addItemToValidation(
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
	protected static void addValidationError(Context context, String checkPointKey, Detail item)
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
	protected static void prepareCheckPoint(Context context,String checkPointKey)
	{
		ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
		if (checkPoint.getDetails().isEmpty())
			checkPoint.setState(CheckPoint.RESULT.OK);
	}




}
