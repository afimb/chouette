package fr.certu.chouette.profiler;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

public class LoggingAspect 
{
	private static final Logger _log = Logger.getLogger( LoggingAspect.class);

	public void logBefore(JoinPoint call) 
	{
		_log.debug( "Appel ShortString="+call.toShortString()+"("+Arrays.asList( call.getArgs()).toString()+")"+", call.getTarget()="+call.getTarget().getClass().getName());
	}
}
