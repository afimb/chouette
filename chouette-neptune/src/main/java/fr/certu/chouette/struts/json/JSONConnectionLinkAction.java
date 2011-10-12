package fr.certu.chouette.struts.json;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.manager.INeptuneManager;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.struts.GeneriqueAction;
import fr.certu.chouette.struts.json.data.JSONStopArea;

@SuppressWarnings("serial")
public class JSONConnectionLinkAction extends GeneriqueAction
{

   @Getter @Setter private Long connectionLinkId;
   @Setter private INeptuneManager<ConnectionLink> connectionLinkManager;


   public Set<JSONStopArea> getStopPlaces()
   {
      try
      {
         ConnectionLink connectionLink;
         connectionLink = connectionLinkManager.getById(connectionLinkId);
         Set<JSONStopArea> stopPlaces = new HashSet<JSONStopArea>();
         if (connectionLink.getStartOfLink() != null)
         {
            stopPlaces.add(new JSONStopArea(connectionLink.getStartOfLink()));
         }
         if (connectionLink.getEndOfLink() != null)
         {
            stopPlaces.add(new JSONStopArea(connectionLink.getEndOfLink()));
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
