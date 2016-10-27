package mobi.chouette.exchange.importer.updater;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.Context;
import mobi.chouette.common.PropertyNames;
import mobi.chouette.exchange.importer.updater.netex.StopPlaceMapper;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.Referential;

@Log4j
@Stateless(name = NeTExStopPlaceRegisterUpdater.BEAN_NAME)
public class NeTExStopPlaceRegisterUpdater {
	private static final String STOP_PLACE_REGISTER_MAP = "STOP_PLACE_REGISTER_MAP";

	public static final String IMPORTED_ID = "imported-id";

	public static final String BEAN_NAME = "NeTExStopPlaceRegisterUpdater";

	public static final String IMPORTED_ID_VALUE_SEPARATOR = ",";

	private PublicationDeliveryClient client;
	private final StopPlaceMapper stopPlaceMapper = new StopPlaceMapper();

	private static final ObjectFactory objectFactory = new ObjectFactory();

	public NeTExStopPlaceRegisterUpdater(PublicationDeliveryClient client) {
		this.client = client;
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
				this.client = new PublicationDeliveryClient(url);
			} catch (JAXBException e) {
				log.warn("Cannot initialize publication delivery client", e);
			}
		}
	}

	public NeTExStopPlaceRegisterUpdater() {
	}

	public void update(Context context, Referential referential) throws JAXBException {

		if (client == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) context.get(STOP_PLACE_REGISTER_MAP);
		if (map == null) {
			map = new HashMap<>();
			context.put(STOP_PLACE_REGISTER_MAP, map);
		}

		final Map<String, String> m = map;

		// Find and convert valid StopAreas
		List<StopPlace> stopPlaces = referential.getStopAreas().values().stream()
				.map(stopArea -> stopArea.getParent() == null ? stopArea : stopArea.getParent())
				.filter(stopArea -> !m.containsKey(stopArea.getObjectId()))
				.filter(stopArea -> stopArea.getObjectId() != null)
				.filter(stopArea -> stopArea.getAreaType() == ChouetteAreaEnum.CommercialStopPoint)
				.peek(stopArea -> log.info("id: " + stopArea.getId() + " objectId: " + stopArea.getObjectId()
						+ " name: " + stopArea.getName() + " type: " + stopArea.getAreaType()

		)).map(stopPlaceMapper::mapStopAreaToStopPlace).collect(Collectors.toList());

		if (!stopPlaces.isEmpty()) {

			// Only keep uniqueIds to avoid duplicate processing
			Set<String> uniqueIds = stopPlaces.stream().map(s -> s.getId()).collect(Collectors.toSet());
			stopPlaces = stopPlaces.stream().filter(s -> uniqueIds.remove(s.getId())).collect(Collectors.toList());

			// Find transport mode for stop place
			for (StopPlace sp : stopPlaces) {

				StopArea sa = referential.getSharedStopAreas().get(sp.getId());
				if (sa == null) {
					log.error("Could not find StopArea for objectId=" + ToStringBuilder.reflectionToString(sp));
				} else {
					// Recursively find all transportModes
					Set<TransportModeNameEnum> transportMode = findTransportModeForStopArea(
							new HashSet<TransportModeNameEnum>(), sa);
					if (transportMode.size() > 1) {
						log.warn("Found more than one transport mode for StopArea with id " + sp.getId() + ": "
								+ ToStringBuilder.reflectionToString(transportMode) + ", will use "
								+ transportMode.iterator().next());
					} else if (transportMode.size() == 1) {
						stopPlaceMapper.mapTransportMode(sp, transportMode.iterator().next());
					} else {
						log.warn("No transport modes found for StopArea with id " + sp.getId());
					}
				}
			}

			SiteFrame siteFrame = new SiteFrame();
			siteFrame.setStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlaces));

			log.info("Create site frame with " + stopPlaces.size() + " stop places");
			JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

			PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
					.withDescription(new MultilingualString().withValue("Publication delivery from chouette")
							.withLang("no").withTextIdType(""))
					.withPublicationTimestamp(OffsetDateTime.now()).withParticipantRef("participantRef")
					.withDataObjects(new PublicationDeliveryStructure.DataObjects()
							.withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));

			PublicationDeliveryStructure response = null;
			try {
				response = client.sendPublicationDelivery(publicationDelivery);
			} catch (JAXBException | IOException e) {
				log.warn("Got exception while sending publication delivery with " + stopPlaces.size() + " stop places",
						e);
				return;
			}
			log.info("Got publication delivery structure back with "
					+ response.getDataObjects().getCompositeFrameOrCommonFrame().size()
					+ " composite frames or common frames");

			List<StopPlace> receivedStopPlaces = response.getDataObjects().getCompositeFrameOrCommonFrame().stream()
					.filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
					.map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
					.flatMap(receivedSiteFrame -> receivedSiteFrame.getStopPlaces().getStopPlace().stream())
					.peek(stopPlace -> log.info("got stop place with ID " + stopPlace.getId() + " and name "
							+ stopPlace.getName() + " back"))

			.collect(Collectors.toList());

			StopPlaceMapper mapper = new StopPlaceMapper();
			receivedStopPlaces.stream().forEach(e -> mapper.mapStopPlaceToStopArea(referential, e));

			// Create map of existing object id -> new object id
			for (StopPlace newStopPlace : receivedStopPlaces) {
				KeyListStructure keyList = newStopPlace.getKeyList();
				addIdsToLookupMap(map, keyList, newStopPlace.getId());

				Quays_RelStructure quays = newStopPlace.getQuays();
				for (Object b : quays.getQuayRefOrQuay()) {
					Quay q = (Quay) b;
					KeyListStructure qKeyList = q.getKeyList();
					addIdsToLookupMap(map, qKeyList, q.getId());
				}
			}
		}
		Set<String> discardedStopAreas = new HashSet<String>();

		// Update each stopPoint
		for (Line line : referential.getLines().values()) {
			for (Route r : line.getRoutes()) {
				for (StopPoint sp : r.getStopPoints()) {
					String currentObjectId = sp.getContainedInStopArea().getObjectId();
					String newObjectId = map.get(currentObjectId);

					if (newObjectId != null) {
						if (!currentObjectId.equals(newObjectId)) {

							StopArea newStopArea = referential.getSharedStopAreas().get(newObjectId);
							if (newStopArea != null) {
								sp.setContainedInStopArea(newStopArea);
								discardedStopAreas.add(currentObjectId);

							} else {

								log.error("About to replace StopArea with id " + currentObjectId + " with "
										+ newObjectId + ", but newStopArea does not exist in referential!");
							}
						}
					} else {
						log.warn("Could not find mapped object for " + currentObjectId);
					}

				}
			}
		}

		// Clean referential from old garbage stop areas
		// for(String obsoleteObjectId : discardedStopAreas) {
		// referential.getStopAreas().remove(obsoleteObjectId);
		// }

	}

	private void addIdsToLookupMap(Map<String, String> map, KeyListStructure keyList, String newStopPlaceId) {
		List<KeyValueStructure> keyValue = keyList.getKeyValue();

		for (KeyValueStructure s : keyValue) {
			if (s.getKey().equals(IMPORTED_ID)) {
				// Split value
				String[] existingIds = StringUtils.split(s.getValue(), IMPORTED_ID_VALUE_SEPARATOR);
				for (String id : existingIds) {
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
