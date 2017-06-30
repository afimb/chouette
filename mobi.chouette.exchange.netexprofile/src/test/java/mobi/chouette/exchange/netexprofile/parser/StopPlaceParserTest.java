package mobi.chouette.exchange.netexprofile.parser;

import java.util.HashMap;

import mobi.chouette.common.Context;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.StopAreaTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;
import mobi.chouette.model.type.TransportSubModeEnum;
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
        parser.parseStopPlace(context, netexStopPlace, new HashMap<>());

        StopArea stopArea = ObjectFactory.getStopArea(referential, netexStopPlace.getId());
        Assert.assertEquals(StopAreaTypeEnum.RailStation, stopArea.getStopAreaType());
        Assert.assertEquals(TransportModeNameEnum.Train, stopArea.getTransportModeName());
        Assert.assertEquals(TransportSubModeEnum.International, stopArea.getTransportSubMode());
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
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.HIGH_SPEED_PASSENGER_SERVICE.value()), TransportSubModeEnum.HighSpeedPassengerService);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.HIGH_SPEED_VEHICLE_SERVICE.value()), TransportSubModeEnum.HighSpeedVehicleService);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.INTERNATIONAL_CAR_FERRY.value()), TransportSubModeEnum.InternationalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.INTERNATIONAL_PASSENGER_FERRY.value()), TransportSubModeEnum.InternationalPassengerFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.LOCAL_CAR_FERRY.value()), TransportSubModeEnum.LocalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.LOCAL_PASSENGER_FERRY.value()), TransportSubModeEnum.LocalPassengerFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.NATIONAL_CAR_FERRY.value()), TransportSubModeEnum.NationalCarFerry);
        Assert.assertEquals(parser.mapTransportSubMode(WaterSubmodeEnumeration.SIGHTSEEING_SERVICE.value()), TransportSubModeEnum.SightseeingService);
    }

    @Test
    public void testMapAirSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.DOMESTIC_FLIGHT.value()), TransportSubModeEnum.DomesticFlight);
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.HELICOPTER_SERVICE.value()), TransportSubModeEnum.HelicopterService);
        Assert.assertEquals(parser.mapTransportSubMode(AirSubmodeEnumeration.INTERNATIONAL_FLIGHT.value()), TransportSubModeEnum.InternationalFlight);
    }

    @Test
    public void testMapBusSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.AIRPORT_LINK_BUS.value()), TransportSubModeEnum.AirportLinkBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.EXPRESS_BUS.value()), TransportSubModeEnum.ExpressBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.LOCAL_BUS.value()), TransportSubModeEnum.LocalBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.NIGHT_BUS.value()), TransportSubModeEnum.NightBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS.value()), TransportSubModeEnum.RailReplacementBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.REGIONAL_BUS.value()), TransportSubModeEnum.RegionalBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SCHOOL_BUS.value()), TransportSubModeEnum.SchoolBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SHUTTLE_BUS.value()), TransportSubModeEnum.ShuttleBus);
        Assert.assertEquals(parser.mapTransportSubMode(BusSubmodeEnumeration.SIGHTSEEING_BUS.value()), TransportSubModeEnum.SightseeingBus);
    }

    @Test
    public void testMapTailSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.INTERNATIONAL.value()), TransportSubModeEnum.International);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.INTERREGIONAL_RAIL.value()), TransportSubModeEnum.InterregionalRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.LOCAL.value()), TransportSubModeEnum.Local);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.LONG_DISTANCE.value()), TransportSubModeEnum.LongDistance);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.NIGHT_RAIL.value()), TransportSubModeEnum.NightRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.REGIONAL_RAIL.value()), TransportSubModeEnum.RegionalRail);
        Assert.assertEquals(parser.mapTransportSubMode(RailSubmodeEnumeration.TOURIST_RAILWAY.value()), TransportSubModeEnum.TouristRailway);
    }

    @Test
    public void testMapTramSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(TramSubmodeEnumeration.LOCAL_TRAM.value()), TransportSubModeEnum.LocalTram);
    }

    @Test
    public void testMapFunicularSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(FunicularSubmodeEnumeration.FUNICULAR.value()), TransportSubModeEnum.Funicular);
    }

    @Test
    public void testMapMetroSubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(MetroSubmodeEnumeration.METRO.value()), TransportSubModeEnum.Metro);
    }

    @Test
    public void testCablewaySubmode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertEquals(parser.mapTransportSubMode(TelecabinSubmodeEnumeration.TELECABIN.value()), TransportSubModeEnum.Telecabin);
    }


    @Test
    public void testTransportMode() {
        StopPlaceParser parser = new StopPlaceParser();
        Assert.assertNull(parser.mapTransportModeName(null));
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.AIR), TransportModeNameEnum.Air);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.BUS), TransportModeNameEnum.Bus);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.RAIL), TransportModeNameEnum.Train);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TAXI), TransportModeNameEnum.Taxi);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TRAM), TransportModeNameEnum.Tramway);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.COACH), TransportModeNameEnum.Coach);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.FERRY), TransportModeNameEnum.Ferry);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.METRO), TransportModeNameEnum.Metro);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.WATER), TransportModeNameEnum.Waterborne);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.CABLEWAY), TransportModeNameEnum.Cabelway);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.FUNICULAR), TransportModeNameEnum.Funicular);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.TROLLEY_BUS), TransportModeNameEnum.Trolleybus);

        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.OTHER), TransportModeNameEnum.Other);
        Assert.assertEquals(parser.mapTransportModeName(VehicleModeEnumeration.OTHER), TransportModeNameEnum.Other);

    }
}
