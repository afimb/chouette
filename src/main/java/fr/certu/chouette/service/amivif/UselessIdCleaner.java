package fr.certu.chouette.service.amivif;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import chouette.schema.ChouettePTNetworkTypeType;
import chouette.schema.Timetable;

public class UselessIdCleaner 
{
	public void clean(ChouettePTNetworkTypeType chouette)
	{
		Set<String> vehicleIdDefined = new HashSet<String>();
		int total = chouette.getChouetteLineDescription().getVehicleJourneyCount();
		for (int i = 0; i < total; i++) {
			vehicleIdDefined.add( chouette.getChouetteLineDescription().getVehicleJourney(i).getObjectId());
		}
		
		int totalTM = chouette.getTimetableCount();
		for (int i = 0; i < totalTM; i++) {
			Timetable tm = chouette.getTimetable( i);
			Set<String> vehicleIdLess = new HashSet<String>( Arrays.asList( tm.getVehicleJourneyId()));
			vehicleIdLess.removeAll( vehicleIdDefined);
			for (String vehicleId : vehicleIdLess) {
				tm.removeVehicleJourneyId( vehicleId);
			}
		}
	}

}
