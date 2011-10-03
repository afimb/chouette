package fr.certu.chouette.struts.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.sql.Date;

import org.apache.log4j.Logger;
import org.displaytag.model.Cell;

public class DateComparator implements Comparator<Cell>
{
   private static final Logger log = Logger.getLogger(DateComparator.class);
   private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

   public int compare(Cell cell1, Cell cell2)
   {
      Date date1 = new Date(new java.util.Date().getTime());
      Date date2 = new Date(new java.util.Date().getTime());
      try
      {
         String cell1Value = cell1.getStaticValue().toString().trim();
         if (!cell1Value.equals(""))
            date1 = new Date(sdf.parse(cell1Value).getTime());

         String cell2Value = cell2.getStaticValue().toString().trim();
         if (!cell2Value.equals(""))
            date2 = new Date(sdf.parse(cell2Value).getTime());
      }
      catch (ParseException e)
      {
         log.error("STATIC VALUE = \"" + cell1.getStaticValue().toString().trim() + "\"\n"+
                   "STATIC VALUE = \"" + cell2.getStaticValue().toString().trim() + "\"",e);
      }
      return date1.compareTo(date2);
   }
}
