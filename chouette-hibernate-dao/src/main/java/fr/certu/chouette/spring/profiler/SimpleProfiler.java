//----------------------------------------------------------------------------
/**
 * Socit DRYADE
 *
 * Projet chouette : tests unitaires (package fr.ratp.imm.profiler)
 *
 * SimpleProfiler.java : TODO titre de la classe
 *
 * Historique des modifications :
 * Date        | Auteur         | Libll
 * ------------+----------------+-----------------------------------------------
 * 11 dc. 06 |Marc  			| Cration
 * ------------+----------------+-----------------------------------------------
 */
//----------------------------------------------------------------------------

package fr.certu.chouette.spring.profiler;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectRetrievalFailureException;

import fr.certu.chouette.dao.hibernate.exception.HibernateDaoExceptionCode;
import fr.certu.chouette.dao.hibernate.exception.HibernateDaoRuntimeException;


//----------------------------------------------------------------------------
/**
 * TODO titre de la classe
 *
 * Auteur : Marc
 *
 * Version : $Revision: 1.8 $
 *
 */
//----------------------------------------------------------------------------
public class SimpleProfiler implements Ordered
{
   private static final Logger _log = Logger.getLogger( SimpleProfiler.class);
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
   // this method is the around advice
   public Object profile(ProceedingJoinPoint call) throws Throwable
   {
      Object returnValue;
      try
      {
         returnValue = call.proceed();
      }
      catch( ObjectRetrievalFailureException e)
      {
          _log.debug( "ObjectRetrievalFailureException: Echec de l'opération, "+e+", msg="+e.getMessage());
         throw new HibernateDaoRuntimeException( HibernateDaoExceptionCode.UNKNOWN_ID,  e);
      }
      catch( DataIntegrityViolationException e)
      {
    	  // ex: contrainte unique violée
          _log.debug( "DataIntegrityViolationException: Echec de l'opération, "+e+", msg="+e.getMessage());
		  throw new HibernateDaoRuntimeException( HibernateDaoExceptionCode.INVALID_CONSTRAINT,  e);
      }
      catch( DataAccessException e)
      {
    	  // ex: type numérique de format invalide
          _log.debug( "DataAccessException: Echec de l'opération, "+e+", msg="+e.getMessage());
		  throw new HibernateDaoRuntimeException( HibernateDaoExceptionCode.INVALID_DATA, e);
      }
      finally
      {
         //_log.debug(clock.prettyPrint());
      }
      return returnValue;
   }
}
