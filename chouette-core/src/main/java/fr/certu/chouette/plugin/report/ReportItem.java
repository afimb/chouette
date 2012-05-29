/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */

package fr.certu.chouette.plugin.report;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public abstract  class ReportItem extends Report implements Comparable<ReportItem>
{
   @Getter @Setter private String messageKey;
   @Getter @Setter private List<Object> messageArgs = new ArrayList<Object>();
   @Getter @Setter private int order;
   public void addMessageArgs(Object... args)
   {
      for (Object arg : args) 
      {
         messageArgs.add(arg);
      }

   }	

   /**
    * @param locale
    * @return
    */
   public final String getLocalizedMessage(Locale locale)
   {
      String format = "";
      String message = "";
      try
      {
         ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName(),locale);
         format = bundle.getString(getMessageKey());
         message = MessageFormat.format(format,messageArgs.toArray());
      }
      catch (MissingResourceException e1)
      {
         try
         {
            ResourceBundle bundle = ResourceBundle.getBundle(this.getClass().getName());
            format = bundle.getString(getMessageKey());
            message = MessageFormat.format(format,messageArgs.toArray());
         }
         catch (MissingResourceException e2)
         {
            message = getMessageKey(); 
            if (messageArgs != null) message += " : "+Arrays.toString(messageArgs.toArray());
         }
      }


      return message;
   }


   @Override
   public int compareTo(ReportItem item) 
   {
      return order-item.order;
   }

   public String toJSON(String indent,boolean last)
   {
      StringBuilder builder = new StringBuilder();

      builder.append(indent+"{\n");
      builder.append(indent+"  \"messagekey\":\"");
      builder.append(getMessageKey());
      builder.append("\",\n"+indent+"  \"message\":");
      builder.append(getLocalizedMessage());
      builder.append("\",\n"+indent+"  \"status\":");
      builder.append(getStatus());
      builder.append("\"");
      builder.append("\",\n"+indent+"  \"order\":");
      builder.append(getOrder());
      builder.append("\"");
      List<ReportItem> subitems = getItems();
      List<Object> args = getMessageArgs();
      if (args != null && !args.isEmpty())
      {
         builder.append(",\n"+indent+"  \"args\":[\n");
         for (int i = 0; i < args.size(); i++)
         {

            builder.append("\""+args.get(i)+"\"");
            if (i < args.size() -1) builder.append(",");
         }
         if (subitems != null && !subitems.isEmpty())
         {
            builder.append(indent+"  ],\n");
         }
         else
         {
            builder.append(indent+"  ]\n");

         }
      }
      if (subitems != null && !subitems.isEmpty())
      {
         builder.append(",\n"+indent+"  \"items\":[\n");
         for (int i = 0; i < subitems.size(); i++)
         {

            builder.append(subitems.get(i).toJSON(indent+"    ",i == subitems.size()-1));
         }
         builder.append(indent+"  ]\n");
      }

      builder.append("\n"+indent+"}");
      if (!last) builder.append(",");
      builder.append("\n");

      return builder.toString();
   }

   public String toJSON()
   {
      return toJSON("",false);
   }
   
}
