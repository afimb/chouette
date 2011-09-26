package fr.certu.chouette.struts.json;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.struts.GeneriqueAction;

@SuppressWarnings("serial")
public class JSONConnectionLinkAction extends GeneriqueAction
{

   @Getter @Setter private Long connectionLinkId;
   private INeptuneManager<ConnectionLink> connectionLinkManager;


   public Set<StopArea> getStopPlaces()
   {
      try
      {
         ConnectionLink connectionLink;
         connectionLink = connectionLinkManager.getById(connectionLinkId);
         Set<StopArea> stopPlaces = new HashSet<StopArea>();
         if (connectionLink.getStartOfLink() != null)
         {
            stopPlaces.add(connectionLink.getStartOfLink());
         }
         if (connectionLink.getEndOfLink() != null)
         {
            stopPlaces.add(connectionLink.getEndOfLink());
         }
         return stopPlaces;
      }
      catch (ChouetteException e)
      {
         addActionError(e.getLocalizedMessage());
         return null;
      }
   }

}
