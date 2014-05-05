package fr.certu.chouette.dao.hibernate;

import java.util.StringTokenizer;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.NeptuneObject;

public class FilterToHibernateClauseTranslator<T extends NeptuneObject>
{

   public Predicate translate(Filter clause, CriteriaBuilder builder, Root<T> root)
   {
      if (clause == null)
      {

         NullPointerException e = new NullPointerException(
               "JE VIENS DE RENCONTRER UNE CLAUSE NON INITIALISEE .. J'ARRETE TOUT TRAITEMENT");
         e.printStackTrace();
         throw e;
      }

      switch (clause.getFamilly())
      {
      case EMPTY:
         return null; // pas de filtre, uniquement de l'ordre et limite
      case TERMINAL:
         return translateTerminal(clause, builder, root);
      case COMBINED:
         return translateCombined(clause, builder, root);
      }
      return null; //
   }

   private Predicate translateCombined(Filter clause, CriteriaBuilder builder, Root<T> root)
   {
      Filter[] filters = clause.getCombinedFilters();
      switch (clause.getType())
      {
      case NOT:
         return builder.not(translate(filters[0], builder, root));
      case AND:

      {
         if (filters.length == 1)
            return translate(filters[0], builder, root);
         Predicate criterion = builder.and(translate(filters[0], builder, root),
               translate(filters[1], builder, root));
         for (int i = 2; i < filters.length; i++)
         {
            criterion = builder.and(criterion, translate(filters[i], builder, root));
         }
         return criterion;
      }
      case OR:
      {
         if (filters.length == 1)
            return translate(filters[0], builder, root);
         Predicate criterion = builder.or(translate(filters[0], builder, root),
               translate(filters[1], builder, root));
         for (int i = 2; i < filters.length; i++)
         {
            criterion = builder.or(criterion, translate(filters[i], builder, root));
         }
         return criterion;
      }
      default:
         return null; // TODO : throw exception
      }
   }

   private Predicate translateTerminal(Filter clause, CriteriaBuilder builder, Root<T> root)
   {
      String propertyName = clause.getAttribute();

      String[] tokens = propertyName.split("[.]");
      Path path = root;
      if (tokens.length > 1)
      {
         for (int i = 0; i < (tokens.length - 1); i++)
         {
            path = path.get(tokens[i]);
         }
      }
      String name = tokens[tokens.length - 1];

      Expression<Comparable> field = path.get(name);

      switch (clause.getType())
      {
      case IS_NULL:
         return builder.isNull(field);
      case EQUALS:
         return builder.equal(field, clause.getFirstValue());
      case NOT_EQUALS:
         return builder.notEqual(field, clause.getFirstValue());
      case LESS:
         return builder.lessThan(field, (Comparable) clause.getFirstValue());
      case LESS_OR_EQUALS:
         return builder.lessThanOrEqualTo(field, (Comparable) clause.getFirstValue());
      case GREATER:
         return builder.greaterThan(field, (Comparable) clause.getFirstValue());
      case GREATER_OR_EQUALS:
         return builder.greaterThanOrEqualTo(field, (Comparable) clause.getFirstValue());
      case LIKE:
         Expression<String> like = path.get(name);
         return builder.like(like, getILikeOrLikeRestrictionValue(clause.getFirstValue()));
      case ILIKE:
         Expression<String> ilike = path.get(name);
         return builder.like(
               builder.lower(ilike), getILikeOrLikeRestrictionValue(((String) clause.getFirstValue()).toLowerCase())
               );
      case IN:
    	  Expression in = path.get(name);

          if (clause.getFirstValue() != null)
          {
             // sous requête non implémentée dans hibernate
          }
          else if (clause.getValueArray() != null)
          {
             return in.in(clause.getValueArray());
          }
    	  return null;
      case BETWEEN:
         return builder.between(field, (Comparable) clause.getFirstValue(), (Comparable) clause.getSecondValue());
      case SQL_WHERE:
         // return Restrictions.sqlRestriction(root.get(propertyName));
      default:
         return null;
      }

   }

   private String getILikeOrLikeRestrictionValue(Object value)
   {
      String ret = value.toString();
      if (!ret.contains("%"))
         ret = "%" + ret + "%";
      return ret;
   }

}
