package fr.certu.chouette.spring.profiler;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;

import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;
import fr.certu.chouette.model.neptune.NeptuneObject;

//----------------------------------------------------------------------------
/**
 *	Protege les instances contre les échecs de leur création en base
 *	dans le cas ou leur id serait modifié à tort
 */
//----------------------------------------------------------------------------
public class CreationControleur implements Ordered
{
   private static final Logger _log = Logger.getLogger( CreationControleur.class);
   private int order;

   // allows us to control the ordering of advice
   public int getOrder()
   {
      return this.order;
   }
   public void setOrder(int order)
   {
      this.order = order;
   }
   
   private boolean isFirstArgNewBaseObject( ProceedingJoinPoint call)
   {
	   Object[] args = call.getArgs();
	   return  args.length>0 
		  && args[0] instanceof NeptuneObject
		  && (( NeptuneObject)args[0]).getId()==null;
   }
   private void keepNewBaseObject( ProceedingJoinPoint call)
   {
	   Object[] args = call.getArgs();
	   (( NeptuneObject)args[0]).setId( null);
 	  _log.debug( "Rétablissement instance ");
   }
   
   // this method is the around advice
   public Object creationProtect(ProceedingJoinPoint call) throws Throwable
   {
      Object returnValue;
      
      boolean isFirstArgNewBaseObject = isFirstArgNewBaseObject( call);
      boolean isConstraintBrocken = false;
      try
      {
         returnValue = call.proceed();
      }
      catch( HibernateDaoRuntimeException e)
      {
    	  _log.debug( "Echec création ! "+e.getMessage());
    	  isConstraintBrocken = HibernateDaoExceptionCode.INVALID_CONSTRAINT.equals( e.getExceptionCode());
    	  throw e;
      }
      finally
      {
    	 if ( isFirstArgNewBaseObject && isConstraintBrocken) keepNewBaseObject(call);
      }
      return returnValue;
   }
}
