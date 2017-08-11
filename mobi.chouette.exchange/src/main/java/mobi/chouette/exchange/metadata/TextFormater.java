/**
 * 
 */
package mobi.chouette.exchange.metadata;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import mobi.chouette.exchange.metadata.Metadata.Box;
import mobi.chouette.exchange.metadata.Metadata.Period;
import mobi.chouette.exchange.metadata.Metadata.Resource;

import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author michel
 *
 */
public class TextFormater implements Formater
{

   private static DecimalFormat doubleFormat = new DecimalFormat("#.000",DecimalFormatSymbols.getInstance(Locale.US));
   private static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
   /* (non-Javadoc)
    * @see fr.certu.chouette.export.metadata.writer.Formater#format(fr.certu.chouette.export.metadata.model.Metadata.Period)
    */
   @Override
   public synchronized String format(Period period)
   {
      return "du "+dateFormat.print(period.getStart())+
            " au "+dateFormat.print(period.getEnd());
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.export.metadata.writer.Formater#format(fr.certu.chouette.export.metadata.model.Metadata.Box)
    */
   @Override
   public synchronized String format(Box box)
   {
      return " nord "+doubleFormat.format(box.getNorthLimit())+
            ", sud "+doubleFormat.format(box.getSouthLimit())+
            ", ouest "+doubleFormat.format(box.getWestLimit())+
            ", est "+doubleFormat.format(box.getEastLimit());
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.export.metadata.writer.Formater#format(java.util.List)
    */
   @Override
   public String format(Resource resource)
   {
      StringBuilder builder = new StringBuilder();
      if (resource.getFileName() != null)
      {
         builder.append(resource.getFileName()+" : ");
      }
      if (resource.getNetworkName() != null)
      {
         builder.append("réseau "+resource.getNetworkName()+", ");
      }
      builder.append("ligne "+resource.getLineName());

      return builder.toString();
   }

   /* (non-Javadoc)
    * @see fr.certu.chouette.export.metadata.writer.Formater#formatDate(java.util.Calendar)
    */
   @Override
   public synchronized String formatDate(ReadablePartial date)
   {
      return dateFormat.print(date);
   }


}
