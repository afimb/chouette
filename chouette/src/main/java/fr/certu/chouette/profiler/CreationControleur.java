package fr.certu.chouette.profiler;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;

import fr.certu.chouette.modele.BaseObjet;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

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
		  && args[0] instanceof BaseObjet
		  && (( BaseObjet)args[0]).getId()==null;
   }
   private void keepNewBaseObject( ProceedingJoinPoint call)
   {
	   Object[] args = call.getArgs();
	   (( BaseObjet)args[0]).setId( null);
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
      catch( ServiceException e)
      {
    	  _log.debug( "Echec création ! "+e.getMessage());
    	  isConstraintBrocken = CodeIncident.CONTRAINTE_INVALIDE.equals( e.getCode());
    	  throw e;
      }
      finally
      {
    	 if ( isFirstArgNewBaseObject && isConstraintBrocken) keepNewBaseObject(call);
      }
      return returnValue;
   }
}
