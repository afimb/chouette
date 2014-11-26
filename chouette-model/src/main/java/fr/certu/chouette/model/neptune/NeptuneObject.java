/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.model.neptune;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * basic class for every Neptune object
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class NeptuneObject implements Serializable
{


   private static final long serialVersionUID = -1406542019260386319L;
   protected static final String CHILD_INDENT = "        ";
   protected static final String CHILD_ARROW = "  -->";
   protected static final String CHILD_LIST_INDENT = "           ";
   protected static final String CHILD_LIST_ARROW = "      -->";


   /** 
    * database id <br/>
    * null if not saved
    * 
    * @param id New value
    * @return  The actual value
    */
   @Getter
   @Setter
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "id", nullable = false)
   private Long id;

   @Transient
   private boolean validationProceeded = false;

   /**
    * read annotation to get maximum size of database dfield
    * 
    * @param fieldName field name 
    * @return size
    * @throws NoSuchFieldException
    */
   private int getfieldSize(String fieldName) throws NoSuchFieldException
   {
      Class<? extends Object> c = getClass();
      Field f = null;
      while (f == null)
      {
            try
            {
               f = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e)
            {
               if (c == Object.class) throw e;
               c = c.getSuperclass();
               
            } 
      }
      return f.getAnnotation(Column.class).length();
   }
   
   /**
    * truncate string value for database suitability
    * 
    * @param value value to set
    * @param fieldName field name
    * @param log log if truncated
    * @return truncated or entire value
    */
   protected String dataBaseSizeProtectedValue(String value,String fieldName, Logger log)
   {
      if (value != null)
      {
         try 
         {
            int size = getfieldSize(fieldName);
            int inLength = value.length();
            if (inLength > size)
            {
               log.warn(fieldName+" length > "+size+", truncated " + value);
               value = value.substring(0, size);
            }
         } 
         catch (NoSuchFieldException ex) {} 
         catch (SecurityException ex) {}
      } 
      return value;
      
   }
   /**
    * Build a list of internal Ids (Id) from a list of Neptune Objects
    * 
    * @param neptuneObjects
    *           the list to parse
    * @return the ids list
    */
   public static List<Long> extractIds(
         List<? extends NeptuneObject> neptuneObjects)
   {
      List<Long> ids = new ArrayList<Long>();
      if (neptuneObjects != null)
      {
         for (NeptuneObject neptuneObject : neptuneObjects)
         {
            if (neptuneObject != null)
            {
               Long id = neptuneObject.getId();
               if (id != null)
               {
                  ids.add(id);
               }
            }
         }
      }

      return ids;
   }

   /**
    * Build a map of internal Ids (Id) from a list of Neptune Objects
    * 
    * @param neptuneObjects
    *           the list to parse
    * @return the ids map
    */
   public static <T extends NeptuneObject> Map<Long, T> mapOnIds(
         List<T> neptuneObjects)
   {
      Map<Long, T> map = new HashMap<Long, T>();
      if (neptuneObjects != null)
      {
         for (T neptuneObject : neptuneObjects)
         {
            if (neptuneObject != null)
            {
               Long id = neptuneObject.getId();
               if (id != null)
               {
                  map.put(id, neptuneObject);
               }
            }
         }
      }
      return map;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public final String toString()
   {
      return toString("", 0);
   }

   /**
    * pretty ToString()
    * 
    * @param indent
    *           indentation
    * @param level
    *           deep level to print
    * @return
    */
   public String toString(String indent, int level)
   {
      return getClass().getSimpleName() + "( id=" + id + ") ";
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (!(obj instanceof NeptuneObject))
         return false;
      NeptuneObject another = (NeptuneObject) obj;
      if (id != null)
         return id.equals(another.getId());
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      if (id != null)
         return id.hashCode();
      return super.hashCode();
   }

   /**
    * check if validation can check this object
    * 
    * @return true at first check and false after
    */
   public final boolean checkValidationProcess()
   {
      boolean check = !validationProceeded;
      validationProceeded = true;
      return check;
   }

   /**
    * compare 2 Neptune Object for identical attributs
    * 
    * @param another
    * @return
    */
   public abstract <T extends NeptuneObject> boolean compareAttributes(T another);

   /**
    * compare attribute values even if null
    * 
    * @param first
    * @param second
    * @return true if values are identical
    */
   protected boolean sameValue(Object first, Object second)
   {
      if (first == null)
         return second == null;
      return first.equals(second);
   }

   /**
    * compare int attribute values
    * 
    * @param first
    * @param second
    * @return if values are identical
    */
   protected boolean sameValue(int first, int second)
   {
      return first == second;
   }

   /**
    * compare int attribute values
    * 
    * @param first
    * @param second
    * @return if values are identical
    */
   protected boolean sameValues(List<?> first, List<?> second)
   {
      if (first == null || first.isEmpty())
         return second == null || second.isEmpty();
      return (first.containsAll(second) && second.containsAll(first));
   }

}
