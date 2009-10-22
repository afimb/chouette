package fr.certu.chouette.echange.comparator;

import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.commun.ServiceException;

public class PTNetworkComparator extends AbstractChouetteDataComparator 
{
   

  public boolean compareData(IExchangeableLineComparator master) throws ServiceException
  {
    this.master = master;
    ILectureEchange source = master.getSource();
    ILectureEchange target = master.getTarget();
    
    Reseau sourceData = source.getReseau();
    Reseau targetData = target.getReseau();
    
    // there is only one company for a line, so we can map the id immediately
    master.addMappingIds(sourceData.getObjectId(),targetData.getObjectId());
    
    // check the attributes 
    ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(),sourceData.getObjectId(),targetData.getObjectId());

    objectState.addAttributeState("Name",sourceData.getName(),targetData.getName());
    objectState.addAttributeState("RegistrationNumber",sourceData.getRegistrationNumber(),targetData.getRegistrationNumber());
    
    
    master.addObjectState(objectState);
    
    return objectState.isIdentical();
  }

  @Override
  public Map<String, ChouetteObjectState> getStateMap()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
