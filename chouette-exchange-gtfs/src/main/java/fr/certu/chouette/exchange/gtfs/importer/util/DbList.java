package fr.certu.chouette.exchange.gtfs.importer.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import fr.certu.chouette.exchange.gtfs.model.GtfsBean;
import fr.certu.chouette.exchange.gtfs.model.factory.GtfsBeanFactory;

public class DbList<E extends GtfsBean> implements List<E>
{
   private static final int BATCH_SIZE = 5000;
	
   private int size = 0;

   private GtfsBeanFactory<E> factory ; 

   private Connection conn;

   private ArrayList<E> buffer = new ArrayList<E>();

   private Map<String,E> map ;

   private Map<String,List<E>> parentMap ;

   private boolean optimizeMemory = true;
   /**
    * 
    */
   public DbList(Connection conn, GtfsBeanFactory<E> factory,boolean optimize)
   {
      this.factory = factory;
      this.conn = conn;
      this.optimizeMemory = optimize;
      if (!optimizeMemory)
      {
         map = new HashMap<String, E>();
         parentMap = new HashMap<String, List<E>>();
      }
   }

   @Override
   public int size()
   {
      return size;
   }

   @Override
   public boolean isEmpty()
   {
      return size == 0;
   }

   @Override
   public boolean contains(Object o)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Iterator<E> iterator()
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public Object[] toArray()
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public <T> T[] toArray(T[] a)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public boolean add(E e)
   {
      buffer.add(e);
      size++;
      if (optimizeMemory)
      {
         if (buffer.size() == BATCH_SIZE)
         {
            flush();
         }
      }
      else
      {
         String id = factory.getId(e);
         if (id != null)
            map.put(id, e);
         String parentId = factory.getParentId(e);
         if (parentId != null)
         {
            List<E> list = parentMap.get(parentId);
            if (list == null)
            {
               list = new ArrayList<E>();
               parentMap.put(parentId, list);
            }
            list.add(e);
         }
      }
      return true;
   }

   public void flush()
   {
      if (optimizeMemory && buffer.size() > 0 )
      {
         factory.saveAll(conn, buffer);
         buffer.clear();
      }
   }

   @Override
   public boolean remove(Object o)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean containsAll(Collection<?> c)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public boolean addAll(Collection<? extends E> c)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public boolean addAll(int index, Collection<? extends E> c)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public boolean removeAll(Collection<?> c)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public boolean retainAll(Collection<?> c)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public void clear()
   {
      buffer.clear();
      size = 0;
      if (optimizeMemory)
      {
         Statement stmt;
         try
         {
            stmt = conn.createStatement();
            factory.initDb(stmt);
            stmt.close();
         }
         catch (SQLException e)
         {
            // TODO Auto-generated catch block
         }
      }
      else
      {
         map.clear();
      }

   }

   public E get(String id)
   {
      if (optimizeMemory)
      {
         return factory.get(conn, id);
      }
      else
      {
         return map.get(id);
      }
   }

   public List<E> getAllFromParent(String parentId)
   {
      if (optimizeMemory)
      {
         return factory.getAllFromParent(conn, parentId);
      }
      List<E> list = parentMap.get(parentId);
      if (list != null) return list;
      return new ArrayList<E>();
   }

   public List<E> getAll()
   {
      if (optimizeMemory)
      {
         return factory.getAll(conn);
      }
      return buffer;
   }


   @Override
   public E get(int index)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public E set(int index, E element)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public void add(int index, E element)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public E remove(int index)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public int indexOf(Object o)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public int lastIndexOf(Object o)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public ListIterator<E> listIterator()
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public ListIterator<E> listIterator(int index)
   {
      throw new RuntimeException("not implemented");
   }

   @Override
   public List<E> subList(int fromIndex, int toIndex)
   {
      throw new RuntimeException("not implemented");
   }

}
