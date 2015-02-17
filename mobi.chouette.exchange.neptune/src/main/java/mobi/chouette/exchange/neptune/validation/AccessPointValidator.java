package mobi.chouette.exchange.neptune.validation;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.neptune.Constant;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.FileLocation;
import mobi.chouette.exchange.validation.report.Location;
import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.AccessPointTypeEnum;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LinkOrientationEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.Referential;

public class AccessPointValidator extends AbstractValidator implements Validator<AccessPoint> , Constant{

	public static String NAME = "AccessPointValidator";

	private static final String ACCESS_POINT_1 = "2-NEPTUNE-AccessPoint-1";
	private static final String ACCESS_POINT_2 = "2-NEPTUNE-AccessPoint-2";
	private static final String ACCESS_POINT_3 = "2-NEPTUNE-AccessPoint-3";
	private static final String ACCESS_POINT_4 = "2-NEPTUNE-AccessPoint-4";
	private static final String ACCESS_POINT_5 = "2-NEPTUNE-AccessPoint-5";
	private static final String ACCESS_POINT_6 = "2-NEPTUNE-AccessPoint-6";
	private static final String ACCESS_POINT_7 = "2-NEPTUNE-AccessPoint-7";

	static final String LOCAL_CONTEXT = "AccessPoint";


	public AccessPointValidator(Context context) 
	{
		addItemToValidation(context, prefix, "AccessPoint", 7, "E", "E", "E", "E", "E", "E", "E");

	}

	public void addLocation(Context context, String objectId, int lineNumber, int columnNumber)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put(LINE_NUMBER, Integer.valueOf(lineNumber));
		objectContext.put(COLUMN_NUMBER, Integer.valueOf(columnNumber));

	}

	public void addContainedIn(Context  context, String objectId, String containedIn)
	{
		Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
		objectContext.put("containedIn", containedIn);

	}


	@Override
	public ValidationConstraints validate(Context context, AccessPoint target) throws ValidationException
	{
		Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
		Context localContext = (Context) validationContext.get(LOCAL_CONTEXT);
		if (localContext == null || localContext.isEmpty()) return new ValidationConstraints();
		Context stopAreaContext = (Context) validationContext.get(StopAreaValidator.LOCAL_CONTEXT);
		Referential referential = (Referential) context.get(REFERENTIAL);
		Map<String, AccessPoint> accessPoints = referential.getAccessPoints();
		String fileName = (String) context.get(FILE_URL);

		Map<String, StopArea> stopAreas = referential.getStopAreas();
		Map<String, AccessLink> accessLinks = referential.getAccessLinks();

		// build a map on link connected ids
		Map<String, List<AccessLink>> mapAccessLinkByAccessPointId = new HashMap<String, List<AccessLink>>();
		for (AccessLink link : accessLinks.values())
		{
			{
				String id = link.getAccessPoint().getObjectId();
				List<AccessLink> list = mapAccessLinkByAccessPointId.get(id);
				if (list == null)
				{
					list = new ArrayList<AccessLink>();
					mapAccessLinkByAccessPointId.put(id, list);
				}
				list.add(link);
			}
		}

		prepareCheckPoint(context,ACCESS_POINT_1);
		prepareCheckPoint(context,ACCESS_POINT_3);
		prepareCheckPoint(context,ACCESS_POINT_7);

		for (String objectId : localContext.keySet()) 
		{

			Context objectContext = (Context) localContext.get(objectId);
			AccessPoint accessPoint = accessPoints.get(objectId);
			int lineNumber = ((Integer) objectContext.get(LINE_NUMBER)).intValue();
			int columnNumber = ((Integer) objectContext.get(COLUMN_NUMBER)).intValue();
			FileLocation sourceLocation = new FileLocation(fileName, lineNumber, columnNumber);
			// 2-NEPTUNE-AccessPoint-1 : check existence of containedIn stopArea
			String containedIn = (String) objectContext.get("containedIn");
			if (containedIn == null || !stopAreaContext.containsKey(containedIn))
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("containedIn", containedIn);
				Detail errorItem = new Detail(
						ACCESS_POINT_1,
						new Location(sourceLocation,accessPoint.getObjectId()), map);
				addValidationError(context,ACCESS_POINT_1, errorItem);
			} else
			{
				StopArea parent = stopAreas.get(containedIn);
				// 2-NEPTUNE-AccessPoint-2 : check type of containedIn stopArea
				prepareCheckPoint(context,ACCESS_POINT_2);
				if (parent.getAreaType().equals(ChouetteAreaEnum.ITL))
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("containedIn", containedIn);
					Detail errorItem = new Detail(
							ACCESS_POINT_2,
							new Location(sourceLocation,accessPoint.getObjectId()), map);
					addValidationError(context,ACCESS_POINT_2, errorItem);
				}
			}

			// 2-NEPTUNE-AccessPoint-3 : check presence of access links
			List<AccessLink> links = mapAccessLinkByAccessPointId.get(accessPoint
					.getObjectId());
			if (links == null)
			{
				Detail errorItem = new Detail(
						ACCESS_POINT_3,
						new Location(sourceLocation,accessPoint.getObjectId()));
				addValidationError(context,ACCESS_POINT_3, errorItem);

			} else
			{
				boolean startFound = false;
				boolean endFound = false;
				for (AccessLink link : links)
				{
					if (link.getLinkOrientation().equals(LinkOrientationEnum.AccessPointToStopArea))
						startFound = true;
					if (link.getLinkOrientation().equals(LinkOrientationEnum.StopAreaToAccessPoint))
						endFound = true;
				}

				if (accessPoint.getType().equals(AccessPointTypeEnum.In))
				{
					// 2-NEPTUNE-AccessPoint-4 : if type in : check only
					// accesslinks on start
					prepareCheckPoint(context,ACCESS_POINT_4);
					if (endFound)
					{
						Detail errorItem = new Detail(
								ACCESS_POINT_4,
								new Location(sourceLocation,accessPoint.getObjectId()));
						addValidationError(context,ACCESS_POINT_4, errorItem);
					}
				} else if (accessPoint.getType().equals(AccessPointTypeEnum.Out))
				{
					// 2-NEPTUNE-AccessPoint-5 : if type out : check only
					// accesslinks on end
					prepareCheckPoint(context,ACCESS_POINT_5);
					if (startFound)
					{
						Detail errorItem = new Detail(
								ACCESS_POINT_5,
								new Location(sourceLocation,accessPoint.getObjectId()));
						addValidationError(context,ACCESS_POINT_5, errorItem);
					}

				} else
					// inout
				{
					// 2-NEPTUNE-AccessPoint-6 : if type inout : check minimum
					// one accessLink in each direction
					prepareCheckPoint(context,ACCESS_POINT_6);
					if (!startFound || !endFound)
					{
						Detail errorItem = new Detail(
								ACCESS_POINT_6,
								new Location(sourceLocation,accessPoint.getObjectId()));
						addValidationError(context,ACCESS_POINT_6, errorItem);
					}
				}
			}

			// 2-NEPTUNE-AccessPoint-7 : check centroid projection type as WSG84
			if (!accessPoint.getLongLatType().equals(LongLatTypeEnum.WGS84))
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("longLatType", accessPoint.getLongLatType().toString());
				Detail errorItem = new Detail(
						ACCESS_POINT_7,
						new Location(sourceLocation,accessPoint.getObjectId()), map);
				addValidationError(context,ACCESS_POINT_7, errorItem);
			}
		}
		return new ValidationConstraints();
	}

	public static class DefaultValidatorFactory extends ValidatorFactory {



		@Override
		protected Validator<AccessPoint> create(Context context) {
			AccessPointValidator instance = (AccessPointValidator) context.get(NAME);
			if (instance == null) {
				instance = new AccessPointValidator(context);
				context.put(NAME, instance);
			}
			return instance;
		}

	}

	static {
		ValidatorFactory.factories
		.put(AccessPointValidator.class.getName(), new DefaultValidatorFactory());
	}



}
