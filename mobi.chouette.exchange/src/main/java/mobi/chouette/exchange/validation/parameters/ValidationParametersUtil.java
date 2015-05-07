package mobi.chouette.exchange.validation.parameters;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.type.TransportModeNameEnum;


@Log4j
public class ValidationParametersUtil {
	
	private static Map<String,List<String>> fieldsMap = new HashMap<>();
	protected static void addFieldList(String key, List<String> fields)
	{
		fieldsMap.put(key,fields);
	}
	
	public static List<String> getFields(NeptuneIdentifiedObject object)
	{
		return fieldsMap.get(object.getClass().getSimpleName());
	}
	
	public static TransportModeParameters getTransportModeParameters(ValidationParameters parameter, TransportModeNameEnum mode)
	{
		switch (mode)
		{
		case Air : return parameter.getModeAir();
		case Bicycle : return parameter.getModeBicycle();
		case Bus : return parameter.getModeBus();
		case Coach : return parameter.getModeCoach();
		case Ferry : return parameter.getModeFerry();
		case LocalTrain : return parameter.getModeLocalTrain();
		case LongDistanceTrain : 
		case LongDistanceTrain_2 : return parameter.getModeLongDistanceTrain();
		case Metro : return parameter.getModeMetro();
		case Other : return parameter.getModeOther();
		case PrivateVehicle : return parameter.getModePrivateVehicle();
		case RapidTransit : return parameter.getModeRapidTransit();
		case Shuttle : return parameter.getModeShuttle();
		case Taxi : return parameter.getModeTaxi();
		case Train : return parameter.getModeTrain();
		case Tramway : return parameter.getModeTramway();
		case Trolleybus : return parameter.getModeTrolleybus();
		case Val : return parameter.getModeVal();
		case Walk : return parameter.getModeWalk();
		case Waterborne : return parameter.getModeWaterborne();
		}
		return parameter.getModeOther();
	}

	public static boolean checkFieldParameters(ValidationParameters parameter, NeptuneIdentifiedObject object)
	{
		String methodName = "getCheck"+object.getClass().getSimpleName();
		try {
			Method method = parameter.getClass().getMethod(methodName);
			Integer res = (Integer) method.invoke(parameter);
			return res != null && res.equals(Integer.valueOf(1));
		} catch (Exception e) {
			log.warn("no validation check flag for object "+object.getClass().getSimpleName());
			return false;
		}
	}

	
	public static FieldParameters getFieldParameters(ValidationParameters parameter, NeptuneIdentifiedObject object, String fieldName )
	{
		Object objectParameters ;
		String methodName = "get"+object.getClass().getSimpleName();
		try {
			Method method = parameter.getClass().getMethod(methodName);
			objectParameters = method.invoke(parameter);
			if (objectParameters == null)
			{
				log.warn("no validation parameters for object "+object.getClass().getSimpleName());
				return null;
			}
			methodName = "get"+fieldName;
			method = objectParameters.getClass().getMethod(methodName);
			return (FieldParameters) method.invoke(objectParameters);
		} catch (Exception e) {
			log.warn("no validation parameters for field "+fieldName+" of object "+object.getClass().getSimpleName());
			return null;
		}
	}

}
