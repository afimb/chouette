package mobi.chouette.exchange.stopplace;

import com.google.common.collect.Sets;
import mobi.chouette.model.StopArea;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.Set;

public class PublicationDeliveryStopPlaceParserTest {

    @Test
    public void testParseStopPlacesFromPublicationDelivery() throws Exception {
        Set<String> expectedIds= Sets.newHashSet("NSR:StopPlace:51566","NSR:Quay:87130","NSR:Quay:87131","NSR:StopPlace:10089","NSR:Quay:50496","NSR:Quay:50502");
        Collection<StopArea> stopAreas = new PublicationDeliveryStopPlaceParser().parseStopPlaces(new FileInputStream("src/test/resources/netex/PublicationDeliveryWithStopPlaces.xml"));
        Assert.assertEquals(stopAreas.size(), expectedIds.size());
        Assert.assertTrue(stopAreas.stream().allMatch(sa -> expectedIds.remove(sa.getObjectId())));
    }
}
