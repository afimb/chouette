package fr.certu.chouette.echange.comparator;

import java.util.Map;

import fr.certu.chouette.service.commun.ServiceException;

public interface IChouetteDataComparator 
{
   boolean compareData(IExchangeableLineComparator master) throws Exception;

   Map<String,ChouetteObjectState> getStateMap();
}
