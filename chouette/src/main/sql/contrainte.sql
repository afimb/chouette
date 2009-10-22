-- ALTER TABLE line DROP CONSTRAINT line_company;

ALTER TABLE line
  ADD CONSTRAINT line_company FOREIGN KEY (idtransporteur)
      REFERENCES company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE line DROP CONSTRAINT line_ptnetwork;

ALTER TABLE line
  ADD CONSTRAINT line_ptnetwork FOREIGN KEY (idreseau)
      REFERENCES ptnetwork (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


-- ALTER TABLE route DROP CONSTRAINT route_line;

ALTER TABLE route
  ADD CONSTRAINT route_line FOREIGN KEY (idligne)
      REFERENCES line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


-- ALTER TABLE stoppoint DROP CONSTRAINT stoppoint_stoparea;

ALTER TABLE stoppoint
  ADD CONSTRAINT stoppoint_stoparea FOREIGN KEY (idphysique)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE stoppoint DROP CONSTRAINT stoppoint_route;

ALTER TABLE stoppoint
  ADD CONSTRAINT stoppoint_route FOREIGN KEY (iditineraire)
      REFERENCES route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


-- ALTER TABLE timetablevehiclejourney DROP CONSTRAINT tmvehicle_tm;

ALTER TABLE timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_tm FOREIGN KEY (idtableaumarche)
      REFERENCES timetable (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE timetablevehiclejourney DROP CONSTRAINT tmvehicle_vehicle;

ALTER TABLE timetablevehiclejourney
  ADD CONSTRAINT tmvehicle_vehicle FOREIGN KEY (idcourse)
      REFERENCES vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE vehiclejourney DROP CONSTRAINT vehiclejourney_journeypattern;

ALTER TABLE vehiclejourney
  ADD CONSTRAINT vehiclejourney_journeypattern FOREIGN KEY (idmission)
      REFERENCES journeypattern (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE vehiclejourney DROP CONSTRAINT vehicle_route;

ALTER TABLE vehiclejourney
  ADD CONSTRAINT vehicle_route FOREIGN KEY (iditineraire)
      REFERENCES route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


-- ALTER TABLE vehiclejourneyatstop DROP CONSTRAINT horaire_course;

ALTER TABLE vehiclejourneyatstop
  ADD CONSTRAINT horaire_course FOREIGN KEY (idcourse)
      REFERENCES vehiclejourney (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE vehiclejourneyatstop DROP CONSTRAINT horaire_stoppoint;

ALTER TABLE vehiclejourneyatstop
  ADD CONSTRAINT horaire_stoppoint FOREIGN KEY (idarret)
      REFERENCES stoppoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE stoparea DROP CONSTRAINT stoparea_parent;

ALTER TABLE stoparea
  ADD CONSTRAINT stoparea_parent FOREIGN KEY (idparent)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      		
-- ALTER TABLE route DROP CONSTRAINT route_parent;

ALTER TABLE route
  ADD CONSTRAINT route_parent FOREIGN KEY (idretour)
      REFERENCES route (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE connectionlink DROP CONSTRAINT connection_stoparea_depart;

ALTER TABLE connectionlink
  ADD CONSTRAINT connection_stoparea_depart FOREIGN KEY (iddepart)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
-- ALTER TABLE connectionlink DROP CONSTRAINT connection_stoparea_arrivee;

ALTER TABLE connectionlink
  ADD CONSTRAINT connection_stoparea_arrivee FOREIGN KEY (idarrivee)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE itl DROP CONSTRAINT itl_line;

ALTER TABLE itl
  ADD CONSTRAINT itl_line FOREIGN KEY (idligne)
      REFERENCES line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE itl_stoparea DROP CONSTRAINT itl_stop_area_itl;

ALTER TABLE itl_stoparea
  ADD CONSTRAINT itl_stop_area_itl FOREIGN KEY (iditl)
      REFERENCES itl (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

-- ALTER TABLE itl_stoparea DROP CONSTRAINT itl_stop_area_stop_area;

ALTER TABLE itl_stoparea
  ADD CONSTRAINT itl_stop_area_stop_area FOREIGN KEY (idstoparea)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;            
      		