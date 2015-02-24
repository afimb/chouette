package mobi.chouette.exchange.validator.parameters;

import java.lang.reflect.Method;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.type.TransportModeNameEnum;


@Log4j
public class ValidationParametersUtil {
	
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
			log.error("object "+object.getClass().getSimpleName()+" not found :"+e.toString());
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
			methodName = "get"+fieldName;
			method = objectParameters.getClass().getMethod(methodName);
			return (FieldParameters) method.invoke(objectParameters);
		} catch (Exception e) {
			log.error("object "+object.getClass().getSimpleName()+" or field "+fieldName+" not found :"+e.toString());
			return null;
		}
	}

}
