package mobi.chouette.exchange.netexprofile.parser;

import java.util.HashMap;

import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeNameEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

import org.rutebanken.netex.model.AirSubmodeEnumeration;
import org.rutebanken.netex.model.BusSubmodeEnumeration;
import org.rutebanken.netex.model.FunicularSubmodeEnumeration;
import org.rutebanken.netex.model.MetroSubmodeEnumeration;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.RailSubmodeEnumeration;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.TelecabinSubmodeEnumeration;
import org.rutebanken.netex.model.TramSubmodeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.rutebanken.netex.model.WaterSubmodeEnumeration;
import org.testng.Assert;
import org.testng.annotations.Test;

import static mobi.chouette.common.Constant.REFERENTIAL;

public class StopPlaceParserTest {


    @Test
    public void parseStopPlaceMapsTransportAndAreaTypes() throws Exception {
        StopPlaceParser parser = new StopPlaceParser();

        Context context = new Context();
        Referential referential = new Referential();
        context.put(REFERENTIAL, referential);


        StopPlace netexStopPlace = new StopPlace();
        netexStopPlace.setId("TST:StopPlace:Id");
        netexStopPlace.setName(new MultilingualString().withValue("name"));
        netexStopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        netexStopPlace.setTransportMode(VehicleModeEnumeration.RAIL);
        netexStopPlace.setRailSubmode(RailSubmodeEnumeration.INTERNATIONAL);
        parser.parseStopPlace(context, netexStopPlace, new HashMap<>(), new HashMap<>());

        StopArea stopArea = ObjectFactory.getStopArea(referential, netexStopPlace.getId());
        Assert.assertEquals(StopAreaTypeEnum.RailStation, stopArea.getStopAreaType());
        Assert.assertEquals(TransportModeNameEnum.Rail, stopArea.getTransportModeName());
        Assert.assertEquals(TransportSubModeNameEnum.International, stopArea.getTransportSubMode());
    }

    @Test
    public void mapStopAreaTypeMapsByName() {
        StopPlaceParser parser = new StopPlaceParser();

        Assert.assertNull(parser.mapStopAreaType(null));
        for (StopTypeEnumeration netexType : StopTypeEnumeration.values()) {
            StopAreaTypeEnum stopAreaTypeEnum = parser.mapStopAreaType(netexType);
            Assert.assertNotNull(stopAreaTypeEnum);
            Assert.assertEquals(stopAreaTypeEnum.name().toLowerCase(), netexType.value().toLowerCase());
        }
    }

    @Test
    public void testMapWaterSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.HIGH_SPEED_PASSENGER_SERVICE.value()), TransportSubModeNameEnum.HighSpeedPassengerService);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.HIGH_SPEED_VEHICLE_SERVICE.value()), TransportSubModeNameEnum.HighSpeedVehicleService);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.INTERNATIONAL_CAR_FERRY.value()), TransportSubModeNameEnum.InternationalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.INTERNATIONAL_PASSENGER_FERRY.value()), TransportSubModeNameEnum.InternationalPassengerFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.LOCAL_CAR_FERRY.value()), TransportSubModeNameEnum.LocalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.LOCAL_PASSENGER_FERRY.value()), TransportSubModeNameEnum.LocalPassengerFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.NATIONAL_CAR_FERRY.value()), TransportSubModeNameEnum.NationalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.SIGHTSEEING_SERVICE.value()), TransportSubModeNameEnum.SightseeingService);
    }

    @Test
    public void testMapAirSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.DOMESTIC_FLIGHT.value()), TransportSubModeNameEnum.DomesticFlight);
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.HELICOPTER_SERVICE.value()), TransportSubModeNameEnum.HelicopterService);
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.INTERNATIONAL_FLIGHT.value()), TransportSubModeNameEnum.InternationalFlight);
    }

    @Test
    public void testMapBusSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.AIRPORT_LINK_BUS.value()), TransportSubModeNameEnum.AirportLinkBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.EXPRESS_BUS.value()), TransportSubModeNameEnum.ExpressBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.LOCAL_BUS.value()), TransportSubModeNameEnum.LocalBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.NIGHT_BUS.value()), TransportSubModeNameEnum.NightBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS.value()), TransportSubModeNameEnum.RailReplacementBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.REGIONAL_BUS.value()), TransportSubModeNameEnum.RegionalBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SCHOOL_BUS.value()), TransportSubModeNameEnum.SchoolBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SHUTTLE_BUS.value()), TransportSubModeNameEnum.ShuttleBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SIGHTSEEING_BUS.value()), TransportSubModeNameEnum.SightseeingBus);
    }

    @Test
    public void testMapTailSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.INTERNATIONAL.value()), TransportSubModeNameEnum.International);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.INTERREGIONAL_RAIL.value()), TransportSubModeNameEnum.InterregionalRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.LOCAL.value()), TransportSubModeNameEnum.Local);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.LONG_DISTANCE.value()), TransportSubModeNameEnum.LongDistance);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.NIGHT_RAIL.value()), TransportSubModeNameEnum.NightRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.REGIONAL_RAIL.value()), TransportSubModeNameEnum.RegionalRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.TOURIST_RAILWAY.value()), TransportSubModeNameEnum.TouristRailway);
    }

    @Test
    public void testMapTramSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(TramSubmodeEnumeration.LOCAL_TRAM.value()), TransportSubModeNameEnum.LocalTram);
    }

    @Test
    public void testMapFunicularSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(FunicularSubmodeEnumeration.FUNICULAR.value()), TransportSubModeNameEnum.Funicular);
    }

    @Test
    public void testMapMetroSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(MetroSubmodeEnumeration.METRO.value()), TransportSubModeNameEnum.Metro);
    }

    @Test
    public void testCablewaySubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(TelecabinSubmodeEnumeration.TELECABIN.value()), TransportSubModeNameEnum.Telecabin);
    }


    @Test
    public void testTransportMode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertNull(parser.mapTransportModeName(null));
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.AIR), TransportModeNameEnum.Air);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.BUS), TransportModeNameEnum.Bus);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.RAIL), TransportModeNameEnum.Rail);
       // Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TAXI), TransportModeNameEnum.Taxi);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TRAM), TransportModeNameEnum.Tram);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.COACH), TransportModeNameEnum.Coach);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.FERRY), TransportModeNameEnum.Ferry);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.METRO), TransportModeNameEnum.Metro);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.WATER), TransportModeNameEnum.Water);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.CABLEWAY), TransportModeNameEnum.Cableway);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.FUNICULAR), TransportModeNameEnum.Funicular);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TROLLEY_BUS), TransportModeNameEnum.TrolleyBus);

        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.OTHER), TransportModeNameEnum.Other);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.OTHER), TransportModeNameEnum.Other);

    }
}
