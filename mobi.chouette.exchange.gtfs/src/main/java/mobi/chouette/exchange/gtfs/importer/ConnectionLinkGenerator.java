package mobi.chouette.exchange.gtfs.importer;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.util.NeptuneUtil;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class ConnectionLinkGenerator extends AbstractGenerator {

	public void createConnectionLinks(Context context) {
		Referential referential = (Referential) context.get(REFERENTIAL);
		GtfsImportParameters configuration = (GtfsImportParameters) context.get(CONFIGURATION);
		Collection<ConnectionLink> fixedLinks = new ArrayList<>(referential.getConnectionLinks().values());
		List<ConnectionLink> excludedLinks = new ArrayList<ConnectionLink>();
		for (ConnectionLink link : referential.getSharedConnectionLinks().values()) {
			if ("FORBIDDEN".equals(link.getName())) {
				excludedLinks.add(link);
			}
		}
		List<StopArea> commercials = new ArrayList<StopArea>();
		for (StopArea stopArea : referential.getSharedStopAreas().values()) {
			if (stopArea.getAreaType().equals(ChouetteAreaEnum.CommercialStopPoint)) {
				commercials.add(stopArea);
			}
		}
		double distanceMax = configuration.getMaxDistanceForConnectionLink();
		double minDistanceRejected = 1000000;
		Map<String, ConnectionLink> fixedLinkMap = NeptuneUtil.mapOnObjectIds(fixedLinks);
		Map<String, ConnectionLink> excludedLinkMap = NeptuneUtil.mapOnObjectIds(excludedLinks);

		// build a map for CL for routes
//		Map<String, List<String>> routesForStopArea = new HashMap<String, List<String>>();
		List<ConnectionLink> links = new ArrayList<ConnectionLink>();
		if (distanceMax <= 0)
			return; // nothing to do

		for (int i = 0; i < commercials.size() - 1; i++) {
			StopArea source = commercials.get(i);
			for (int j = i + 1; j < commercials.size(); j++) {
				StopArea target = commercials.get(j);
				double distance = distance(source.getLongitude().doubleValue(), source.getLatitude().doubleValue(),
						target.getLongitude().doubleValue(), target.getLatitude().doubleValue());
				if (distance < distanceMax) {
					boolean ok = true;
					// eligible, check route
//					List<String> sourceRoutes = routesForStopArea.get(source.getObjectId());
//					if (sourceRoutes == null) {
//						sourceRoutes = computeRoutes(source);
//						routesForStopArea.put(source.getObjectId(), sourceRoutes);
//					}
//					List<String> targetRoutes = routesForStopArea.get(target.getObjectId());
//					if (targetRoutes == null) {
//						targetRoutes = computeRoutes(target);
//						routesForStopArea.put(target.getObjectId(), targetRoutes);
//					}
//					
//					for (String route : targetRoutes) {
//						if (sourceRoutes.contains(route)) {
//							ok = false;
//							break;
//						}
//					}
					if (ok) {
						// create connectionLink
						String[] sourceToken = source.getObjectId().split(":");
						String[] targetToken = target.getObjectId().split(":");
						String objectId = sourceToken[0] + ":" + ConnectionLink.CONNECTIONLINK_KEY + ":"
								+ sourceToken[2] + "_" + targetToken[2];
						String reverseId = sourceToken[0] + ":" + ConnectionLink.CONNECTIONLINK_KEY + ":"
								+ targetToken[2] + "_" + sourceToken[2];

						if (excludedLinkMap.containsKey(objectId))
							continue;
						if (excludedLinkMap.containsKey(reverseId))
							continue;
						double durationInMillis = distance * 900; // speed of 4
						// km/h
						Time defaultDuration = getTime((long) durationInMillis);

						if (fixedLinkMap.containsKey(objectId) || fixedLinkMap.containsKey(reverseId)) {
							ConnectionLink link = fixedLinkMap.get(objectId);
							if (link != null) {
								if (link.getDefaultDuration() == null)
									link.setDefaultDuration(defaultDuration);
								link.setLinkDistance(BigDecimal.valueOf(distance));
								link.setStartOfLink(source);
								link.setEndOfLink(target);
								log.info("ConnectionLink " + link.getName() + " updated");
							}
							link = fixedLinkMap.get(reverseId);
							if (link != null) {
								if (link.getDefaultDuration() == null)
									link.setDefaultDuration(defaultDuration);
								link.setLinkDistance(BigDecimal.valueOf(distance));
								link.setStartOfLink(source);
								link.setEndOfLink(target);
								log.info("ConnectionLink " + link.getName() + " updated");

							}
						} else {

							ConnectionLink link = ObjectFactory.getConnectionLink(referential, objectId);
							link.setDefaultDuration(defaultDuration);
							link.setCreationTime(Calendar.getInstance().getTime());
							link.setStartOfLink(source);
							link.setEndOfLink(target);
							link.setLinkDistance(BigDecimal.valueOf(distance));
							link.setLinkType(ConnectionLinkTypeEnum.Overground);
							link.setName("from " + source.getName() + " to " + target.getName());
							// logger.info("ConnectionLink "+link.getName()+" added");
							links.add(link);

							objectId = sourceToken[0] + ":" + ConnectionLink.CONNECTIONLINK_KEY + ":" + targetToken[2]
									+ "_" + sourceToken[2];
							ConnectionLink reverseLink = ObjectFactory.getConnectionLink(referential, objectId);
							reverseLink.setDefaultDuration(defaultDuration);
							reverseLink.setObjectId(objectId);
							reverseLink.setCreationTime(Calendar.getInstance().getTime());
							reverseLink.setStartOfLink(target);
							reverseLink.setEndOfLink(source);
							reverseLink.setLinkDistance(BigDecimal.valueOf(distance));
							reverseLink.setLinkType(ConnectionLinkTypeEnum.Overground);
							reverseLink.setName("from " + target.getName() + " to " + source.getName());
							// logger.info("ConnectionLink "+reverseLink.getName()+" added");
							links.add(reverseLink);
						}
					}
				} else if (distance < minDistanceRejected) {
					minDistanceRejected = distance;
				}

			}
		}

		if (links.isEmpty()) {
			log.info("ConnectionLink : no links builded , minimal distance found = " + minDistanceRejected + " > "
					+ distanceMax);
		}
		return;
	}

	protected Time getTime(long timeInMillis) {
		long timeInSec = timeInMillis / 1000;
		Calendar c = Calendar.getInstance();
		int d = c.get(Calendar.DATE);
		int M = c.get(Calendar.MONTH);
		int y = c.get(Calendar.YEAR);
		int s = (int) (timeInSec % 3600);
		timeInSec /= 60;
		int m = (int) (timeInSec % 60);
		timeInSec /= 60;
		int h = (int) (timeInSec);
		c.set(y, M, d, h, m, s);
		Time time = new Time(c.getTimeInMillis());
		return time;
	}

}
