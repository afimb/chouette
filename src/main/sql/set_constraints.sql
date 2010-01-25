-- call by :  psql -U [user] -W -f set_constraints.sql -v SCH=[new schema] [database name]

-- refresh constraints

ALTER TABLE :SCH.line DROP CONSTRAINT line_company;

ALTER TABLE :SCH.line
  ADD CONSTRAINT line_company FOREIGN KEY (companyId)
      REFERENCES :SCH.company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.line DROP CONSTRAINT line_ptnetwork;

ALTER TABLE :SCH.line
  ADD CONSTRAINT line_ptnetwork FOREIGN KEY (PTNetworkId)
      REFERENCES :SCH.ptnetwork (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE :SCH.route DROP CONSTRAINT route_line;
ALTER TABLE :SCH.route
  ADD CONSTRAINT route_line FOREIGN KEY (lineId)
      REFERENCES :SCH.line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE :SCH.stoppoint DROP CONSTRAINT stoppoint_stoparea;

ALTER TABLE :SCH.stoppoint
  ADD CONSTRAINT stoppoint_stoparea FOREIGN KEY (stopAreaId)
      REFERENCES :SCH.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.stoppoint DROP CONSTRAINT stoppoint_route;

ALTER TABLE :SCH.stoppoint
  ADD CONSTRAINT stoppoint_route FOREIGN KEY (routeId)
      REFERENCES :SCH.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE :SCH.timetablevehiclejourney DROP CONSTRAINT tmvehicle_tm;

ALTER TABLE :SCH.timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_tm FOREIGN KEY (timetableId)
      REFERENCES :SCH.timetable (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.timetablevehiclejourney DROP CONSTRAINT tmvehicle_vehicle;

ALTER TABLE :SCH.timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_vehicle FOREIGN KEY (vehicleJourneyId)
      REFERENCES :SCH.vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.vehiclejourney DROP CONSTRAINT vehiclejourney_journeypattern;

ALTER TABLE :SCH.vehiclejourney
  ADD CONSTRAINT vehiclejourney_journeypattern FOREIGN KEY (journeyPatternId)
      REFERENCES :SCH.journeypattern (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.vehiclejourney DROP CONSTRAINT vehicle_route;

ALTER TABLE :SCH.vehiclejourney
  ADD CONSTRAINT vehicle_route FOREIGN KEY (routeId)
      REFERENCES :SCH.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE :SCH.vehiclejourneyatstop DROP CONSTRAINT vjas_course;

ALTER TABLE :SCH.vehiclejourneyatstop
  ADD CONSTRAINT vjas_course FOREIGN KEY (vehiclejourneyId)
      REFERENCES :SCH.vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.vehiclejourneyatstop DROP CONSTRAINT vjas_stoppoint;

ALTER TABLE :SCH.vehiclejourneyatstop
  ADD CONSTRAINT vjas_stoppoint FOREIGN KEY (stopPointId)
      REFERENCES :SCH.stoppoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.stoparea DROP CONSTRAINT stoparea_parent;

ALTER TABLE :SCH.stoparea
  ADD CONSTRAINT stoparea_parent FOREIGN KEY (parentId)
      REFERENCES :SCH.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      		
ALTER TABLE :SCH.route DROP CONSTRAINT route_parent;

ALTER TABLE :SCH.route
  ADD CONSTRAINT route_parent FOREIGN KEY (oppositeRouteId)
      REFERENCES :SCH.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.connectionlink DROP CONSTRAINT connection_stoparea_departure;

ALTER TABLE :SCH.connectionlink
  ADD CONSTRAINT connection_stoparea_departure FOREIGN KEY (departureId)
      REFERENCES :SCH.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
ALTER TABLE :SCH.connectionlink DROP CONSTRAINT connection_stoparea_arrival;

ALTER TABLE :SCH.connectionlink
  ADD CONSTRAINT connection_stoparea_arrival FOREIGN KEY (arrivalId)
      REFERENCES :SCH.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.routingconstraint DROP CONSTRAINT routingconstraint_line;

ALTER TABLE :SCH.routingconstraint
  ADD CONSTRAINT routingconstraint_line FOREIGN KEY (lineId)
      REFERENCES :SCH.line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.routingconstraint_stoparea DROP CONSTRAINT rconstraint_stoparea_rconstraint;

ALTER TABLE :SCH.routingconstraint_stoparea
  ADD CONSTRAINT rconstraint_stoparea_rconstraint FOREIGN KEY (routingConstraintId)
      REFERENCES :SCH.routingconstraint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE :SCH.routingconstraint_stoparea DROP CONSTRAINT rconstraint_stoparea_stoparea;

ALTER TABLE :SCH.routingconstraint_stoparea
  ADD CONSTRAINT rconstraint_stoparea_stoparea FOREIGN KEY (stopareaId)
      REFERENCES :SCH.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      		

-- Add comment on tables and columns
COMMENT ON TABLE :SCH.company IS 'company operating a public transport service';

COMMENT ON COLUMN :SCH.company.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.company.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.company.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.company.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.company.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.company."name" IS 'Name';
COMMENT ON COLUMN :SCH.company.shortname IS 'Short name';
COMMENT ON COLUMN :SCH.company.organizationalunit IS 'Organizational Unit';
COMMENT ON COLUMN :SCH.company.operatingdepartmentname IS 'Opeating department';
COMMENT ON COLUMN :SCH.company.code IS 'Zip code';
COMMENT ON COLUMN :SCH.company.phone IS 'Phone number';
COMMENT ON COLUMN :SCH.company.fax IS 'FAX number';
COMMENT ON COLUMN :SCH.company.email IS 'Email';
COMMENT ON COLUMN :SCH.company.registrationnumber IS 'Registration number';

COMMENT ON TABLE :SCH.connectionlink IS 'Connection link between 2 stopareas';

COMMENT ON COLUMN :SCH.connectionlink.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.connectionlink.departureid IS 'Start stoparea internal id';
COMMENT ON COLUMN :SCH.connectionlink.arrivalid IS 'End stoparea internal id';
COMMENT ON COLUMN :SCH.connectionlink.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.connectionlink.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.connectionlink.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.connectionlink.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.connectionlink."name" IS 'Name';
COMMENT ON COLUMN :SCH.connectionlink."comment" IS 'Comment';
COMMENT ON COLUMN :SCH.connectionlink.linkdistance IS 'Length in meter';
COMMENT ON COLUMN :SCH.connectionlink.linktype IS 'Type (Underground,Overground or Mixed)';
COMMENT ON COLUMN :SCH.connectionlink.defaultduration IS 'average duration of travel';
COMMENT ON COLUMN :SCH.connectionlink.frequenttravellerduration IS 'duration of travel for frequent traveller';
COMMENT ON COLUMN :SCH.connectionlink.occasionaltravellerduration IS 'duration of travel for occasional traveler';
COMMENT ON COLUMN :SCH.connectionlink.mobilityrestrictedtravellerduration IS 'duration of travel for mobiliy restricted traveller';
COMMENT ON COLUMN :SCH.connectionlink.mobilityrestrictedsuitability IS 'link is useable for mobility restricted traveller';
COMMENT ON COLUMN :SCH.connectionlink.stairsavailability IS 'link has stairs';
COMMENT ON COLUMN :SCH.connectionlink.liftavailability IS 'link has lift';
 
COMMENT ON TABLE :SCH.journeypattern IS 'Journey pattern';

COMMENT ON COLUMN :SCH.journeypattern.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.journeypattern.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.journeypattern.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.journeypattern.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.journeypattern.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.journeypattern.registrationnumber IS 'Registration number';
COMMENT ON COLUMN :SCH.journeypattern."name" IS 'Name';
COMMENT ON COLUMN :SCH.journeypattern.publishedname IS 'Public name for travellers';
COMMENT ON COLUMN :SCH.journeypattern."comment" IS 'Comment';

COMMENT ON TABLE :SCH.line IS 'Public Transport Line';

COMMENT ON COLUMN :SCH.line.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.line.ptnetworkid IS 'Public Transport Network internal id';
COMMENT ON COLUMN :SCH.line.companyid IS 'Company internal id';
COMMENT ON COLUMN :SCH.line.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.line.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.line.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.line.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.line."name" IS 'Name';
COMMENT ON COLUMN :SCH.line."number" IS 'Number of the line (characters)';
COMMENT ON COLUMN :SCH.line.publishedname IS 'Public name for travellers';
COMMENT ON COLUMN :SCH.line.transportmodename IS 'Transport Mode (Bus, Train, ...)';
COMMENT ON COLUMN :SCH.line.registrationnumber IS 'Registration number';
COMMENT ON COLUMN :SCH.line."comment" IS 'Comment';

COMMENT ON TABLE :SCH.ptnetwork IS 'Public Transport Network';

COMMENT ON COLUMN :SCH.ptnetwork.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.ptnetwork.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.ptnetwork.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.ptnetwork.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.ptnetwork.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.ptnetwork.versiondate IS 'Version date and time' ;
COMMENT ON COLUMN :SCH.ptnetwork.description IS 'Network description';
COMMENT ON COLUMN :SCH.ptnetwork."name" IS 'Name';
COMMENT ON COLUMN :SCH.ptnetwork.registrationnumber IS 'Registration number';
COMMENT ON COLUMN :SCH.ptnetwork.sourcename IS 'Source name';
COMMENT ON COLUMN :SCH.ptnetwork.sourceidentifier IS 'Source identifier';
COMMENT ON COLUMN :SCH.ptnetwork."comment" IS 'Comment';

COMMENT ON TABLE :SCH.route IS 'Route';

COMMENT ON COLUMN :SCH.route.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.route.oppositerouteid IS 'Opposite route internal id';
COMMENT ON COLUMN :SCH.route.lineid IS 'Line internal id';
COMMENT ON COLUMN :SCH.route.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.route.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.route.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.route.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.route."name" IS 'Name';
COMMENT ON COLUMN :SCH.route.publishedname IS 'Public name for travellers';
COMMENT ON COLUMN :SCH.route."number" IS 'Number of the line (characters)';
COMMENT ON COLUMN :SCH.route.direction IS 'Direction (Clockwise, North, ...)';
COMMENT ON COLUMN :SCH.route."comment" IS 'Comment';
COMMENT ON COLUMN :SCH.route.wayback IS 'A for Outward, R for Return';

COMMENT ON TABLE :SCH.routingconstraint IS 'Routing Constraint';

COMMENT ON COLUMN :SCH.routingconstraint.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.routingconstraint.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.routingconstraint.lineid IS 'Line internal id';
COMMENT ON COLUMN :SCH.routingconstraint."name" IS 'Name';

COMMENT ON TABLE :SCH.routingconstraint_stoparea IS 'Stopareas concerned by routingconstraint';

COMMENT ON COLUMN :SCH.routingconstraint_stoparea.routingconstraintid IS 'RoutingConstraint internal id';
COMMENT ON COLUMN :SCH.routingconstraint_stoparea.stopareaid IS 'StopArea internal id';
COMMENT ON COLUMN :SCH.routingconstraint_stoparea."position" IS 'Rank of stoparea in the routing constraint';

COMMENT ON TABLE :SCH.stoparea IS 'Stop Area';

COMMENT ON COLUMN :SCH.stoparea.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.stoparea.parentid IS 'Container Stoparea internal id';
COMMENT ON COLUMN :SCH.stoparea.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.stoparea.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.stoparea.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.stoparea.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.stoparea."name" IS 'Name';
COMMENT ON COLUMN :SCH.stoparea."comment" IS 'Comment';
COMMENT ON COLUMN :SCH.stoparea.areatype IS 'Type of Area : BoardingPosition, Quay, CommercialStopPoint, StopPlace)';
COMMENT ON COLUMN :SCH.stoparea.registrationnumber IS 'Registration number';
COMMENT ON COLUMN :SCH.stoparea.nearesttopicname IS '';
COMMENT ON COLUMN :SCH.stoparea.farecode IS 'fare code';
COMMENT ON COLUMN :SCH.stoparea.longitude IS 'Longitude';
COMMENT ON COLUMN :SCH.stoparea.latitude IS 'Latitude';
COMMENT ON COLUMN :SCH.stoparea.longlattype IS 'Model used for Longitude and Latitude (Standard, WGS84 or WGS92)';
COMMENT ON COLUMN :SCH.stoparea.x IS 'X coordinate';
COMMENT ON COLUMN :SCH.stoparea.y IS 'Y coordinate';
COMMENT ON COLUMN :SCH.stoparea.projectiontype IS 'Projection used for coordinates';
COMMENT ON COLUMN :SCH.stoparea.countrycode IS 'Zip code';
COMMENT ON COLUMN :SCH.stoparea.streetname IS 'address';

COMMENT ON TABLE :SCH.stoppoint IS 'Stop Point on Route';

COMMENT ON COLUMN :SCH.stoppoint.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.stoppoint.routeid IS 'Route internal id';
COMMENT ON COLUMN :SCH.stoppoint.stopareaid IS 'Container Stoparea internal id';
COMMENT ON COLUMN :SCH.stoppoint.ismodified IS 'Internal marker as modified';
COMMENT ON COLUMN :SCH.stoppoint."position" IS 'Rank in the route';
COMMENT ON COLUMN :SCH.stoppoint.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.stoppoint.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.stoppoint.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.stoppoint.creatorid IS 'Creator identification';

COMMENT ON TABLE :SCH.timetable IS 'Timetable';

COMMENT ON COLUMN :SCH.timetable.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.timetable.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.timetable.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.timetable.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.timetable.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.timetable."version" IS 'Operator managed version';
COMMENT ON COLUMN :SCH.timetable."comment" IS 'Comment';
COMMENT ON COLUMN :SCH.timetable.intdaytypes IS 'Binary Mask : bit rank = Daytype Enumeration rank';

COMMENT ON TABLE :SCH.timetable_date IS 'Single dates for timetable';

COMMENT ON COLUMN :SCH.timetable_date.timetableid IS 'Timetable internal identification';
COMMENT ON COLUMN :SCH.timetable_date.date IS 'Calendar date';
COMMENT ON COLUMN :SCH.timetable_date."position" IS 'Order of date in list';

COMMENT ON TABLE :SCH.timetable_period IS 'Calendar period for timetable';

COMMENT ON COLUMN :SCH.timetable_period.timetableid IS 'Timetable internal identification';
COMMENT ON COLUMN :SCH.timetable_period.periodstart IS 'First calendar day for the period (included)';
COMMENT ON COLUMN :SCH.timetable_period.periodend IS 'Last calendar day for the period (included)';
COMMENT ON COLUMN :SCH.timetable_period."position" IS 'Order of period in list';

COMMENT ON TABLE :SCH.timetablevehiclejourney IS 'Application of timetable for Vehicle journeys';

COMMENT ON COLUMN :SCH.timetablevehiclejourney.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.timetablevehiclejourney.timetableid IS 'Timetable internal identification';
COMMENT ON COLUMN :SCH.timetablevehiclejourney.vehiclejourneyid IS 'Vehicle journey internal identification';

COMMENT ON TABLE :SCH.vehiclejourney IS 'Vehicle journey';

COMMENT ON COLUMN :SCH.vehiclejourney.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.vehiclejourney.routeid IS 'Route internal id';
COMMENT ON COLUMN :SCH.vehiclejourney.journeypatternid IS 'Journey pattern internal id';
COMMENT ON COLUMN :SCH.vehiclejourney.objectid IS 'Trident identification';
COMMENT ON COLUMN :SCH.vehiclejourney.objectversion IS 'Version of this object';
COMMENT ON COLUMN :SCH.vehiclejourney.creationtime IS 'Creation date and time';
COMMENT ON COLUMN :SCH.vehiclejourney.creatorid IS 'Creator identification';
COMMENT ON COLUMN :SCH.vehiclejourney.publishedjourneyname IS 'Name for travellers';
COMMENT ON COLUMN :SCH.vehiclejourney.publishedjourneyidentifier IS 'Identifier for travellers';
COMMENT ON COLUMN :SCH.vehiclejourney.transportmode IS 'Transport mode';
COMMENT ON COLUMN :SCH.vehiclejourney.vehicletypeidentifier IS 'Vehicle type';
COMMENT ON COLUMN :SCH.vehiclejourney.statusvalue IS 'Status';
COMMENT ON COLUMN :SCH.vehiclejourney.facility IS 'Facility';
COMMENT ON COLUMN :SCH.vehiclejourney."number" IS 'Number of the vehicle journey (characters)';
COMMENT ON COLUMN :SCH.vehiclejourney."comment" IS 'Comment';

COMMENT ON TABLE :SCH.vehiclejourneyatstop IS 'Comment';

COMMENT ON COLUMN :SCH.vehiclejourneyatstop.id IS 'Internal identification';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.vehiclejourneyid IS 'Vehicle journey internal id';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.stoppointid IS 'Stop point internal id';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.ismodified IS 'Internal marker as modified';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.arrivaltime IS 'Arrival time';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.departuretime IS 'Departure time';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.waitingtime IS 'Waiting time';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.connectingserviceid IS '';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.boardingalightingpossibility IS '';
COMMENT ON COLUMN :SCH.vehiclejourneyatstop.isdeparture IS 'First stop of the vehicle journey';














