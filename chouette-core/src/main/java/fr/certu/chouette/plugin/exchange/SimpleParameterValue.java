/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.plugin.exchange;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

public class SimpleParameterValue extends ParameterValue
{

   @Getter
   @Setter
   private Long integerValue;
   @Getter
   @Setter
   private Boolean booleanValue;
   @Getter
   @Setter
   private Calendar dateValue;
   @Getter
   @Setter
   private String stringValue;
   @Getter
   @Setter
   private String filenameValue;
   @Getter
   @Setter
   private String filepathValue;
   @Getter
   @Setter
   private Object objectValue;

   public SimpleParameterValue(String name)
   {
      super(name);

   }
}
