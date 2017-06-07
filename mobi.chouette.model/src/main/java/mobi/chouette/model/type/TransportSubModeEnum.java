package mobi.chouette.model.type;

import java.util.EnumSet;

public enum TransportSubModeEnum {

    /**
     * Bus sub modes
     */
    AirportLinkBus,
    ExpressBus,
    LocalBus,
    NightBus,
    RailReplacementBus,
    RegionalBus,
    SchoolBus,
    ShuttleBus,
    SightseeingBus,

    /**
     * Tram sub modes
     */
    LocalTram,

    /**
     * Rail sub modes
     */
    International,
    InterregionalRail,
    Local,
    LongDistance,
    NightRail,
    RegionalRail,
    TouristRailway,

    /**
     * Metro sub modes
     */
    Metro,

    /**
     * Air sub modes
     */
    DomesticFlight,
    HelicopterService,
    InternationalFlight,

    /**
     * Water sub modes
     */
    HighSpeedPassengerService,
    HighSpeedVehicleService,
    InternationalCarFerry,
    InternationalPassengerFerry,
    LocalCarFerry,
    LocalPassengerFerry,
    NationalCarFerry,
    SightseeingService,

    /**
     * Cabelway sub modes
     */
    Telecabin,

    /**
     * Funicular sub modes
     */
    Funicular;

    public static final EnumSet<TransportSubModeEnum> CABELWAY_SUB_MODES = EnumSet.of(TransportSubModeEnum.Telecabin);
    public static final EnumSet<TransportSubModeEnum> FUNICULAR_SUB_MODES = EnumSet.of(TransportSubModeEnum.Funicular);
    public static final EnumSet<TransportSubModeEnum> METRO_SUB_MODES = EnumSet.of(TransportSubModeEnum.Metro);
    public static final EnumSet<TransportSubModeEnum> TRAM_SUB_MODES = EnumSet.of(TransportSubModeEnum.LocalTram);

    public static final EnumSet<TransportSubModeEnum> AIR_SUB_MODES = EnumSet.of(
            TransportSubModeEnum.DomesticFlight,
            TransportSubModeEnum.HelicopterService,
            TransportSubModeEnum.InternationalFlight
    );

    public static final EnumSet<TransportSubModeEnum> RAIL_SUB_MODES = EnumSet.of(
            TransportSubModeEnum.International,
            TransportSubModeEnum.InterregionalRail,
            TransportSubModeEnum.Local,
            TransportSubModeEnum.LongDistance,
            TransportSubModeEnum.NightRail,
            TransportSubModeEnum.RegionalRail,
            TransportSubModeEnum.TouristRailway
    );

    public static final EnumSet<TransportSubModeEnum> WATER_SUB_MODES = EnumSet.of(
            TransportSubModeEnum.HighSpeedPassengerService,
            TransportSubModeEnum.HighSpeedVehicleService,
            TransportSubModeEnum.InternationalCarFerry,
            TransportSubModeEnum.InternationalPassengerFerry,
            TransportSubModeEnum.LocalCarFerry,
            TransportSubModeEnum.LocalPassengerFerry,
            TransportSubModeEnum.NationalCarFerry,
            TransportSubModeEnum.SightseeingService
    );

    public static final EnumSet<TransportSubModeEnum> BUS_SUB_MODES = EnumSet.of(
            TransportSubModeEnum.AirportLinkBus,
            TransportSubModeEnum.ExpressBus,
            TransportSubModeEnum.LocalBus,
            TransportSubModeEnum.NightBus,
            TransportSubModeEnum.RailReplacementBus,
            TransportSubModeEnum.RegionalBus,
            TransportSubModeEnum.SchoolBus,
            TransportSubModeEnum.ShuttleBus,
            TransportSubModeEnum.SightseeingBus
    );

}
