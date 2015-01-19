package fr.certu.chouette.export.metadata.model;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;

public class NeptuneObjectPresenter
{
   public static String getName(Line line)
   {
      String name = line.getObjectId(); // maybe name empty
      if (!isEmpty(line.getName()))
      {
         name = line.getName();
      }
      else if (!isEmpty(line.getPublishedName()))
      {
         name = line.getPublishedName();
      }
      if (!isEmpty(line.getNumber()))
      {
         name += " ["+line.getNumber()+"]";
      }
      return name;
   }

   public static String getName(PTNetwork network)
   {
      String name = network.getObjectId(); // maybe name empty
      if (!isEmpty(network.getName()))
      {
         name = network.getName();
      }
      return name;
   }


   public static boolean isEmpty(String text)
   {
      return (text ==  null || text.isEmpty());
   }
}
