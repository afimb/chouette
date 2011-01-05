package fr.certu.chouette.dao.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import fr.certu.chouette.filter.Filter;

public class FilterToHibernateClauseTranslator {

	protected static final Logger logger = Logger.getLogger(FilterToHibernateClauseTranslator.class);
	
   /**
    * @param clause
    * @return
    */
   public  Criterion translate(Filter clause)
   {
		if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");
		
		switch (clause.getFamilly())
      {
		   case EMPTY : 
		      return null;  // pas de filtre, uniquement de l'ordre et limite
		   case TERMINAL : 
		      return translateTerminal(clause);
		   case COMBINED : 
		      return translateCombined(clause);
		}
		return null; // 
	}
	

	/**
    * @param clause
    * @return
    */
   private Criterion translateCombined(Filter clause)
   {
      switch (clause.getType())
      {
         case NOT : return Restrictions.not(translate(clause.getFirstCombinedFilter()));
         case AND : return Restrictions.and(translate(clause.getFirstCombinedFilter()), translate(clause.getSecondCombinedFilter()));
         case OR : return Restrictions.or(translate(clause.getFirstCombinedFilter()), translate(clause.getSecondCombinedFilter()));
      }
      return null;
   }

   /**
    * @param clause
    * @return
    */
   private Criterion translateTerminal(Filter clause)
   {
      String propertyName = clause.getAttribute();
      
      switch (clause.getType())
      {
         case IS_NULL : return Restrictions.isNull(propertyName);
         case EQUALS : return Restrictions.eq(propertyName, clause.getFirstValue());
         case NOT_EQUALS : return Restrictions.ne(propertyName, clause.getFirstValue());
         case LESS : return Restrictions.lt(propertyName, clause.getFirstValue());
         case LESS_OR_EQUALS : return Restrictions.le(propertyName, clause.getFirstValue());
         case GREATER : return Restrictions.gt(propertyName, clause.getFirstValue());
         case GREATER_OR_EQUALS : return Restrictions.ge(propertyName, clause.getFirstValue());
         case LIKE : return Restrictions.like(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
         case ILIKE : return Restrictions.ilike(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
         case IN : return translateIn(clause);
         case BETWEEN : return Restrictions.between(propertyName, clause.getFirstValue(), clause.getSecondValue());
         case SQL_WHERE : return Restrictions.sqlRestriction(propertyName);
      }
      return null; // TODO : throw exception
   }

   /**
    * @param clause
    * @return
    */
   private Criterion translateIn(Filter clause)
   {
      if (clause.getFirstValue() != null)
      {
         // sous requête non implémentée dans hibernate
      }
      else if (clause.getValueArray() != null)
      {
         logger.debug("build in clause");
         return Restrictions.in(clause.getAttribute(), clause.getValueArray());
      }
      return null;
   }
   
   /**
    * @param value
    * @return
    */
   private String getILikeOrLikeRestrictionValue(Object value) 
   {
		return "%" + value + "%";
	}

}
