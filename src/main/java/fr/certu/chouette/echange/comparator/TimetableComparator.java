package fr.certu.chouette.echange.comparator;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chouette.schema.Period;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.modele.Mission;
import fr.certu.chouette.modele.Periode;
import fr.certu.chouette.modele.TableauMarche;
import fr.certu.chouette.service.commun.ServiceException;

/**
 * @author Dryade, Evelyne Zahn
 * 
 */
public class TimetableComparator extends AbstractChouetteDataComparator {
	public boolean compareData(IExchangeableLineComparator master)
			throws ComparatorException{
		
		boolean sameTimetables = true;

		this.master = master;

		ILectureEchange source = master.getSource();
		ILectureEchange target = master.getTarget();

		List<TableauMarche> sourceDataList = source.getTableauxMarche();
		List<TableauMarche> targetDataList = target.getTableauxMarche();

		HashMap<String, String> sourceObjectIdBytargetNaturalKey = new HashMap<String, String>();
		// natural key ? dayTypes, periods, calendar days
		for (TableauMarche sourceTM : sourceDataList) {
			int dayTypeMask = sourceTM.getIntDayTypes();
			List<Periode> TMPeriods = sourceTM.getPeriodes();
			List<Date> TMDates = sourceTM.getDates();
			String key = buildNaturalKey(dayTypeMask, TMPeriods, TMDates);
			for (int i = 0; i < sourceTM.getVehicleJourneyIdCount(); i++) {
				String sourceVehiculeJourneyId = sourceTM
						.getVehicleJourneyId(i);
				String targetVehiculeJourneyId = master
						.getTargetId(sourceVehiculeJourneyId);
				key += "-" + targetVehiculeJourneyId;
			}
			sourceObjectIdBytargetNaturalKey.put(key, sourceTM.getObjectId());
		}

		for (TableauMarche targetTM : targetDataList) {
			int dayTypeMask = targetTM.getIntDayTypes();
			List<Periode> TMPeriods = targetTM.getPeriodes();
			List<Date> TMDates = targetTM.getDates();
			String key = buildNaturalKey(dayTypeMask, TMPeriods, TMDates);
			for (int i = 0; i < targetTM.getVehicleJourneyIdCount(); i++) {
				key += "-" + targetTM.getVehicleJourneyId(i);
			}

			String sourceTMId = sourceObjectIdBytargetNaturalKey.remove(key);
			ChouetteObjectState objectState = null;
			if (sourceTMId == null) {
				objectState = new ChouetteObjectState(getMappingKey(), null,
						targetTM.getObjectId());
				sameTimetables = false;
			} else {
				objectState = new ChouetteObjectState(getMappingKey(), sourceTMId,
						targetTM.getObjectId());
				master.addMappingIds(sourceTMId, targetTM.getObjectId());
			}
			master.addObjectState(objectState);
		}

		// Unmapped source vehicle journeys
		Collection<String> unmappedSourceObjects = sourceObjectIdBytargetNaturalKey
				.values();
		if (unmappedSourceObjects.size() != 0) {
			sameTimetables = false;
			for (String sourceObjectId : unmappedSourceObjects) {
				ChouetteObjectState objectState = new ChouetteObjectState(
				        getMappingKey(), sourceObjectId, null);
				master.addObjectState(objectState);
			}
		}
		return sameTimetables;
	}

	//@todo rename : just first part of key, the other part 
	// consisting in stoppoints list
	private String buildNaturalKey(int dayTypesMask, List<Periode> TMPeriods,
			List<Date> TMDates) {
		String key = ((Integer) dayTypesMask).toString();
		for (Periode TMPeriod : TMPeriods) {
			key += "-" + TMPeriod.getDebut().toString();
			key += "-" + TMPeriod.getFin().toString();
		}

		for (Date TMDate : TMDates) {
			key += "-" + TMDate.toString();
		}

		return key;
	}

    @Override
    public Map<String, ChouetteObjectState> getStateMap()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
