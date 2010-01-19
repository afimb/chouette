-- refresh constraints

--ALTER TABLE chouette.line DROP CONSTRAINT line_company;

ALTER TABLE chouette.line
  ADD CONSTRAINT line_company FOREIGN KEY (companyId)
      REFERENCES chouette.company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.line DROP CONSTRAINT line_ptnetwork;

ALTER TABLE chouette.line
  ADD CONSTRAINT line_ptnetwork FOREIGN KEY (PTNetworkId)
      REFERENCES chouette.ptnetwork (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--ALTER TABLE chouette.route DROP CONSTRAINT route_line;
ALTER TABLE chouette.route
  ADD CONSTRAINT route_line FOREIGN KEY (lineId)
      REFERENCES chouette.line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--ALTER TABLE chouette.stoppoint DROP CONSTRAINT stoppoint_stoparea;

ALTER TABLE chouette.stoppoint
  ADD CONSTRAINT stoppoint_stoparea FOREIGN KEY (stopAreaId)
      REFERENCES chouette.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.stoppoint DROP CONSTRAINT stoppoint_route;

ALTER TABLE chouette.stoppoint
  ADD CONSTRAINT stoppoint_route FOREIGN KEY (routeId)
      REFERENCES chouette.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--ALTER TABLE chouette.timetablevehiclejourney DROP CONSTRAINT tmvehicle_tm;

ALTER TABLE chouette.timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_tm FOREIGN KEY (timetableId)
      REFERENCES chouette.timetable (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.timetablevehiclejourney DROP CONSTRAINT tmvehicle_vehicle;

ALTER TABLE chouette.timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_vehicle FOREIGN KEY (vehicleJourneyId)
      REFERENCES chouette.vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.vehiclejourney DROP CONSTRAINT vehiclejourney_journeypattern;

ALTER TABLE chouette.vehiclejourney
  ADD CONSTRAINT vehiclejourney_journeypattern FOREIGN KEY (journeyPatternId)
      REFERENCES chouette.journeypattern (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.vehiclejourney DROP CONSTRAINT vehicle_route;

ALTER TABLE chouette.vehiclejourney
  ADD CONSTRAINT vehicle_route FOREIGN KEY (routeId)
      REFERENCES chouette.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


--ALTER TABLE chouette.vehiclejourneyatstop DROP CONSTRAINT vjas_course;

ALTER TABLE chouette.vehiclejourneyatstop
  ADD CONSTRAINT vjas_course FOREIGN KEY (vehiclejourneyId)
      REFERENCES chouette.vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.vehiclejourneyatstop DROP CONSTRAINT vjas_stoppoint;

ALTER TABLE chouette.vehiclejourneyatstop
  ADD CONSTRAINT vjas_stoppoint FOREIGN KEY (stopPointId)
      REFERENCES chouette.stoppoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.stoparea DROP CONSTRAINT stoparea_parent;

ALTER TABLE chouette.stoparea
  ADD CONSTRAINT stoparea_parent FOREIGN KEY (parentId)
      REFERENCES chouette.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      		
--ALTER TABLE chouette.route DROP CONSTRAINT route_parent;

ALTER TABLE chouette.route
  ADD CONSTRAINT route_parent FOREIGN KEY (oppositeRouteId)
      REFERENCES chouette.route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.connectionlink DROP CONSTRAINT connection_stoparea_departure;

ALTER TABLE chouette.connectionlink
  ADD CONSTRAINT connection_stoparea_departure FOREIGN KEY (departureId)
      REFERENCES chouette.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
--ALTER TABLE chouette.connectionlink DROP CONSTRAINT connection_stoparea_arrival;

ALTER TABLE chouette.connectionlink
  ADD CONSTRAINT connection_stoparea_arrival FOREIGN KEY (arrivalId)
      REFERENCES chouette.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.routingconstraint DROP CONSTRAINT routingconstraint_line;

ALTER TABLE chouette.routingconstraint
  ADD CONSTRAINT routingconstraint_line FOREIGN KEY (lineId)
      REFERENCES chouette.line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.routingconstraint_stoparea DROP CONSTRAINT rconstraint_stoparea_rconstraint;

ALTER TABLE chouette.routingconstraint_stoparea
  ADD CONSTRAINT rconstraint_stoparea_rconstraint FOREIGN KEY (routingConstraintId)
      REFERENCES chouette.routingconstraint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--ALTER TABLE chouette.routingconstraint_stoparea DROP CONSTRAINT rconstraint_stoparea_stoparea;

ALTER TABLE chouette.routingconstraint_stoparea
  ADD CONSTRAINT rconstraint_stoparea_stoparea FOREIGN KEY (stopareaId)
      REFERENCES chouette.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      		
-- Add comment on tables and columns
