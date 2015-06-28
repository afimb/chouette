package mobi.chouette.exchange.gtfs.importer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class CommercialStopGenerator extends AbstractGenerator {

	/**
	 * create and associate commercial stop points for boarding positions
	 * 
	 * @param boardingPositions
	 *            list of input boarding positions
	 * @return commercial stop points created
	 */
	public void createCommercialStopPoints(Context context) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);

		List<StopArea> boardingPositions = new ArrayList<StopArea>();
		for (StopArea stopArea : referential.getSharedStopAreas().values()) {
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.BoardingPosition)
					|| stopArea.getAreaType().equals(ChouetteAreaEnum.Quay)) {
				boardingPositions.add(stopArea);
			}
		}
		double distanceMax = configuration.getMaxDistanceForCommercial();
		boolean ignoreLastWord = configuration.isIgnoreLastWord();
		int ignoreEndCharacters = configuration.getMaxDistanceForCommercial();

		Map<String, StopArea> areaMap = new HashMap<String, StopArea>();

		Set<String> keys = new HashSet<String>();
		for (StopArea stop : boardingPositions) {
			String key = stop.getName();
			if (ignoreLastWord) {
				String[] token = key.split(" ");
				if (token.length > 0 && token[token.length - 1].length() < ignoreEndCharacters) {
					key = key.substring(0, key.lastIndexOf(" " + token[token.length - 1]));
				}
			} else if (key.length() > ignoreEndCharacters) {
				key = key.substring(0, key.length() - ignoreEndCharacters);
			}
			keys.add(key);
		}

		for (StopArea stop : boardingPositions) {

			String mergeKey = stop.getName();
			if (stop.getParent() != null) {
				mergeKey = stop.getParent().getObjectId();
			} else {
				for (String key : keys) {
					if (mergeKey.startsWith(key)) {
						mergeKey = key;
						break;
					}
				}
			}

			StopArea area = areaMap.get(mergeKey);
			if (area == null) {
				// check if stop has already a parent (from gtfs)
				if (stop.getParent() == null) {

					area = initArea(referential, stop, null);

					areaMap.put(mergeKey, area);
				} else {
					area = stop.getParent();
				}
			} else if (stop.getParent() != null) {
				if (!area.equals(stop.getParent())) {
					log.error("conflict between generated and setted parent");
					log.error("stop   = " + stop.getObjectId() + " " + stop.getName());
					log.error("parent = " + area.getObjectId() + " " + area.getName());
					continue;
				}
			}
			stop.setParent(area);

		}

		// check distance to explode areas
		List<StopArea> dividedAreas = new ArrayList<StopArea>();
		for (StopArea area : areaMap.values()) {
			explodeArea(referential, dividedAreas, area, 1, area.getObjectId(), distanceMax);
		}

		// save area
		List<StopArea> areas = new ArrayList<StopArea>();
		areas.addAll(areaMap.values());
		areas.addAll(dividedAreas);

		for (StopArea stopArea : areas) {
			String basename = stopArea.getName();
			for (StopArea boarding : stopArea.getContainedStopAreas()) {
				String name = boarding.getName();
				while (!name.contains(basename)) {
					basename = basename.substring(0, basename.length() - 1);
					if (basename.isEmpty())
						break;
				}
			}
			if (!basename.isEmpty())
				stopArea.setName(basename.trim());
		}

		log.debug("" + areas.size() + " commercial stops created");

	}

	/**
	 * Commercial stop point initialization with first boarding position values
	 * 
	 * @param area
	 *            commercial stop point
	 * @param stop
	 *            boarding position
	 * @param referential
	 * @param objectId
	 */
	private StopArea initArea(Referential referential, StopArea stop, String objectId) {
		Calendar now = Calendar.getInstance();
		String[] token = stop.getObjectId().split(":");
		if (objectId == null)
			objectId = token[0] + ":" + token[1] + ":COM_" + token[2];
		StopArea area = ObjectFactory.getStopArea(referential, objectId);
		area.setName(stop.getName());
		area.setObjectId(objectId);
		area.setObjectVersion(stop.getObjectVersion());
		area.setCreationTime(now.getTime());
		area.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
		return area;
	}

	/**
	 * divide commercial stop point into smaller ones if boarding positions are
	 * too far <br/>
	 * recursive method
	 * 
	 * @param dividedAreas
	 *            divided area container
	 * @param area
	 *            area to check
	 * @param rank
	 *            rank of subdivision
	 * @param referential
	 */
	private void explodeArea(Referential referential, List<StopArea> dividedAreas, StopArea area, int rank,
			String baseId, double distanceMax) {
		if (!checkDistance(area, distanceMax)) {
			if (rank == 1) {
				log.warn(area.getName() + " has long distance boarding positions , divided");
			}
			List<StopArea> excludedList = excludeLongDistanceStops(area, distanceMax);

			StopArea areaExcluded = initArea(referential, excludedList.get(0), baseId + "_" + rank);
			// patch object id for non confusion
			areaExcluded.setObjectId(baseId + "_" + rank);
			for (StopArea excluded : excludedList) {
				excluded.setParent(areaExcluded);
			}
			dividedAreas.add(areaExcluded);
			explodeArea(referential, dividedAreas, areaExcluded, rank + 1, baseId, distanceMax);
		}
	}

	/**
	 * remove boarding positions to far from others.
	 * 
	 * @param area
	 *            the commercial stop point to check
	 * @return a list of removed boarding positions
	 */
	private List<StopArea> excludeLongDistanceStops(StopArea area, double distanceMax) {
		List<StopArea> excludedStops = new ArrayList<StopArea>();

		// remove stop most away from area while distance id invalid
		while (!checkDistance(area, distanceMax)) {
			double distanceMaxInArea = 0;
			StopArea excluded = null;
			List<StopArea> stops = area.getContainedStopAreas();
			for (StopArea stop : stops) {

				double distance = distance(area.getLongitude().doubleValue(), area.getLatitude().doubleValue(), stop
						.getLongitude().doubleValue(), stop.getLatitude().doubleValue());
				if (distance > distanceMaxInArea) {
					distanceMaxInArea = distance;
					excluded = stop;
				}
			}
			if (excluded != null) {
				excluded.setParent(null);
				excludedStops.add(excluded);
			}
		}
		return excludedStops;
	}

	/**
	 * compute centroid for the commercial stop point's boarding positions
	 * 
	 * @param area
	 */
	private void buildCentroid(StopArea area) {

		double sigmaLong = 0;
		double sigmaLat = 0;

		for (StopArea stop : area.getContainedStopAreas()) {
			sigmaLong += stop.getLongitude().doubleValue();
			sigmaLat += stop.getLatitude().doubleValue();
		}
		double areaLong = sigmaLong / area.getContainedStopAreas().size();
		double areaLat = sigmaLat / area.getContainedStopAreas().size();
		area.setLongitude(new BigDecimal(areaLong));
		area.setLatitude(new BigDecimal(areaLat));
		area.setLongLatType(LongLatTypeEnum.WGS84);
	}

	/**
	 * check every pair of stop to see if they are all in the distance
	 * 
	 * @return
	 */
	private boolean checkDistance(StopArea area, double distanceMax) {
		buildCentroid(area);
		//
		List<StopArea> stops = area.getContainedStopAreas();
		for (int i = 0; i < stops.size() - 1; i++) {
			StopArea first = stops.get(i);
			for (int j = i + 1; j < stops.size(); j++) {
				StopArea next = stops.get(j);
				double distance = distance(first.getLongitude().doubleValue(), first.getLatitude().doubleValue(), next
						.getLongitude().doubleValue(), next.getLatitude().doubleValue());
				if (distance > distanceMax) {
					// logger.debug("BP : "+first.getName()+" (pos="+first.getLatitude()+","+first.getLongitude());
					// logger.debug("BP : "+next.getName()+" (pos="+next.getLatitude()+","+next.getLongitude());
					// logger.debug("distance = "+distance);
					return false;
				}
			}
		}
		return true;
	}

}
