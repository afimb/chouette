package mobi.chouette.exchange.importer.updater;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.testng.Assert;
import org.testng.annotations.Test;

import mobi.chouette.common.Context;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.netex.model.Zone_VersionStructure;

public class NeTExStopPlaceRegisterUpdaterTest {
	
	@Test
	public void exportStopArea() throws Exception {

		Referential referential = new Referential();

		StopArea stopArea = ObjectFactory.getStopArea(referential, "AKT:StopArea:1");
		stopArea.setName("Nesbru");
		stopArea.setAreaType(ChouetteAreaEnum.CommercialStopPoint);

		stopArea.setLatitude(new BigDecimal(59.9202707));
		stopArea.setLongitude(new BigDecimal(10.7913503));
		stopArea.setLongLatType(LongLatTypeEnum.WGS84);

		StopArea boardingPosition = ObjectFactory.getStopArea(referential, "AKT:StopArea:2");
		boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
		boardingPosition.setLatitude(new BigDecimal(59.9202707));
		boardingPosition.setLongitude(new BigDecimal(10.7913503));
		boardingPosition.setLongLatType(LongLatTypeEnum.WGS84);
		boardingPosition.setParent(stopArea);

		Line line = ObjectFactory.getLine(referential, "AKT:Line:1");
		Route route = ObjectFactory.getRoute(referential, "AKT:Route:1");
		route.setLine(line);

		StopPoint sp1 = ObjectFactory.getStopPoint(referential, "AKT:StopPoint:1");
		sp1.setContainedInStopArea(stopArea);

		StopPoint sp2 = ObjectFactory.getStopPoint(referential, "AKT:StopPoint:2");
		sp2.setContainedInStopArea(boardingPosition);

		route.getStopPoints().add(sp1);
		route.getStopPoints().add(sp2);

		Context context = new Context();

		// Build response
		NeTExStopPlaceRegisterUpdater neTExStopPlaceRegisterUpdater = new NeTExStopPlaceRegisterUpdater(
				new PublicationDeliveryClient("") {
					@Override
					public PublicationDeliveryStructure sendPublicationDelivery(
							PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException {

						SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure()
								.withLocation(new LocationStructure().withLatitude(stopArea.getLatitude())
										.withLongitude(stopArea.getLongitude()));

						StopPlace stopPlace = new StopPlace();
						stopPlace.setId("NHR:StopArea:1");
						stopPlace.setCentroid(centroid);
						stopPlace.setName(new MultilingualString().withValue("StopPlaceName"));
						stopPlace.setKeyList(createKeyListStructure("AKT:StopArea:1"));

						Quay q = new Quay();
						q.setId("NHR:StopArea:2");
						q.setKeyList(createKeyListStructure("AKT:StopArea:2"));
						q.setName(new MultilingualString().withValue("QuayName"));
						q.setCentroid(centroid);

						Quays_RelStructure quays = new Quays_RelStructure();
						quays.getQuayRefOrQuay().add(q);
						stopPlace.setQuays(quays);

						List<StopPlace> stopPlaces = new ArrayList<>();
						stopPlaces.add(stopPlace);

						SiteFrame siteFrame = new SiteFrame();
						siteFrame.setStopPlaces(new StopPlacesInFrame_RelStructure().withStopPlace(stopPlaces));

						org.rutebanken.netex.model.ObjectFactory objectFactory = new org.rutebanken.netex.model.ObjectFactory();
						JAXBElement<SiteFrame> jaxSiteFrame = objectFactory.createSiteFrame(siteFrame);

						PublicationDeliveryStructure respoonse = new PublicationDeliveryStructure()
								.withDescription(
										new MultilingualString().withValue("Publication delivery from chouette")
												.withLang("no").withTextIdType(""))
								.withPublicationTimestamp(OffsetDateTime.now()).withParticipantRef("participantRef")
								.withDataObjects(new PublicationDeliveryStructure.DataObjects()
										.withCompositeFrameOrCommonFrame(Arrays.asList(jaxSiteFrame)));
						return respoonse;

					}

					protected KeyListStructure createKeyListStructure(String value) {
						KeyListStructure kl = new KeyListStructure();
						KeyValueStructure kv = new KeyValueStructure();
						kv.setKey(NeTExStopPlaceRegisterUpdater.IMPORTED_ID);
						kv.setValue(value);
						kl.getKeyValue().add(kv);
						return kl;
					}
				});

		// Call update
		neTExStopPlaceRegisterUpdater.update(context, referential);

		// Assert stopPoints changed
		Assert.assertEquals(sp1.getContainedInStopArea().getObjectId(), "NHR:StopArea:1");
		Assert.assertEquals(sp2.getContainedInStopArea().getObjectId(), "NHR:StopArea:2");

	}

}