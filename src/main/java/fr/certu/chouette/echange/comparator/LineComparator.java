package fr.certu.chouette.echange.comparator;

import java.util.HashMap;
import java.util.Map;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.service.commun.ServiceException;

public class LineComparator extends AbstractChouetteDataComparator 
{
	
	public boolean compareData(IExchangeableLineComparator master) throws ServiceException
	{
        this.master = master;
		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();
		
		Ligne sourceData = source.getLigne();
		Ligne targetData = target.getLigne();
		
		// there is only one company for a line, so we can map the id immediately
		master.addMappingIds(sourceData.getObjectId(), targetData.getObjectId());
		
		// check the attributes 
		ChouetteObjectState objectState = new ChouetteObjectState(getMappingKey(), sourceData.getObjectId(), targetData.getObjectId());

		objectState.addAttributeState("Name", sourceData.getName(), targetData.getName());
		objectState.addAttributeState("RegistrationNumber", sourceData.getRegistrationNumber(), targetData.getRegistrationNumber());
		objectState.addAttributeState("TransportModeName", sourceData.getTransportModeName(), targetData.getTransportModeName());

		
		// check the count of all the sublists 
		objectState.addAttributeState("StopPoint count", source.getArrets(), target.getArrets());
			
		objectState.addAttributeState("StopArea count (Boarding position and Quay)", source.getArretsPhysiques(), target.getArretsPhysiques());
		
		objectState.addAttributeState("StopArea count (Place)", source.getZonesPlaces(), target.getZonesPlaces());
		
		objectState.addAttributeState("StopArea count (Commercial)", source.getZonesCommerciales(), target.getZonesCommerciales());
		
		objectState.addAttributeState("Routes count", source.getItineraires(), target.getItineraires());
		
		/** @todo discuss : only internal link are checkable
		objectState.addAttributeState("Connection links count", source.getCorrespondances(), target.getCorrespondances());
		addMasterCountState("connectionLink-count", objectState.isIdentical());
		**/
		
		objectState.addAttributeState("JourneyPattern count", source.getMissions(), target.getMissions());		
		
		objectState.addAttributeState("TimeTable count", source.getTableauxMarche(), target.getTableauxMarche());		
		
		objectState.addAttributeState("VehicleJourney count", source.getCourses(), target.getCourses());		
		
		objectState.addAttributeState("VehicleJourneyAtStop count", source.getHoraires(), target.getHoraires());
		
		// TODO : ajouter à terme les ITL		
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
