package fr.certu.chouette.dao.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class FilterToHibernateHqlTranslator {

   //private static final Logger logger = Logger.getLogger(FilterToHibernateClauseTranslator.class);
   private HashSet<String> aliasses;

   @Getter private List<Object> values = new ArrayList<Object>();

   private SessionFactory factory;

   public FilterToHibernateHqlTranslator(SessionFactory factory)
   {
      this.factory = factory;
   }

   /**
    * @param clause
    * @return
    */
   public  String translateToHQLSelect(Class<? extends NeptuneIdentifiedObject> baseClass, Filter clause,List<Object> values)
   {
      if (clause == null) throw new NullPointerException("JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");
      ClassMetadata metaData = factory.getClassMetadata(baseClass);
      String name = metaData.getEntityName();
      String retVal = "select distinct b0 from "+name+" as b0";
      Map<String,String> mapJoinBeans = new HashMap<String, String>();

      // add join on from
      retVal += join(metaData,clause,mapJoinBeans);
      switch (clause.getFamilly())
      {
      case EMPTY : 
         break;  // pas de filtre, uniquement de l'ordre et limite
      default : 
         retVal += " where "+translate(metaData,clause,mapJoinBeans);
      }

      return retVal; // 
   }

   private String translate(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {

      return "";
   }

   private String join(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {
      String retVal = "";
      switch (clause.getFamilly())
      {
      case EMPTY : 
         break;  // pas de jointure a ajouter
      case TERMINAL : 
         retVal += joinTerminal(metaData,clause,mapJoinBeans);
         break;
      case COMBINED : 
         retVal += joinCombined(metaData,clause,mapJoinBeans);
      }
      return retVal;
   }

   private String joinTerminal(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {
      ClassMetadata localMetaData = metaData;
      String retVal = "";
      String propertyName = clause.getAttribute();
      // if (propertyName.contains("."))
      {
         // manage collections
         String[] items = propertyName.split("\\.");

         String parentKey = "b0";

         for (String item : items)
         {
            // check association 
            Type type = localMetaData.getPropertyType(item);
            System.out.println(type.isCollectionType());
            if (type.isCollectionType())
            {
               if (!mapJoinBeans.containsValue(item))
               {
                  String key = "b"+Integer.toString(mapJoinBeans.size()+1);
                  mapJoinBeans.put(key, item);
                  retVal +=" left join "+parentKey+"."+item+" as "+key; 
                  parentKey = key;
               }
               else
               {
                  for (Entry<String,String> entry : mapJoinBeans.entrySet())
                  {
                     if (entry.getValue().equals(item))
                     {
                        parentKey = entry.getKey();
                        break;
                     }
                  }
               }
            }
         }

      }
      return retVal;
   }

   private String joinCombined(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {
      String retVal = "";
      Filter[] filters = clause.getCombinedFilters();
      for (Filter filter : filters)
      {
         retVal+=join(metaData,filter,mapJoinBeans);
      }
      return retVal;
   }

   private String translateTerminal(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {
      String propertyName = clause.getAttribute();

      switch (clause.getType())
      {
      case IS_NULL : 
         return null;
      case EQUALS : 
         return null;
      case NOT_EQUALS : 
         return null;
      case LESS : 
         return null;
      case LESS_OR_EQUALS : 
         return null;
      case GREATER : 
         return null;
      case GREATER_OR_EQUALS : 
         return null;
      case LIKE : 
         return null;
      case ILIKE : 
         return null;
      case IN : 
         return null;
      case BETWEEN : 
         return null;
      case SQL_WHERE : 
         return null;
      }
      return null;
   }

   private String translateCombined(ClassMetadata metaData, Filter clause, Map<String, String> mapJoinBeans)
   {
      Filter[] filters = clause.getCombinedFilters();
      switch (clause.getType())
      {
      case NOT : return null;
      case AND : 

      {
         if (filters.length == 1) return null;

         return null;
      }
      case OR : 
      {
         if (filters.length == 1) return null;

         return null;
      }
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
