package fr.certu.chouette.echange.comparator.amivif;

import java.util.Map;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;

public abstract class AbstractAmivifDataComparator implements IAmivifDataComparator 
{
   protected ExchangeableAmivifLineComparator master;

   private String mappingKey;

   private boolean stopOnFailure = false;


   /**
    * convert a list of target ObjectId in their corresponding source ObjectId
    * 
    * if not found, "no match" will be substituted
    * 
    * @param targetList 
    * @return sourceList a copy of targetList with sourceIds instead (the order is maintained)
    */
   protected String[] convertToSourceId(String[] targetList)
   {
      String[] sourceList = new String[targetList.length];
      for (int i = 0; i < targetList.length; i++)
      {

         if (master == null)
         {
            throw new RuntimeException("Null master in ExchangeableAmivifLineComparator");
         }
         if (targetList.length == 0)
         {
            throw new RuntimeException("Empty target list in ExchangeableAmivifLineComparator");
         }
         String sourceId = master.getSourceId(targetList[i]);
         if (sourceId == null) sourceId = "no match";
         sourceList[i] = sourceId;
      }
      return sourceList;
   }

   public String getMappingKey()
   {
      return mappingKey;
   }

   public void setMappingKey(String mappingKey)
   {
      this.mappingKey = mappingKey;
   }

   public Map<String, ChouetteObjectState> getStateMap() {
      // TODO Auto-generated method stub
      return null;
   }
   public void setStopOnFailure(boolean flag) {
      this.stopOnFailure = flag;
   }

   public boolean mustStopOnFailure()
   {
      return this.stopOnFailure;
   }

}
