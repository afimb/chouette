package fr.certu.chouette.exchange.gtfs.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public abstract class GtfsBean
{

   public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

   @Getter
   @Setter
   int fileLineNumber = 0;

   @Getter
   List<String> missingData = new ArrayList<String>();

   public abstract String getCSVLine();

   public abstract boolean isValid();

   protected void addMissingData(String data)
   {
      missingData.add(data);
   }

   protected String toCSVString(String input)
   {
      if (input == null)
         return "";
      if (input.contains("\"") || input.contains(","))
      {
         StringBuilder builder = new StringBuilder();
         builder.append('"');
         for (char c : input.toCharArray())
         {
            if (c == '"')
            {
               builder.append('"');
            }
            builder.append(c);
         }
         builder.append('"');
         return builder.toString();
      }
      return input;
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      return super.equals(obj);
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return super.hashCode();
   }

}
