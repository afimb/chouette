package mobi.chouette.model.type;

import java.util.EnumSet;

public enum TransportSubModeNameEnum {

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
    AirportLinkRail,

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
    Funicular,
	
	/**
	 * Coach sub modes
	 */
	InternationalCoach,
	NationalCoach,
	TouristCoach;

    public static final EnumSet<TransportSubModeNameEnum> CABELWAY_SUB_MODES = EnumSet.of(TransportSubModeNameEnum.Telecabin);
    public static final EnumSet<TransportSubModeNameEnum> FUNICULAR_SUB_MODES = EnumSet.of(TransportSubModeNameEnum.Funicular);

    public static final EnumSet<TransportSubModeNameEnum> AIR_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.DomesticFlight,
            TransportSubModeNameEnum.HelicopterService,
            TransportSubModeNameEnum.InternationalFlight
    );

    public static final EnumSet<TransportSubModeNameEnum> RAIL_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.International,
            TransportSubModeNameEnum.InterregionalRail,
            TransportSubModeNameEnum.Local,
            TransportSubModeNameEnum.LongDistance,
            TransportSubModeNameEnum.NightRail,
            TransportSubModeNameEnum.RegionalRail,
            TransportSubModeNameEnum.TouristRailway,
            TransportSubModeNameEnum.AirportLinkRail
    );

    public static final EnumSet<TransportSubModeNameEnum> FERRY_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.InternationalCarFerry,
            TransportSubModeNameEnum.InternationalPassengerFerry,
            TransportSubModeNameEnum.LocalCarFerry,
            TransportSubModeNameEnum.LocalPassengerFerry,
            TransportSubModeNameEnum.NationalCarFerry
    );

    public static final EnumSet<TransportSubModeNameEnum> WATER_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.HighSpeedPassengerService,
            TransportSubModeNameEnum.HighSpeedVehicleService,
            TransportSubModeNameEnum.InternationalCarFerry,
            TransportSubModeNameEnum.InternationalPassengerFerry,
            TransportSubModeNameEnum.LocalCarFerry,
            TransportSubModeNameEnum.LocalPassengerFerry,
            TransportSubModeNameEnum.NationalCarFerry,
            TransportSubModeNameEnum.SightseeingService
    );

    public static final EnumSet<TransportSubModeNameEnum> BUS_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.AirportLinkBus,
            TransportSubModeNameEnum.ExpressBus,
            TransportSubModeNameEnum.LocalBus,
            TransportSubModeNameEnum.NightBus,
            TransportSubModeNameEnum.RailReplacementBus,
            TransportSubModeNameEnum.RegionalBus,
            TransportSubModeNameEnum.SchoolBus,
            TransportSubModeNameEnum.ShuttleBus,
            TransportSubModeNameEnum.SightseeingBus
    );

    public static final EnumSet<TransportSubModeNameEnum> COACH_SUB_MODES = EnumSet.of(
            TransportSubModeNameEnum.InternationalCoach,
            TransportSubModeNameEnum.NationalCoach,
            TransportSubModeNameEnum.TouristCoach
    );

}
