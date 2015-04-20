package mobi.chouette.exchange.neptune.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.model.Line;
import mobi.chouette.model.util.Referential;


public abstract class AbstractValidator implements Constant
{

	protected static final String prefix = "2-NEPTUNE-";
	
	protected static final String OBJECT_IDS = "encontered_ids";

	@SuppressWarnings("unchecked")
	public static void resetContext(Context context)
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext != null) 
		{
			for (String key : validationContext.keySet()) 
			{
				if (key.equals(OBJECT_IDS))
				{
					Set<String> objects = (Set<String>) validationContext.get(key);
					objects.clear();
				}
				else
				{
					Context localContext = (Context) validationContext.get(key);
					localContext.clear();
				}
				
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static Context getObjectContext(Context context, String localContextName, String objectId)
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		if (validationContext == null) 
		{
			validationContext = new Context();
			context.put(VALIDATION_CONTEXT, validationContext);
			validationContext.put(OBJECT_IDS, new HashSet<String>());
		}
		
		Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);
		objectIds.add(objectId);

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
		if (checkPoint == null )
		{
			initializeCheckPoints(context);
			checkPoint = validationReport.findCheckPointByName(checkPointKey);
		}
		if (checkPoint.getDetails().isEmpty())
			checkPoint.setState(CheckPoint.RESULT.OK);
	}
	
	protected static Line getLine(Referential referential)
	{
		for (Line line : referential.getLines().values()) 
		{
			if (line.isFilled()) return line;
		}
		return null;
	}


	/**
	 * check if a list is null or empty
	 * 
	 * @param list
	 * @return
	 */
	protected boolean isListEmpty(List<?> list)
	{
		return list == null || list.isEmpty();
	}

	protected abstract void initializeCheckPoints(Context context);


}
