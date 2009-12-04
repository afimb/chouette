package fr.certu.chouette.echange.comparator.amivif;

import java.util.Map;

import fr.certu.chouette.echange.comparator.ChouetteObjectState;
import fr.certu.chouette.service.commun.ServiceException;


public interface IAmivifDataComparator 
{
   boolean compareData(ExchangeableAmivifLineComparator master) throws ServiceException;

   Map<String,ChouetteObjectState> getStateMap();
   
   boolean mustStopOnFailure();
   
   String getMappingKey();

}
