package fr.certu.chouette.dao.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import fr.certu.chouette.filter.Filter;

public class FilterToHibernateClauseTranslator {

	//private static final Logger logger = Logger.getLogger(FilterToHibernateClauseTranslator.class);
	private HashSet<String> aliasses;
	
	@Getter private List<Object> values = new ArrayList<Object>();
	
	

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
		Filter[] filters = clause.getCombinedFilters();
		switch (clause.getType())
		{
		case NOT : return Restrictions.not(translate(filters[0]));
		case AND : 

		{
			if (filters.length == 1) return translate(filters[0]);
			Criterion criterion = Restrictions.and(translate(filters[0]),translate(filters[1]));
			for (int i = 2; i < filters.length; i++) {
				criterion = Restrictions.and(criterion,translate(filters[i]));
			}
			return criterion;
		}
		case OR : 
		{
			if (filters.length == 1) return translate(filters[0]);
			Criterion criterion = Restrictions.or(translate(filters[0]),translate(filters[1]));
			for (int i = 2; i < filters.length; i++) {
				criterion = Restrictions.or(criterion,translate(filters[i]));
			}
			return criterion;
		}
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
		case IS_NULL : 
			return Restrictions.isNull(propertyName);
		case EQUALS : 
			return Restrictions.eq(propertyName, clause.getFirstValue());
		case NOT_EQUALS : 
			return Restrictions.ne(propertyName, clause.getFirstValue());
		case LESS : 
			return Restrictions.lt(propertyName, clause.getFirstValue());
		case LESS_OR_EQUALS : 
			return Restrictions.le(propertyName, clause.getFirstValue());
		case GREATER : 
			return Restrictions.gt(propertyName, clause.getFirstValue());
		case GREATER_OR_EQUALS : 
			return Restrictions.ge(propertyName, clause.getFirstValue());
		case LIKE : 
			return Restrictions.like(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		case ILIKE : 
			return Restrictions.ilike(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		case IN : 
			return translateIn(clause);
		case BETWEEN : 
			return Restrictions.between(propertyName, clause.getFirstValue(), clause.getSecondValue());
		case SQL_WHERE : 
			return Restrictions.sqlRestriction(propertyName);
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
	   String ret = value.toString();
	   if (!ret.contains("%")) ret = "%" + ret + "%";
		return ret;
	}


	public Criterion translate(Filter clause, Criteria criteria, ClassMetadata metadata) 
	{
		aliasses = new HashSet<String>();
		if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");

		switch (clause.getFamilly())
		{
		case EMPTY : 
			break ;  // pas de filtre, uniquement de l'ordre et limite
		case TERMINAL : 
			return  translateTerminal(clause,criteria,metadata);
		case COMBINED : 
			return  translateCombined(clause,criteria,metadata);
		}
		return null; // 


	}


	private Criterion translateCombined(Filter clause,
			Criteria criteria, ClassMetadata metadata) 
	{
		Filter[] filters = clause.getCombinedFilters();
		switch (clause.getType())
		{
		case NOT : return Restrictions.not(translate(filters[0],criteria,metadata));
		case AND :
		{
			if (filters.length == 1) return translate(filters[0]);
			Criterion criterion = Restrictions.and(translate(filters[0]),translate(filters[1],criteria,metadata));
			for (int i = 2; i < filters.length; i++) {
				criterion = Restrictions.and(criterion,translate(filters[i],criteria,metadata));
			}
			return criterion;
		}
		case OR : 
		{
			if (filters.length == 1) return translate(filters[0],criteria,metadata);
			Criterion criterion = Restrictions.or(translate(filters[0],criteria,metadata),translate(filters[1],criteria,metadata));
			for (int i = 2; i < filters.length; i++) {
				criterion = Restrictions.or(criterion,translate(filters[i],criteria,metadata));
			}
			return criterion;
		}
		}

		return null;
	}


	private Criterion translateTerminal(Filter clause, Criteria criteria, ClassMetadata metadata) 
	{
		String propertyName = clause.getAttribute();
		if (propertyName.contains("."))
		{
			// manage aliases
			String[] items = propertyName.split("\\.");
			if (items.length == 2)
			{
				String alias = items[0];
				String path = items[0];
				// check association 
				if (metadata.getPropertyType(path).isAssociationType())
				{
					if (!aliasses.contains(alias))
					{
						aliasses.add(alias);
						criteria.createCriteria(path,alias);
						criteria.setFetchMode(path, FetchMode.JOIN);
					}
				}
			}
		}
		return translateTerminal(clause);
	}


	public String translateToHQLCount(Filter clause, ClassMetadata metadata) 
	{
		values.clear();
		aliasses = new HashSet<String>();
		if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");

		switch (clause.getFamilly())
		{
		case EMPTY : 
		    return  translateEmptyToHQLCount(metadata);
		case TERMINAL : 
			return  translateTerminalToHQLCount(clause,metadata);
		case COMBINED : 
			throw new NullPointerException("Combined query to HQL not yet implemented");
		}
		return null; // 


	}

	public String translateToHQLDelete(Filter clause, ClassMetadata metadata) 
	{
		aliasses = new HashSet<String>();
		if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");

		switch (clause.getFamilly())
		{
		case EMPTY : throw new NullPointerException("Empty query to HQL not yet implemented");

		case TERMINAL : 
			return  translateTerminalToHQLDelete(clause,metadata);
		case COMBINED : 
			throw new NullPointerException("Combined query to HQL not yet implemented");
		}
		return null; // 


	}


	private String translateTerminalToHQLDelete(Filter clause, ClassMetadata metadata) 
	{
		String propertyName = clause.getAttribute();

		String entityName = metadata.getEntityName();

		switch (clause.getType())
		{
		//		case IS_NULL : 
		//			return Restrictions.isNull(propertyName);
		case EQUALS : 
		{
			return "delete "+entityName+" bean where bean."+propertyName+" = "+toHQL(clause.getFirstValue());
		}
		//		case NOT_EQUALS : 
		//			return Restrictions.ne(propertyName, clause.getFirstValue());
		//		case LESS : 
		//			return Restrictions.lt(propertyName, clause.getFirstValue());
		//		case LESS_OR_EQUALS : 
		//			return Restrictions.le(propertyName, clause.getFirstValue());
		//		case GREATER : 
		//			return Restrictions.gt(propertyName, clause.getFirstValue());
		//		case GREATER_OR_EQUALS : 
		//			return Restrictions.ge(propertyName, clause.getFirstValue());
		//		case LIKE : 
		//			return Restrictions.like(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		//		case ILIKE : 
		//			return Restrictions.ilike(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		//		case IN : 
		//			return translateIn(clause);
		//		case BETWEEN : 
		//			return Restrictions.between(propertyName, clause.getFirstValue(), clause.getSecondValue());
		//		case SQL_WHERE : 
		//			return Restrictions.sqlRestriction(propertyName);
		}
		throw new NullPointerException(clause.getType()+" to HQL not yet implemented");
	}

	private String translateEmptyToHQLCount(ClassMetadata metadata) 
	{

		String entityName = metadata.getEntityName();

		return "Select count(*) from "+entityName+" bean ";
	}

	private String translateTerminalToHQLCount(Filter clause, ClassMetadata metadata) 
	{
		String propertyName = clause.getAttribute();

		String entityName = metadata.getEntityName();
		
		switch (clause.getType())
		{
		//		case IS_NULL : 
		//			return Restrictions.isNull(propertyName);
		case EQUALS : 
		{
			return "Select count(*) from "+entityName+" bean where bean."+propertyName+" = "+toHQL(clause.getFirstValue());
		}
		//		case NOT_EQUALS : 
		//			return Restrictions.ne(propertyName, clause.getFirstValue());
		//		case LESS : 
		//			return Restrictions.lt(propertyName, clause.getFirstValue());
		//		case LESS_OR_EQUALS : 
		//			return Restrictions.le(propertyName, clause.getFirstValue());
		//		case GREATER : 
		//			return Restrictions.gt(propertyName, clause.getFirstValue());
		//		case GREATER_OR_EQUALS : 
		//			return Restrictions.ge(propertyName, clause.getFirstValue());
		//		case LIKE : 
		//			return Restrictions.like(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		//		case ILIKE : 
		//			return Restrictions.ilike(propertyName, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
		case IN : 
		{
			 return "Select count(*) from "+entityName+" bean where bean."+propertyName+" IN ("+toHQL(clause.getValueArray())+")";
		}
		//		case BETWEEN : 
		//			return Restrictions.between(propertyName, clause.getFirstValue(), clause.getSecondValue());
		//		case SQL_WHERE : 
		//			return Restrictions.sqlRestriction(propertyName);
		}
		throw new NullPointerException(clause.getType()+" to HQL not yet implemented");
	}

	private String toHQL(Object[] array) 
	{
		String ret = "";
		for (int i = 0; i < array.length; i++) 
		{
			if (i > 0) ret += ",";
			ret+= toHQL(array[i]);
		}
		return ret;
	}
	
	
	private String toHQL(Object value) 
	{
		if (value instanceof Number)
		{
			return value.toString();
		}
		if (value instanceof String)
		{
			return StringHelper.quote(value.toString());
		}
		if (value instanceof Enum)
		{
			values.add(value);
			return "?";
		}
		throw new NullPointerException(value.getClass().getName()+" to HQL not yet implemented");

	}


}
