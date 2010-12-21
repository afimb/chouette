package fr.certu.chouette.echange.comparator;

import java.util.Map;

public interface IChouetteDataComparator 
{
   boolean compareData(IExchangeableLineComparator master) throws Exception;

   Map<String,ChouetteObjectState> getStateMap();
   
   boolean mustStopOnFailure();
   
   String getMappingKey();
}
