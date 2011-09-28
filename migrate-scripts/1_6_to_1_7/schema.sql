SET search_path TO :SCH ;

-- TimetableVehicleJourney

ALTER TABLE timetablevehiclejourney DROP CONSTRAINT timetablevehiclejourney_pkey;

ALTER TABLE timetablevehiclejourney DROP COLUMN id;

ALTER TABLE timetablevehiclejourney
  ADD CONSTRAINT timetablevehiclejourney_pkey PRIMARY KEY(timetableid, vehiclejourneyid);
  
-- VehicleJourneyAtStop

ALTER TABLE vehiclejourneyatstop DROP CONSTRAINT vjas_pk;

ALTER TABLE vehiclejourneyatstop DROP COLUMN id;

ALTER TABLE vehiclejourneyatstop
  ADD CONSTRAINT vehiclejourneyatstop_pkey PRIMARY KEY(vehiclejourneyid, stoppointid);

-- id sequences 
-- purge nouvelles tables eventuelles
DROP TABLE IF EXISTS accesslink CASCADE;
DROP TABLE IF EXISTS accesspoint  CASCADE;
DROP TABLE IF EXISTS facilityfeature  CASCADE;
DROP TABLE IF EXISTS facility  CASCADE;
DROP TABLE IF EXISTS groupofline  CASCADE;
DROP TABLE IF EXISTS ptlink  CASCADE;
DROP TABLE IF EXISTS timeslot  CASCADE;
  
-- company   
CREATE SEQUENCE company_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE company ALTER COLUMN id SET DEFAULT nextval('company_id_seq'::regclass);

SELECT SETVAL('company_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM company),false);

-- connectionlink   
CREATE SEQUENCE connectionlink_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE connectionlink ALTER COLUMN id SET DEFAULT nextval('connectionlink_id_seq'::regclass);

SELECT SETVAL('connectionlink_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM connectionlink),false);

-- journeypattern   
CREATE SEQUENCE journeypattern_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE journeypattern ALTER COLUMN id SET DEFAULT nextval('journeypattern_id_seq'::regclass);

SELECT SETVAL('journeypattern_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM journeypattern),false);

-- line   
CREATE SEQUENCE line_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE line ALTER COLUMN id SET DEFAULT nextval('line_id_seq'::regclass);

SELECT SETVAL('line_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM line),false);

-- PTNetwork   
CREATE SEQUENCE ptnetwork_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE ptnetwork ALTER COLUMN id SET DEFAULT nextval('ptnetwork_id_seq'::regclass);

SELECT SETVAL('ptnetwork_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM ptnetwork),false);

-- route   
CREATE SEQUENCE route_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE route ALTER COLUMN id SET DEFAULT nextval('route_id_seq'::regclass);

SELECT SETVAL('route_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM route),false);


-- stoparea   
CREATE SEQUENCE stoparea_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoparea ALTER COLUMN id SET DEFAULT nextval('stoparea_id_seq'::regclass);

SELECT SETVAL('stoparea_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoparea),false);

-- stoppoint   
CREATE SEQUENCE stoppoint_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoppoint ALTER COLUMN id SET DEFAULT nextval('stoppoint_id_seq'::regclass);

SELECT SETVAL('stoppoint_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoppoint),false);

-- timetable   
CREATE SEQUENCE timetable_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE timetable ALTER COLUMN id SET DEFAULT nextval('timetable_id_seq'::regclass);

SELECT SETVAL('timetable_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM timetable),false);

-- vehiclejourney   
CREATE SEQUENCE vehiclejourney_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE vehiclejourney ALTER COLUMN id SET DEFAULT nextval('vehiclejourney_id_seq'::regclass);

SELECT SETVAL('vehiclejourney_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM vehiclejourney),false);

-- add new tables for compatibility
CREATE TABLE stopareastoparea
(
  parentid bigint NOT NULL,
  childid bigint NOT NULL,
  CONSTRAINT stoparea_child_fkey FOREIGN KEY (childid)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parentid)
      REFERENCES stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);

CREATE TABLE chouette_test.routingconstraints_lines
(
  lineid bigint NOT NULL, -- Line reference
  stopareaid bigint NOT NULL, -- Routing constraint reference
  CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (lineid)
      REFERENCES chouette_test.line (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT routingconstraint_stoparea_fkey FOREIGN KEY (stopareaid)
      REFERENCES chouette_test.stoparea (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (OIDS=FALSE);

-- init stoparea-stoparea links alter
insert into stopareastoparea (parentId,childId) select parentId,id as childId from StopArea where parentId NOTNULL; 


-- init routing constraint links (preserve routing constraint id is possible because pervious versions had a shared sequence)
insert into stoparea (id, objectid,areatype,name,creationtime) select id, :PREFIX||':StopArea:'||id as objectId,'ITL' as areatype,name,now() as creationtime from routingconstraint;

insert into routingconstraints_lines (lineid,stopareaid) select lineid, id as stopareaid from routingconstraint;

insert into stopareastoparea (parentId,childId) select routingconstraintid as parentId, stopareaid as childId from routingconstraint_stoparea;

-- drop deprecated tables
drop table routingconstraint_stoparea;
drop table routingconstraint;
