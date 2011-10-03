/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.Getter;

/**
 * Chouette request filter builder
 * <p/>
 * an easy Database request builder similar to Hibernate Criterion but which can be use with oder DAO access
 *
 */
public class Filter
{
	/**
	 * Enumeration of possible type of filter
	 *
	 */
	public static enum Type {
		EQUALS,
		NOT_EQUALS,
		LESS,
		LESS_OR_EQUALS,
		GREATER,
		GREATER_OR_EQUALS,
		LIKE,
		ILIKE,
		IN,
		AND,
		OR,
		NOT,
		IS_NULL,
		SQL_WHERE,
		BETWEEN }

	/**
	 * Enumeration of available families for the filter type
	 *
	 */
	public enum TypeFamilly 
	{
		/**
		 * EMPTY : not a filter, contains only order and deepness restrictions
		 */
		EMPTY,
		/**
		 * Elementary filter : restriction values for a bean attribute
		 */
		TERMINAL,
		/**
		 * Combined filter : assembly of other filters for logical operations
		 */
		COMBINED
	}

	/**
	 * Filter type 
	 */
	@Getter private Type type;
	/**
	 * Filter type family
	 */
	@Getter private TypeFamilly familly;
	/**
	 * Filter target bean attribute : name of the bean attribute
	 * <p/>
	 * when a bean contains dependency with another bean, the filter 
	 * can point on an attribute of the dependency bean
	 * <br/>
	 * Sample syntax to point the network name attribute of a line  
	 * <pre>ptNetwork.name</pre>
	 * where <i>ptNetwork</i> is the network attribute name in line bean and <i>name</i> is the name attribute in PTNework bean
	 */
	@Getter private String attribute;
	/**
	 * required value for a TERMINAL family filter (first value when the filter requires 2 values
	 */
	@Getter private Object firstValue;
	/**
	 * second required value for a TERMINAL family filter which requires 2 values 
	 * <p/>
	 * for example : BETWEEN type filter
	 */
	@Getter private Object secondValue;
	/**
	 * list of required values for a TERMINAL family filter (which requires muliple values
	 * <p/>
	 * for example : IN type filter
	 */
	@Getter private Object[] valueArray;
	/**
	 * required value for a COMBINED family filter of first one for filter which requires 2 values 
	 */
	@Getter private Filter[] combinedFilters = null;
	/**
	 * second required value for a COMBINED family filter which requires 2 values 
	 * <p/>
	 * for example : AND type filter
	 */
	// @Getter private Filter secondCombinedFilter = null;
	/**
	 * list of ordering attributes to add to the request clause
	 */
	@Getter private List<FilterOrder> orderList = null;
	/**
	 * start discriminant for deepness result management (0 will be ignored)
	 */
	@Getter private int start = 0;
	/**
	 * limit discriminant for deepness result management (0 will be ignored , i.e. no limit)
	 */
	@Getter private int limit = 0;

	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for EMPTY filter
	 */
	private Filter()
	{
		this.type = null;
		this.familly = TypeFamilly.EMPTY;
	}

	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for EQUALS, NOT_EQUALS, LESS, LESS_OR_EQUALS, GREATER, GREATER_OR_EQUALS, LIKE , ILIKE and IN filters
	 * 
	 * @param type filter type
	 * @param attribute filtered attribute
	 * @param value value
	 */
	private Filter(Type type,String attribute,Object value)
	{
		this();
		if (type.ordinal() > Type.IN.ordinal())
		{
			throw new IllegalArgumentException("This constructor accept only EQUALS, NOT_EQUALS, LESS, LESS_OR_EQUALS, GREATER, GREATER_OR_EQUALS, LIKE , ILIKE and IN types");
		}
		if (value == null || value.equals("")) return;
		this.type = type;
		this.familly = TypeFamilly.TERMINAL;
		this.attribute = attribute;
		this.firstValue = value;
		if (type.ordinal() >= Type.LESS.ordinal() && type.ordinal() <= Type.GREATER_OR_EQUALS.ordinal())
		{
			if (! (value instanceof Number) && !(value instanceof Date))
			{
				throw new IllegalArgumentException("LESS, LESS_OR_EQUALS, GREATER, GREATER_OR_EQUALS accept only numeric or date types");
			}

		}
		if (type.equals(Type.IN))
		{
			if (! (value instanceof String) )
			{
				throw new IllegalArgumentException("IN accept only a sub select clause in a string");
			}
		}
	}

	/**
	 * create a filter for an 'equals' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return an Equals filter
	 */
	public static Filter getNewEqualsFilter(String attribute,Object value)
	{
		return new Filter(Type.EQUALS,attribute,value);
	}
	/**
	 * create a filter for a 'not equals' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Not Equals filter
	 */
	public static Filter getNewNotEqualsFilter(String attribute,Object value)
	{
		return new Filter(Type.NOT_EQUALS,attribute,value);
	}
	/**
	 * create a filter for a 'lesser than' where clause
	 *
	 * the attribute must be comparable
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Less filter
	 */
	public static Filter getNewLessFilter(String attribute,Object value)
	{
		return new Filter(Type.LESS,attribute,value);
	}
	/**
	 * create a filter for a 'lesser or equals' where clause
	 *
	 * the attribute must be comparable
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Less Or Equals filter
	 */
	public static Filter getNewLessOrEqualsFilter(String attribute,Object value)
	{
		return new Filter(Type.LESS_OR_EQUALS,attribute,value);
	}
	/**
	 * create a filter for a 'greater than' where clause
	 *
	 * the attribute must be comparable
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Greater filter
	 */
	public static Filter getNewGreaterFilter(String attribute,Object value)
	{
		return new Filter(Type.GREATER,attribute,value);
	}
	/**
	 * create a filter for a 'greater or equals' where clause
	 *
	 * the attribute must be comparable
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Greater Or Equals filter
	 */
	public static Filter getNewGreaterOrEqualsFilter(String attribute,Object value)
	{
		return new Filter(Type.GREATER_OR_EQUALS,attribute,value);
	}
	/**
	 * create a filter for a 'ilike' where clause
	 *
	 * the attribute must be a string
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return an ilike filter
	 */
	public static Filter getNewIgnoreCaseLikeFilter(String attribute,String value)
	{
		return new Filter(Type.ILIKE,attribute,value);
	}
	/**
	 * create a filter for a 'like' where clause
	 *
	 * the attribute must be a string
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value the value to be compared
	 * @return a Like filter
	 */
	public static Filter getNewLikeFilter(String attribute,String value)
	{
		return new Filter(Type.LIKE,attribute,value);
	}

	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for BETWEEN filter
	 * 
	 * @param type filter type
	 * @param attribute filtered attribute
	 * @param value1  first value
	 * @param value2  second value
	 */
	private Filter(Type type,String attribute,Object value1, Object value2)
	{
		this();
		if (!type.equals(Type.BETWEEN))
		{
			throw new IllegalArgumentException("This constructor accept only BETWEEN type");
		}
		if (value1 == null || value2 == null) return; // TODO exception ???
		
		this.type = type;
		this.familly = TypeFamilly.TERMINAL;
		this.attribute = attribute;
		this.firstValue = value1;
		this.secondValue = value2;
		if (! (value1 instanceof Number) && !(value1 instanceof Date))
		{
			throw new IllegalArgumentException("BETWEEN accept only numeric or date types");
		}
		if (! (value2 instanceof Number) && !(value2 instanceof Date))
		{
			throw new IllegalArgumentException("BETWEEN accept only numeric or date types");
		}
		if (!value1.getClass().equals(value2.getClass()))
		{
			throw new IllegalArgumentException("BETWEEN accept only identical value types");
		}

	}

	/**
	 * create a filter for a 'between' where clause
	 *
	 * the attribute must be comparable
	 *
	 * @param attribute the bean attribute name concerned
	 * @param value1 the lower value to be compared
	 * @param value2 the upper value to be compared
	 * @return a Between filter
	 */
	public static Filter getNewBetweenFilter(String attribute,Object value1, Object value2)
	{
		return new Filter(Type.BETWEEN,attribute,value1,value2);
	}

	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for IN filter
	 * 
	 * @param type filter type
	 * @param attribute filtered attribute
	 * @param valueArray list of required values
	 */
	private Filter(Type type,String attribute,Object[] valueArray)
	{
		this();
		if (valueArray == null || valueArray.length == 0) return;
		if (type.equals(Type.IN))
		{
			this.type = type;
			this.familly = TypeFamilly.TERMINAL;
			this.attribute = attribute;
			this.valueArray = valueArray;
		}
		else
		{
			throw new IllegalArgumentException("This constructor accept only IN type");
		}
	}

	/**
	 * create a filter for a 'in' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @param valueArray the array of possible values
	 * @return an In filter
	 */
	public static Filter getNewInFilter(String attribute,Object[] valueArray)
	{
		return new Filter(Type.IN,attribute,valueArray);
	}
	/**
	 * create a filter for a 'in' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @param valueList the list of possible values
	 * @return an In filter
	 */
	public static Filter getNewInFilter(String attribute,Collection<?> valueList)
	{
		return new Filter(Type.IN,attribute,valueList.toArray());
	}

	/**
	 * create a filter for a 'in' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @param sqlClause a sub select of sql syntax
	 * @return an In filter
	 */
	public static Filter getNewInFilter(String attribute,String sqlClause)
	{
		return new Filter(Type.IN,attribute,sqlClause);
	}


	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for OR and AND filters
     * 
	 * @param type filter type
	 * @param first first sub filter
	 * @param second second sub filter
	 */
	private Filter(Type type,Filter... filters)
	{
	    this();	
		if (type.ordinal() < Type.AND.ordinal() || type.ordinal() > Type.OR.ordinal())
		{
			throw new IllegalArgumentException("This constructor accept only AND and OR types");
		}
		List<Filter> realFilters = new ArrayList<Filter>();
		for (Filter filter : filters) 
		{
		   if (filter == null) continue;
		   if (!filter.getFamilly().equals(TypeFamilly.EMPTY))	
		   {
			   realFilters.add(filter);
		   }
		}
		for (Filter filter : filters) 
		{
         if (filter == null) continue;
			if (filter.orderList != null) addAllOrder(filter.orderList);
			this.limit = Math.max(this.limit,filter.limit);
			this.start = Math.min(this.start, filter.start);
		}
		if (realFilters.size() == 0) return; // no usefull filters create empty filter 
		if (realFilters.size() ==  1) 
		{
		   // just clone filter
          Filter copy = realFilters.get(0);
          this.attribute = copy.attribute;
          this.combinedFilters = copy.combinedFilters;
          this.familly = copy.familly;
          this.firstValue = copy.firstValue;
          this.limit = copy.limit;
          this.orderList = copy.orderList;
          this.secondValue = copy.secondValue;
          this.start = copy.start;
          this.type = copy.type;
          this.valueArray = copy.valueArray;
          
		}
		else
		{
		this.type = type;
		this.familly = TypeFamilly.COMBINED;
		this.combinedFilters = realFilters.toArray(new Filter[0]);
		}
	}
	/**
	 * create a combined filter for a 'and' where clause
	 *
	 * @param first the left side criteria
	 * @param second the right side criteria
	 * @return an And filter
	 */
	public static Filter getNewAndFilter(Filter...filters)
	{
		return new Filter(Type.AND,filters);
	}
	/**
	 * create a combined filter for a 'OR' where clause
	 *
	 * @param first the left side criteria
	 * @param second the right side criteria
	 * @return an Or filter
	 */
	public static Filter getNewOrFilter(Filter...filters)
	{
		return new Filter(Type.OR,filters);
	}


	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * internal use for NOT filter
	 * 
	 * @param type filter type
	 * @param first sub filter
	 */
	private Filter(Type type,Filter first)
	{
		this();
		if (first.getFamilly().equals(TypeFamilly.EMPTY)) return;
		if (type.equals(Type.NOT))
		{
			this.type = type;
			this.familly = TypeFamilly.COMBINED;
			this.combinedFilters = new Filter[] {first};
			if (first.orderList != null) this.addAllOrder(first.orderList);
			this.limit = first.limit;
		}
		else
		{
			throw new IllegalArgumentException("This constructor accept only NOT type");
		}
	}
	/**
	 * create a combined filter for a 'NOT' where clause
	 *
	 * @param first the criteria to negate
	 * @return a Not filter
	 */
	public static Filter getNewNotFilter(Filter first)
	{
		return new Filter(Type.NOT,first);
	}

	/**
	 * private constructor, must use the static methods getNew<i>Type</i>Filter(...)
	 * <p/>
	 * 
	 * internal use for IS_NULL and SQL_WHERE filter
	 * 
	 * @param type filter type 
	 * @param attribute attribute or sql clause
	 */
	private Filter(Type type,String attribute)
	{
		if (type.equals(Type.IS_NULL) || type.equals(Type.SQL_WHERE) )
		{
			this.type = type;
			this.familly = TypeFamilly.TERMINAL;
			this.attribute = attribute;
		}
		else
		{
			throw new IllegalArgumentException("This constructor accept only IS_NULL and SQL_WHERE types");
		}
	}
	/**
	 * create a filter for a 'IS_NULL' where clause
	 *
	 * @param attribute the bean attribute name concerned
	 * @return a Is Null filter
	 */
	public static Filter getNewIsNullFilter(String attribute)
	{
		return new Filter(Type.IS_NULL,attribute);
	}

	/**
	 * create a SQL LITERAL criteria for a where clause
	 *
	 * @param sqlQuery the literal query (must conform mapping property files)
	 * @return a SQL Literal filter
	 */
	public static Filter getNewSqlWhereFilter(String sqlQuery)
	{
		return new Filter(Type.SQL_WHERE,sqlQuery);
	}

	/**
	 * add an order clause for the request
	 *
	 * @param order
	 */
	public void addOrder(FilterOrder order)
	{
		if (this.orderList == null)
		{
			this.orderList = new ArrayList<FilterOrder>();
		}
		this.orderList.add(order);
	}

	/**
	 * add an ordered list of order clauses
	 *
	 * @param order
	 */
	public void addAllOrder(List<FilterOrder> order)
	{
		if (this.orderList == null)
		{
			this.orderList = new ArrayList<FilterOrder>();
		}
		this.orderList.addAll(order);
	}


	/**
	 * create an empty filter (used when only order clauses or limit are needed)
	 *
	 * @return an Empty filter
	 */
	public static Filter getNewEmptyFilter()
	{
		return new Filter();
	}

	/**
	 * add a start restriction
	 *
	 * @param startRestriction
	 */
	public void addStart(int startRestriction)
	{
		if (startRestriction < 0) throw new IllegalArgumentException("startRestriction can not be negative");
		this.start  = startRestriction;

	}
	/**
	 * add a limit restriction
	 *
	 * @param limitRestriction
	 */
	public void addLimit(int limitRestriction)
	{
		if (limitRestriction < 0) throw new IllegalArgumentException("limitRestriction can not be negative");
		this.limit  = limitRestriction;

	}

	/**
	 * indicate that this filter contains no effective criteria clause
	 *
	 * @return true if the filter is of EMPTY type
	 */
	public boolean isEmpty()
	{
		return (this.type == null);
	}
}
