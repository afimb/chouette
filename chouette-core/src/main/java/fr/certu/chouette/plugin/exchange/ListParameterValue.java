/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;

import lombok.Getter;
import lombok.Setter;

public class ListParameterValue extends ParameterValue
{

   @Getter
   @Setter
   private List<Long> integerList;
   @Getter
   @Setter
   private List<Boolean> booleanList;
   @Getter
   @Setter
   private List<Calendar> dateList;
   @Getter
   @Setter
   private List<String> stringList;
   @Getter
   @Setter
   private List<String> filenameList;
   @Getter
   @Setter
   private List<String> filepathList;

   public ListParameterValue(String name)
   {
      super(name);
   }

   public void fillFilepathList(JSONArray vals)
   {
      filepathList = toStringArray(vals);

   }

   public void fillStringList(JSONArray vals)
   {
      stringList = toStringArray(vals);

   }

   public void fillFilenameList(JSONArray vals)
   {
      filenameList = toStringArray(vals);

   }

   private List<String> toStringArray(JSONArray vals)
   {
      List<String> array = new ArrayList<String>();

      for (int i = 0; i < vals.length(); i++)
      {
         array.add(vals.getString(i));
      }
      return array;
   }
}
