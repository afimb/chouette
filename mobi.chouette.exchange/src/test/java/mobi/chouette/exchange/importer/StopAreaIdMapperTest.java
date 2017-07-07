package mobi.chouette.exchange.importer;

import java.util.HashMap;
import java.util.Map;

import mobi.chouette.exchange.importer.updater.StopAreaIdCache;
import mobi.chouette.exchange.importer.updater.StopAreaIdMapper;
import mobi.chouette.model.Line;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.util.Referential;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.StopTypeEnumeration;

public class StopAreaIdMapperTest {

    private StopAreaIdMapper stopAreaIdMapper;
    StopAreaIdCacheMock cache = new StopAreaIdCacheMock();

    @Before
    public void setUp() {
        stopAreaIdMapper = new StopAreaIdMapper();

        stopAreaIdMapper.setStopAreaIdCache(cache);
    }


    @Test
    public void testMapIdsForStopAreasWithQuaysOnlyOneMappingForOrgId() {
        StopArea stopPlace = new StopArea();
        stopPlace.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
        stopPlace.setObjectId("orgIdSP");

        StopArea quay1 = new StopArea();
        quay1.setAreaType(ChouetteAreaEnum.BoardingPosition);
        quay1.setObjectId("orgIdQ1");
        quay1.setParent(stopPlace);

        StopArea quay2 = new StopArea();
        quay2.setAreaType(ChouetteAreaEnum.BoardingPosition);
        quay2.setObjectId("orgIdQ2");
        quay2.setParent(stopPlace);


        Referential referential = new Referential();
        referential.getStopAreas().put(stopPlace.getObjectId(), stopPlace);

        String stopNewId = "newIdSP";
        String quay1NewId = "newIdQ1";
        String quay2NewId = "newIdQ2";
        cache.clear();
        cache.addStopPlaceMapping(stopPlace.getObjectId(), null, stopNewId);
        cache.addQuayMapping(quay1.getObjectId(), null, quay1NewId);
        cache.addQuayMapping(quay2.getObjectId(), null, quay2NewId);

        stopAreaIdMapper.mapStopAreaIds(referential);

        Assert.assertEquals(1, referential.getStopAreas().size());

        StopArea mappedStopArea = referential.getStopAreas().get(stopNewId);
        Assert.assertNotNull("Expected map to contain entry for mapped stop place id", mappedStopArea);
        Assert.assertEquals("Expected stop to have id updated by mapping", stopNewId, mappedStopArea.getObjectId());

        Assert.assertEquals(mappedStopArea.getContainedStopAreas().size(), 2);
        Assert.assertTrue("Expected quay1 to have id updated by mapping", mappedStopArea.getContainedStopAreas().stream().anyMatch(mappedQuay -> quay1NewId.equals(mappedQuay.getObjectId())));
        Assert.assertTrue("Expected quay2 to have id updated by mapping", mappedStopArea.getContainedStopAreas().stream().anyMatch(mappedQuay -> quay2NewId.equals(mappedQuay.getObjectId())));
    }

    @Test
    public void testMapIdsForStopAreasWithQuaysMultipleMappingsForOrgId() {
        StopArea stopPlace = new StopArea();
        stopPlace.setAreaType(ChouetteAreaEnum.CommercialStopPoint);
        stopPlace.setObjectId("orgIdSP");

        StopArea quay1 = new StopArea();
        quay1.setAreaType(ChouetteAreaEnum.BoardingPosition);
        quay1.setObjectId("orgIdQ1");
        setTransportModeForQuay(quay1,TransportModeNameEnum.Coach);
        quay1.setParent(stopPlace);

        StopArea quay2 = new StopArea();
        quay2.setAreaType(ChouetteAreaEnum.BoardingPosition);
        quay2.setObjectId("orgIdQ2");
        quay2.setParent(stopPlace);
        setTransportModeForQuay(quay2,TransportModeNameEnum.Coach);


        Referential referential = new Referential();
        referential.getStopAreas().put(stopPlace.getObjectId(), stopPlace);

        String stopNewId = "newIdSP";
        String quay1NewId = "newIdQ1";
        String quay2NewId = "newIdQ2";
        cache.clear();
        cache.addStopPlaceMapping(stopPlace.getObjectId(), StopTypeEnumeration.AIRPORT, stopNewId + "-airport");
        cache.addQuayMapping(quay1.getObjectId(), StopTypeEnumeration.AIRPORT, quay1NewId + "-airport");
        cache.addQuayMapping(quay2.getObjectId(), StopTypeEnumeration.AIRPORT, quay2NewId + "-airport");

        cache.addStopPlaceMapping(stopPlace.getObjectId(), StopTypeEnumeration.ONSTREET_BUS, stopNewId);
        cache.addQuayMapping(quay1.getObjectId(), StopTypeEnumeration.ONSTREET_BUS, quay1NewId);
        cache.addQuayMapping(quay2.getObjectId(), StopTypeEnumeration.ONSTREET_BUS, quay2NewId);

        stopAreaIdMapper.mapStopAreaIds(referential);

        Assert.assertEquals(1, referential.getStopAreas().size());

        StopArea mappedStopArea = referential.getStopAreas().get(stopNewId);
        Assert.assertNotNull("Expected map to contain entry for mapped stop place id", mappedStopArea);
        Assert.assertEquals("Expected stop to have id updated by mapping", stopNewId, mappedStopArea.getObjectId());

        Assert.assertEquals(mappedStopArea.getContainedStopAreas().size(), 2);
        Assert.assertTrue("Expected quay1 to have id updated by mapping", mappedStopArea.getContainedStopAreas().stream().anyMatch(mappedQuay -> quay1NewId.equals(mappedQuay.getObjectId())));
        Assert.assertTrue("Expected quay2 to have id updated by mapping", mappedStopArea.getContainedStopAreas().stream().anyMatch(mappedQuay -> quay2NewId.equals(mappedQuay.getObjectId())));
    }


    private void setTransportModeForQuay(StopArea quay, TransportModeNameEnum transportModeName){
        StopPoint sp=new StopPoint();
        sp.setContainedInStopArea(quay);
        Route route=new Route();
        sp.setRoute(route);
        Line line=new Line();
        route.setLine(line);
        line.setTransportModeName(transportModeName);
    }


    private class StopAreaIdCacheMock extends StopAreaIdCache {

        private Map<String, Map<StopTypeEnumeration, String>> stopPlaceMap = new HashMap<>();
        private Map<String, Map<StopTypeEnumeration, String>> quayMap = new HashMap<>();


        public void clear() {
            stopPlaceMap.clear();
            quayMap.clear();
        }

        private void addStopPlaceMapping(String orgId, StopTypeEnumeration type, String newId) {
            updateCache(stopPlaceMap, orgId, type, newId);
        }

        private void addQuayMapping(String orgId, StopTypeEnumeration type, String newId) {
            updateCache(quayMap, orgId, type, newId);
        }


        private void updateCache(Map<String, Map<StopTypeEnumeration, String>> cache, String orgId, StopTypeEnumeration type, String newId) {
            Map<StopTypeEnumeration, String> typeMap = cache.get(orgId);
            if (typeMap == null) {
                typeMap = new HashMap<>();
            }
            typeMap.put(type, newId);
            cache.put(orgId, typeMap);
        }


        @Override
        public Map<StopTypeEnumeration, String> getStopPlaceMapping(String localId) {
            return stopPlaceMap.get(localId);
        }


        @Override
        public Map<StopTypeEnumeration, String> getQuayMapping(String localId) {
            return quayMap.get(localId);
        }
    }

}