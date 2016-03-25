package mobi.chouette.exchange.gtfs.model;

public enum RouteTypeEnum {

	Tram(0), 
	Subway(1), 
	Rail(2), 
	Bus(3), 
	Ferry(4), 
	Cable(5), 
	Gondola(6), 
	Funicular(7),
	// extension Rail
	RailwayService(100),
	HighSpeedRailService(101),
	LongDistanceTrains(102),
	InterRegionalRailService(103),
	CarTransportRailService(104),
	SleeperRailService(105),
	RegionalRailService(106),
	TouristRailwayService(107),
	RailShuttleWithinComplex(108),
	SuburbanRailway(109),
	ReplacementRailService(110),
	SpecialRailService(111),
	LorryTransportRailService(112),
	AllRailServices(113),
	CrossCountryRailService(114),
	VehicleTransportRailService(115),
	RackandPinionRailway(116),
	AdditionalRailService(117),
	// extension Coach
	CoachService(200),
	InternationalCoachService(201),
	NationalCoachService(202),
	ShuttleCoachService(203),
	RegionalCoachService(204),
	SpecialCoachService(205),
	SightseeingCoachService(206),
	TouristCoachService(207),
	CommuterCoachService(208),
	AllCoachServices(209),
	// extension 
	SuburbanRailwayService(300),
	// extension 
	UrbanRailwayService(400),
	MetroService(401),
	UndergroundService(402),
	UrbanRailwayService2(403),
	AllUrbanRailwayServices(404),
	Monorail(405),
	// extension 
	MetroService2(500),
	// extension 
	UndergroundService2(600),
	// extension 
	BusService(700),
	RegionalBusService(701),
	ExpressBusService(702),
	StoppingBusService(703),
	LocalBusService(704),
	NightBusService(705),
	PostBusService(706),
	SpecialNeedsBus(707),
	MobilityBusService(708),
	MobilityBusforRegisteredDisabled(709),
	SightseeingBus(710),
	ShuttleBus(711),
	SchoolBus(712),
	SchoolandPublicServiceBus(713),
	RailReplacementBusService(714),
	DemandandResponseBusService(715),
	AllBusServices(716),
	// extension 
	TrolleybusService(800),
	// extension 
	TramService(900),
	CityTramService(901),
	LocalTramService(902),
	RegionalTramService(903),
	SightseeingTramService(904),
	ShuttleTramService(905),
	AllTramServices(906),
	// extension 
	WaterTransportService(1000),
	InternationalCarFerryService(1001),
	NationalCarFerryService(1002),
	RegionalCarFerryService(1003),
	LocalCarFerryService(1004),
	InternationalPassengerFerryService(1005),
	NationalPassengerFerryService(1006),
	RegionalPassengerFerryService(1007),
	LocalPassengerFerryService(1008),
	PostBoatService(1009),
	TrainFerryService(1010),
	RoadLinkFerryService(1011),
	AirportLinkFerryService(1012),
	CarHighSpeedFerryService(1013),
	PassengerHighSpeedFerryService(1014),
	SightseeingBoatService(1015),
	SchoolBoat(1016),
	CableDrawnBoatService(1017),
	RiverBusService(1018),
	ScheduledFerryService(1019),
	ShuttleFerryService(1020),
	AllWaterTransportServices(1021),
	// extension 
	AirService(1100),
	InternationalAirService(1101),
	DomesticAirService(1102),
	IntercontinentalAirService(1103),
	DomesticScheduledAirService(1104),
	ShuttleAirService(1105),
	IntercontinentalCharterAirService(1106),
	InternationalCharterAirService(1107),
	RoundTripCharterAirService(1108),
	SightseeingAirService(1109),
	HelicopterAirService(1110),
	DomesticCharterAirService(1111),
	SchengenAreaAirService(1112),
	AirshipService(1113),
	AllAirServices(1114),
	// extension 
	FerryService(1200),
	// extension 
	TelecabinService(1300),
	TelecabinService2(1301),
	CableCarService(1302),
	ElevatorService(1303),
	ChairLiftService(1304),
	DragLiftService(1305),
	SmallTelecabinService(1306),
	AllTelecabinServices(1307),
	// extension 
	FunicularService(1400),
	FunicularService2(1401),
	AllFunicularService(1402),
	// extension 
	TaxiService(1500),
	CommunalTaxiService(1501),
	WaterTaxiService(1502),
	RailTaxiService(1503),
	BikeTaxiService(1504),
	LicensedTaxiService(1505),
	PrivateHireServiceVehicle(1506),
	AllTaxiServices(1507),
	// extension 
	SelfDrive(1600),
	HireCar(1601),
	HireVan(1602),
	HireMotorbike(1603),
	HireCycle(1604),
	// extension 
	MiscellaneousService(1700),
	CableCar(1701),
	HorseDrawnCarriage(1702),
;
	private final int value;

	private RouteTypeEnum(final int value) {
		this.value = value;
	}

	public static RouteTypeEnum fromValue(final int value) {
		for (RouteTypeEnum c : RouteTypeEnum.values()) {
			if (c.value == value) {
				return c;
			}
		}
		throw new IllegalArgumentException(Integer.toString(value));
	}

	public int value() {
		return this.value;
	}
}
