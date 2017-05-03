package mobi.chouette.exchange.importer.updater;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.Context;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.exchange.importer.updater.netex.NavigationPathMapper;
import mobi.chouette.exchange.importer.updater.netex.StopAreaMapper;
import mobi.chouette.exchange.importer.updater.netex.StopPlaceMapper;
import mobi.chouette.model.*;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.ObjectIdTypes;
import mobi.chouette.model.util.Referential;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.*;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton(name = NeTExStopPlaceRegisterUpdater.BEAN_NAME)
public class NeTExStopPlaceRegisterUpdater {
	private static final String STOP_PLACE_REGISTER_MAP = "STOP_PLACE_REGISTER_MAP";

	public static final String IMPORTED_ID = "imported-id";

	public static final String BEAN_NAME = "NeTExStopPlaceRegisterUpdater";

	public static final String IMPORTED_ID_VALUE_SEPARATOR = ",";

	private PublicationDeliveryClient client;

	private final StopPlaceMapper stopPlaceMapper = new StopPlaceMapper();

	private final StopAreaMapper stopAreaMapper = new StopAreaMapper();
	
	private NavigationPathMapper navigationPathMapper = null;

	private static final ObjectFactory objectFactory = new ObjectFactory();

	public NeTExStopPlaceRegisterUpdater(PublicationDeliveryClient client) throws DatatypeConfigurationException {
		this.client = client;
		navigationPathMapper = new NavigationPathMapper();
	}

	public NeTExStopPlaceRegisterUpdater() throws DatatypeConfigurationException {
		navigationPathMapper = new NavigationPathMapper();
	}

	@EJB
	private ContenerChecker contenerChecker;

	@PostConstruct
	public void postConstruct() {
		String urlPropertyKey = contenerChecker.getContext() + PropertyNames.STOP_PLACE_REGISTER_URL;
		String url = System.getProperty(urlPropertyKey);
		if (url == null) {
			log.warn("Cannot read property " + urlPropertyKey + ". Will not update stop place registry.");
			this.client = null;
		} else {
			try {
				this.client = new PublicationDeliveryClient(url, true);
			} catch (JAXBException | SAXException | IOException e) {
				log.warn("Cannot initialize publication delivery client with URL '" + url + "'", e);
			}
		}

	}

	public void update(Context context, Referential referential) throws JAXBException, DatatypeConfigurationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (client == null) {
			return;
		}

		// Use a correlation ID that will be set as ID on the site frame sent to
		// the stop place register.
		// This correlation ID shall be defined in every log line related to
		// this publication delivery
		// to be able to trace logs both in chouette and the stop place
		// register.
		final String correlationId = UUID.randomUUID().toString();

		@SuppressWarnings("unchecked")
		Map<String, String> stopPlaceRegisterMap = (Map<String, String>) context.get(STOP_PLACE_REGISTER_MAP);
		if (stopPlaceRegisterMap == null) {
			stopPlaceRegisterMap = new HashMap<>();
			context.put(STOP_PLACE_REGISTER_MAP, stopPlaceRegisterMap);
		}

		final Map<String, String> m = stopPlaceRegisterMap;

		Predicate<StopArea> fullStopAreaNotCahced = t -> {
            if (m.containsKey(t.getObjectId())) {
                for (StopArea child : t.getContainedStopAreas()) {
                    if (!m.containsKey(child.getObjectId())) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        };

		List<StopArea> boardingPositionsWithoutParents = referential.getStopAreas().values().stream()
				.filter(stopArea -> fullStopAreaNotCahced.test(stopArea))
				.filter(stopArea -> stopArea.getAreaType() == ChouetteAreaEnum.BoardingPosition)
				.filter(stopArea -> stopArea.getParent() == null).filter(stopArea -> stopArea.getObjectId() != null)
				.collect(Collectors.toList());

		List<StopArea> createdParents = new ArrayList<StopArea>();
		
		for(StopArea bp : boardingPositionsWithoutParents) {
			StopArea csp = stopAreaMapper.mapCommercialStopPoint(referential, bp);
			createdParents.add(csp);
			// log.info("created parent "+csp.getObjectId()+ " for
			// "+bp.getObjectId());
		}

		// Find and convert valid StopAreas
		// TODO add routesection arrival/departure
		// TODO add connection links stopareas?
		List<StopPlace> stopPlaces = referential.getStopAreas().values().stream()
				.map(stopArea -> stopArea.getParent() == null ? stopArea : stopArea.getParent())
				.filter(stopArea -> fullStopAreaNotCahced.test(stopArea))
				.filter(stopArea -> stopArea.getObjectId() != null)
				.filter(stopArea -> stopArea.getAreaType() == ChouetteAreaEnum.CommercialStopPoint).distinct()
				.peek(stopArea -> log.info(
						stopArea.getObjectId() + " name: " + stopArea.getName() + " correlationId: " + correlationId))
				.map(stopPlaceMapper::mapStopAreaToStopPlace).collect(Collectors.toList());

		SiteFrame siteFrame = new SiteFrame();
		stopPlaceMapper.setVersion(siteFrame);

		List<NavigationPath> navigationPaths = findAndMapConnectionLinks(referential, correlationId, siteFrame, m);

		if (!stopPlaces.isEmpty()) {

			// Only keep uniqueIds to avoid duplicate processing
			Set<String> uniqueIds = stopPlaces.stream().map(s -> s.getId()).collect(Collectors.toSet());
			stopPlaces = stopPlaces.stream().filter(s -> uniqueIds.remove(s.getId())).collect(Collectors.toList());

			// Find transport mode for stop place
			for (StopPlace stopPlace : stopPlaces) {

				String id = stopPlace.getId();
				StopArea stopArea = referential.getSharedStopAreas().get(stopPlace.getId());
				if(id.contains(ObjectIdTypes.STOPAREA_KEY)) {
					// Only replace IDs if ID already contains Chouette ID key (StopArea)
					stopPlaceMapper.replaceIdIfQuayOrStopPlace(stopPlace);
				}

				if (stopArea == null) {
					log.error("Could not find StopArea for objectId=" + ToStringBuilder.reflectionToString(stopPlace)
							+ " correlationId: " + correlationId);
				} else {
					// Recursively find all transportModes
					Set<TransportModeNameEnum> transportMode = findTransportModeForStopArea(new HashSet<>(), stopArea);
					if (transportMode.size() > 1) {
						log.warn("Found more than one transport mode for StopArea with id " + stopPlace.getId() + ": "
								+ ToStringBuilder.reflectionToString(transportMode.toArray(),ToStringStyle.SIMPLE_STYLE) + ", will use "
								+ transportMode.iterator().next()+ " correlationId: "+correlationId);
						stopPlaceMapper.mapTransportMode(stopPlace, transportMode.iterator().next());
					} else if (transportMode.size() == 1) {
						stopPlaceMapper.mapTransportMode(stopPlace, transportMode.iterator().next());
					} else {
						log.warn("No transport modes found for StopArea with id " + stopPlace.getId() + " correlationId: "
								+ correlationId);
					}
				}
			}

			siteFrame.setStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlaces));

			log.info("Create site frame with " + stopPlaces.size() + " stop places. correlationId: " + correlationId);
		}

		if (!stopPlaces.isEmpty() || !navigationPaths.isEmpty()) {
			siteFrame.setCreated(OffsetDateTime.now());
			siteFrame.setId(correlationId);

			JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

			PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
					.withDescription(new MultilingualString().withValue("Publication delivery from chouette")
							.withLang("no").withTextIdType(""))
					.withPublicationTimestamp(OffsetDateTime.now()).withParticipantRef("participantRef")
					.withDataObjects(new PublicationDeliveryStructure.DataObjects()
							.withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));

			PublicationDeliveryStructure response;
			try {
				response = client.sendPublicationDelivery(publicationDelivery);
			} catch (JAXBException | IOException | SAXException e) {
				throw new RuntimeException("Got exception while sending publication delivery with " + stopPlaces.size()
						+ " stop places to stop place register. correlationId: " + correlationId, e);
			}

			if (response.getDataObjects() == null) {
				throw new RuntimeException("The response dataObjects is null for received publication delivery. Nothing to do here. "
						+ correlationId);

			} else if (response.getDataObjects().getCompositeFrameOrCommonFrame() == null) {
				throw new RuntimeException("Composite frame or common frame is null for received publication delivery. " + correlationId);
			}

			log.info("Got publication delivery structure back with "
					+ response.getDataObjects().getCompositeFrameOrCommonFrame().size()
					+ " composite frames or common frames correlationId: " + correlationId);

			List<StopPlace> receivedStopPlaces = response.getDataObjects().getCompositeFrameOrCommonFrame().stream()
					.filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
					.map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
					.filter(receivedSiteFrame -> receivedSiteFrame.getStopPlaces() != null)
					.filter(receivedSiteFrame -> receivedSiteFrame.getStopPlaces().getStopPlace() != null)
					.flatMap(receivedSiteFrame -> receivedSiteFrame.getStopPlaces().getStopPlace().stream())
					.peek(stopPlace -> log.info("got stop place with ID " + stopPlace.getId() + " and name "
							+ stopPlace.getName() + " back. correlationId: " + correlationId))

			.collect(Collectors.toList());

			log.info("Collected " + receivedStopPlaces.size()
					+ " stop places from stop place register response. correlationId: " + correlationId);

			AtomicInteger mappedStopPlacesCount = new AtomicInteger();
			receivedStopPlaces.forEach(e -> {
				stopAreaMapper.mapStopPlaceToStopArea(referential, e);
				mappedStopPlacesCount.incrementAndGet();
			});

			log.info("Mapped " + mappedStopPlacesCount.get() + " stop places into stop areas. correlationId: "
					+ correlationId);

			// Create map of existing object id -> new object id
			for (StopPlace newStopPlace : receivedStopPlaces) {
				KeyListStructure keyList = newStopPlace.getKeyList();
				addIdsToLookupMap(stopPlaceRegisterMap, keyList, newStopPlace.getId());

				Quays_RelStructure quays = newStopPlace.getQuays();
				if (quays != null && quays.getQuayRefOrQuay() != null) {
					for (Object b : quays.getQuayRefOrQuay()) {
						Quay q = (Quay) b;
						KeyListStructure qKeyList = q.getKeyList();
						addIdsToLookupMap(stopPlaceRegisterMap, qKeyList, q.getId());
					}
				}
			}

			log.info("Map with objectId->newObjectId now contains " + stopPlaceRegisterMap.keySet().size() + " keys (objectIds) and "
					+ stopPlaceRegisterMap.values().size() + " values (newObjectIds). correlationId: " + correlationId);

			// Create map of existing object id -> new object id
			List<PathLink> receivedPathLinks = response.getDataObjects().getCompositeFrameOrCommonFrame().stream()
					.filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
					.map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
					.filter(plStucture -> plStucture.getPathLinks() != null)
					.filter(plStructure -> plStructure.getPathLinks() != null)
					.filter(plStructure -> plStructure.getPathLinks().getPathLink() != null)
					.flatMap(plStructure -> plStructure.getPathLinks().getPathLink().stream())
					.peek(pl -> log
							.info("got path link with ID " + pl.getId() + " back. correlationId: " + correlationId))
					.collect(Collectors.toList());

			receivedPathLinks.forEach(e -> navigationPathMapper.mapPathLinkToConnectionLink(referential, e));

			for (PathLink pl : receivedPathLinks) {
				KeyListStructure keyList = pl.getKeyList();
				addIdsToLookupMap(stopPlaceRegisterMap, keyList, pl.getId());
			}

		}
		Set<String> discardedStopAreas = new HashSet<>();

		// Update each stopPoint
		for (Line line : referential.getLines().values()) {
			for (Route r : line.getRoutes()) {
				for (StopPoint sp : r.getStopPoints()) {
					updateStopArea(correlationId, stopPlaceRegisterMap, referential, discardedStopAreas, sp, "containedInStopArea");
				}
			}
		}
		for (RouteSection rs : referential.getRouteSections().values()) {
			updateStopArea(correlationId, stopPlaceRegisterMap, referential, discardedStopAreas, rs, "arrival");
			updateStopArea(correlationId, stopPlaceRegisterMap, referential, discardedStopAreas, rs, "departure");
		}
		// TODO update stoparea in connectionlinks and accesslinks (check uml
		// diagram for usage of stoparea

		// TODO? remove obsolete connectionLinks?
		List<ConnectionLink> removedCollectionLinks = referential.getSharedConnectionLinks().values().stream()
				.filter(e -> m.containsKey(e.getObjectId())).collect(Collectors.toList());

		removedCollectionLinks.stream()
				.peek(e -> log.info(
						"Removing old connectionLink with id " + e.getObjectId() + ". correlationId: " + correlationId))
				.map(e -> referential.getSharedConnectionLinks().remove(e.getObjectId())).collect(Collectors.toList());

		// Clean referential from old garbage stop areas
		for (String obsoleteObjectId : discardedStopAreas)

		{
			referential.getStopAreas().remove(obsoleteObjectId);
		}

		for (

		StopArea sa : createdParents)

		{
			referential.getStopAreas().remove(sa.getObjectId());
		}

	}

	private List<NavigationPath> findAndMapConnectionLinks(Referential referential, String correlationId, SiteFrame siteFrame, Map<String, String> m ) {
		referential.getSharedConnectionLinks().clear(); // Nuke connection links
		// fully to avoid old
		// stopareas being
		// persisted

		return referential.getSharedConnectionLinks().values().stream()
						.filter(link -> !m.containsKey(link.getObjectId()))
						.peek(link -> log.debug(link.getObjectId() + " correlationId:"+correlationId))
						.map(link ->
								navigationPathMapper.mapConnectionLinkToNavigationPath(siteFrame,
										link))
						.collect(Collectors.toList());
	}

	private void updateStopArea(String correlationId, Map<String, String> map, Referential referential,
			Set<String> discardedStopAreas, NeptuneIdentifiedObject sp, String name)
					throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StopArea stopArea = (StopArea) PropertyUtils.getProperty(sp, name);
		String currentObjectId = stopArea.getObjectId();
		String newObjectId = map.get(currentObjectId);

		if (newObjectId != null) {
			if (!currentObjectId.equals(newObjectId)) {
				StopArea newStopArea = referential.getSharedStopAreas().get(newObjectId);
				if (newStopArea != null) {
					PropertyUtils.setProperty(sp, name, newStopArea);
					discardedStopAreas.add(currentObjectId);
				} else {

					log.error("About to replace StopArea with id " + currentObjectId + " with " + newObjectId
							+ ", but newStopArea does not exist in referential! correlationId: " + correlationId);
				}
			}
		} else {
			log.warn("Could not find mapped object for " + sp.getClass().getSimpleName() + "/" + name + " "
					+ currentObjectId + " " + stopArea.getName() + " correlationId: " + correlationId);
		}
	}

	private void addIdsToLookupMap(Map<String, String> map, KeyListStructure keyList, String newStopPlaceId) {
		List<KeyValueStructure> keyValue = keyList.getKeyValue();

		for (KeyValueStructure s : keyValue) {
			if (s.getKey().equals(IMPORTED_ID)) {
				// Split value
				String[] existingIds = StringUtils.split(s.getValue(), IMPORTED_ID_VALUE_SEPARATOR);
				for (String id : existingIds) {
					map.put(id.replaceAll("Quay", "StopArea").replaceAll("StopPlace", "StopArea"), newStopPlaceId);
					map.put(id, newStopPlaceId);
				}
			}
		}
	}

	protected Set<TransportModeNameEnum> findTransportModeForStopArea(Set<TransportModeNameEnum> transportModes,
			StopArea sa) {
		TransportModeNameEnum transportModeName = null;
		List<StopPoint> stopPoints = sa.getContainedStopPoints();
		for (StopPoint stop : stopPoints) {
			if (stop.getRoute() != null && stop.getRoute().getLine() != null) {
				transportModeName = stop.getRoute().getLine().getTransportModeName();
				if (transportModeName != null) {
					transportModes.add(transportModeName);
					break;
				}
			}
		}

		for (StopArea child : sa.getContainedStopAreas()) {
			transportModes = findTransportModeForStopArea(transportModes, child);
		}

		return transportModes;

	}

}
