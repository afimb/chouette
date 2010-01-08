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
