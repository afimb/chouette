/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

/**
 * SQL criteria builder
 *
 */
public class Filter
{
  public static enum Type
   {EQUALS,
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
  
    public enum TypeFamilly 
    {
       EMPTY,
       TERMINAL,
       COMBINED
    }

    @Getter private Type type;
    @Getter private TypeFamilly familly;
    @Getter private String attribute;
    @Getter private Object firstValue;
    @Getter private Object secondValue;
    @Getter private Object[] valueArray;
    @Getter private Filter firstCombinedFilter = null;
    @Getter private Filter secondCombinedFilter = null;
    @Getter private List<FilterOrder> orderList = null;

    @Getter private int limit = 0;

    private Filter()
    {
      this.type = null;
      this.familly = TypeFamilly.EMPTY;
    }

    private Filter(Type type,String attribute,Object value)
    {
      if (type.ordinal() > Type.IN.ordinal())
      {
        throw new IllegalArgumentException("This constructor accept only EQUALS, NOT_EQUALS, LESS, LESS_OR_EQUALS, GREATER, GREATER_OR_EQUALS, LIKE , ILIKE and IN types");
      }
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
     * create a criteria for an 'equals' where clause
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewEqualsFilter(String attribute,Object value)
    {
      return new Filter(Type.EQUALS,attribute,value);
    }
    /**
     * create a criteria for a 'not equals' where clause
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewNotEqualsFilter(String attribute,Object value)
    {
      return new Filter(Type.NOT_EQUALS,attribute,value);
    }
    /**
     * create a criteria for a 'lesser than' where clause
     *
     * the attribute must be comparable
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewLessFilter(String attribute,Object value)
    {
      return new Filter(Type.LESS,attribute,value);
    }
    /**
     * create a criteria for a 'lesser or equals' where clause
     *
     * the attribute must be comparable
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewLessOrEqualsFilter(String attribute,Object value)
    {
      return new Filter(Type.LESS_OR_EQUALS,attribute,value);
    }
    /**
     * create a criteria for a 'greater than' where clause
     *
     * the attribute must be comparable
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewGreaterFilter(String attribute,Object value)
    {
      return new Filter(Type.GREATER,attribute,value);
    }
    /**
     * create a criteria for a 'greater or equals' where clause
     *
     * the attribute must be comparable
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewGreaterOrEqualsFilter(String attribute,Object value)
    {
      return new Filter(Type.GREATER_OR_EQUALS,attribute,value);
    }
    /**
     * create a criteria for a 'ilike' where clause
     *
     * the attribute must be a string
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewIgnoreCaseLikeFilter(String attribute,String value)
    {
      return new Filter(Type.ILIKE,attribute,value);
    }
    /**
     * create a criteria for a 'like' where clause
     *
     * the attribute must be a string
     *
     * @param attribute the bean attribute name concerned
     * @param value the value to be compared
     * @return
     */
    public static Filter getNewLikeFilter(String attribute,String value)
    {
      return new Filter(Type.LIKE,attribute,value);
    }

    private Filter(Type type,String attribute,Object value1, Object value2)
    {
      if (!type.equals(Type.BETWEEN))
      {
        throw new IllegalArgumentException("This constructor accept only BETWEEN type");
      }
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
     * create a criteria for a 'between' where clause
     *
     * the attribute must be comparable
     *
     * @param attribute the bean attribute name concerned
     * @param value1 the lower value to be compared
     * @param value2 the upper value to be compared
     * @return
     */
    public static Filter getNewBetweenFilter(String attribute,Object value1, Object value2)
    {
      return new Filter(Type.BETWEEN,attribute,value1,value2);
    }

    public Filter(Type type,String attribute,Object[] valueArray)
    {
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
     * create a criteria for a 'in' where clause
     *
     * @param attribute the bean attribute name concerned
     * @param valueArray the array of possible values
     * @return
     */
    public static Filter getNewInFilter(String attribute,Object[] valueArray)
    {
      return new Filter(Type.IN,attribute,valueArray);
    }
    /**
     * create a criteria for a 'in' where clause
     *
     * @param attribute the bean attribute name concerned
     * @param valueList the list of possible values
     * @return
     */
    public static Filter getNewInFilter(String attribute,List<?> valueList)
    {
      return new Filter(Type.IN,attribute,valueList.toArray());
    }

    /**
     * create a criteria for a 'in' where clause
     *
     * @param attribute the bean attribute name concerned
     * @param sqlClause a subselect in sql syntax
     * @return
     */
    public static Filter getNewInFilter(String attribute,String sqlClause)
    {
      return new Filter(Type.IN,attribute,sqlClause);
    }


    private Filter(Type type,Filter first,Filter second)
    {
      if (type.ordinal() < Type.AND.ordinal() || type.ordinal() > Type.OR.ordinal())
      {
        throw new IllegalArgumentException("This constructor accept only AND and OR types");
      }
      this.type = type;
      this.familly = TypeFamilly.COMBINED;
      this.firstCombinedFilter = first;
      this.secondCombinedFilter = second;
      if (first.orderList != null) addAllOrder(first.orderList);
      if (second.orderList != null) addAllOrder(second.orderList);
      this.limit = Math.max(first.limit,second.limit);
    }
    /**
     * create a combined criteria for a 'and' where clause
     *
     * @param first the left side criteria
     * @param second the right side criteria
     * @return
     */
    public static Filter getNewAndFilter(Filter first,Filter second)
    {
      return new Filter(Type.AND,first,second);
    }
    /**
     * create a combined criteria for a 'OR' where clause
     *
     * @param first the left side criteria
     * @param second the right side criteria
     * @return
     */
    public static Filter getNewOrFilter(Filter first,Filter second)
    {
      return new Filter(Type.OR,first,second);
    }


    private Filter(Type type,Filter first)
    {
      if (type.equals(Type.NOT))
      {
        this.type = type;
        this.familly = TypeFamilly.COMBINED;
        this.firstCombinedFilter = first;
        if (first.orderList != null) this.addAllOrder(first.orderList);
        this.limit = first.limit;
      }
      else
      {
        throw new IllegalArgumentException("This constructor accept only NOT type");
      }
    }
    /**
     * create a combined criteria for a 'NOT' where clause
     *
     * @param first the criteria to negate
     * @return
     */
    public static Filter getNewNotFilter(Filter first)
    {
      return new Filter(Type.NOT,first);
    }

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
     * create a criteria for a 'IS_NULL' where clause
     *
     * @param attribute the bean attribute name concerned
     * @return
     */
    public static Filter getNewIsNullFilter(String attribute)
    {
      return new Filter(Type.IS_NULL,attribute);
    }

    /**
     * create a SQL LITERAL criteria for a where clause
     *
     * @param sqlQuery the literal query (must conform mapping property files)
     * @return
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
     * create an empty criteria (used when only order clauses or limit are needed)
     *
     * @return
     */
    public static Filter getNewEmptyFilter()
    {
      return new Filter();
    }

    /**
     * add a limit restriction
     *
     * @param limitRestriction
     */
    public void addLimit(int limitRestriction)
    {
      this.limit  = limitRestriction;

    }

    /**
     * indicate that this criteria contains no effective criteria clause
     *
     * @return
     */
    public boolean isEmpty()
    {
      return (this.type == null);
    }
}
