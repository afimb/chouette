SET search_path TO :SCH ;

-- cleaning views
DROP VIEW IF EXISTS public.company;
DROP VIEW IF EXISTS public.connectionlink;
DROP VIEW IF EXISTS public.itl;
DROP VIEW IF EXISTS public.journeypattern;
DROP VIEW IF EXISTS public.line;
DROP VIEW IF EXISTS public.ptnetwork;
DROP VIEW IF EXISTS public.route;
DROP VIEW IF EXISTS public.stoparea;
DROP VIEW IF EXISTS public.stoppoint;
DROP VIEW IF EXISTS public.timetable;
DROP VIEW IF EXISTS public.timetable_date;
DROP VIEW IF EXISTS public.timetable_period;
DROP VIEW IF EXISTS public.timetablevehiclejourney;
DROP VIEW IF EXISTS public.vehiclejourney;
DROP VIEW IF EXISTS public.vehiclejourneyatstop;

-- TimetableVehicleJourney
-- remove potentialy duplicate
DROP TABLE IF EXISTS temp_duplicate;

CREATE TABLE temp_duplicate AS select timetableid,vehiclejourneyid from timetablevehiclejourney group by timetableid,vehiclejourneyid having count(*) > 1;

ALTER TABLE timetablevehiclejourney DROP CONSTRAINT timetablevehiclejourney_pkey;

ALTER TABLE timetablevehiclejourney DROP COLUMN id;

DELETE FROM timetablevehiclejourney tv USING temp_duplicate WHERE tv.timetableid = temp_duplicate.timetableid AND tv.vehiclejourneyid = temp_duplicate.vehiclejourneyid;
INSERT INTO timetablevehiclejourney (timetableid,vehiclejourneyid) SELECT timetableid,vehiclejourneyid FROM temp_duplicate;

DROP TABLE temp_duplicate;

ALTER TABLE timetablevehiclejourney
  ADD CONSTRAINT timetablevehiclejourney_pkey PRIMARY KEY(timetableid, vehiclejourneyid);
  
-- VehicleJourneyAtStop

ALTER TABLE vehiclejourneyatstop DROP CONSTRAINT vjas_pk;

ALTER TABLE vehiclejourneyatstop DROP COLUMN id;

ALTER TABLE vehiclejourneyatstop
  ADD CONSTRAINT vehiclejourneyatstop_pkey PRIMARY KEY(vehiclejourneyid, stoppointid);

-- id sequences 

-- company   
DROP SEQUENCE IF EXISTS company_id_seq CASCADE  ;
CREATE SEQUENCE company_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE company ALTER COLUMN id SET DEFAULT nextval('company_id_seq'::regclass);
SELECT SETVAL('company_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM company),false);

-- connectionlink   
DROP SEQUENCE IF EXISTS connectionlink_id_seq CASCADE  ;
CREATE SEQUENCE  connectionlink_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE connectionlink ALTER COLUMN id SET DEFAULT nextval('connectionlink_id_seq'::regclass);
SELECT SETVAL('connectionlink_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM connectionlink),false);


-- journeypattern   
DROP SEQUENCE IF EXISTS journeypattern_id_seq CASCADE  ;
CREATE SEQUENCE journeypattern_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE journeypattern ALTER COLUMN id SET DEFAULT nextval('journeypattern_id_seq'::regclass);
SELECT SETVAL('journeypattern_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM journeypattern),false);

-- line  
DROP SEQUENCE IF EXISTS line_id_seq CASCADE  ; 
CREATE SEQUENCE line_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE line ALTER COLUMN id SET DEFAULT nextval('line_id_seq'::regclass);
SELECT SETVAL('line_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM line),false);


-- PTNetwork   
DROP SEQUENCE IF EXISTS ptnetwork_id_seq CASCADE  ; 
CREATE SEQUENCE ptnetwork_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE ptnetwork ALTER COLUMN id SET DEFAULT nextval('ptnetwork_id_seq'::regclass);
SELECT SETVAL('ptnetwork_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM ptnetwork),false);

-- route   
DROP SEQUENCE IF EXISTS route_id_seq CASCADE  ; 
CREATE SEQUENCE route_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE route ALTER COLUMN id SET DEFAULT nextval('route_id_seq'::regclass);
SELECT SETVAL('route_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM route),false);

-- stoparea   
DROP SEQUENCE IF EXISTS stoparea_id_seq CASCADE  ; 
CREATE SEQUENCE stoparea_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoparea ALTER COLUMN id SET DEFAULT nextval('stoparea_id_seq'::regclass);
SELECT SETVAL('stoparea_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoparea),false);

-- stoppoint   
DROP SEQUENCE IF EXISTS stoppoint_id_seq CASCADE  ; 
CREATE SEQUENCE stoppoint_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoppoint ALTER COLUMN id SET DEFAULT nextval('stoppoint_id_seq'::regclass);
SELECT SETVAL('stoppoint_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoppoint),false);


-- timetable   
DROP SEQUENCE IF EXISTS timetable_id_seq CASCADE  ; 
CREATE SEQUENCE timetable_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE timetable ALTER COLUMN id SET DEFAULT nextval('timetable_id_seq'::regclass);
SELECT SETVAL('timetable_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM timetable),false);

-- vehiclejourney   
DROP SEQUENCE IF EXISTS vehiclejourney_id_seq CASCADE  ; 
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
  childid bigint NOT NULL
)
WITH (OIDS=FALSE);

CREATE TABLE routingconstraints_lines
(
  lineid bigint NOT NULL, -- Line reference
  stopareaid bigint NOT NULL -- Routing constraint reference
)
WITH (OIDS=FALSE);

-- init stoparea-stoparea links alter
insert into stopareastoparea (parentId,childId) select parentId,id as childId from StopArea where parentId NOTNULL; 

-- init routing constraint links (preserve routing constraint id is possible because pervious versions had a shared sequence)
insert into stoparea (id, objectid,areatype,name,creationtime) select id, :PREFIX||':StopArea:'||id as objectId,'ITL' as areatype,name,now() as creationtime from routingconstraint;

-- refresh next value for stoparea sequence
SELECT SETVAL('stoparea_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoparea),false);

insert into routingconstraints_lines (lineid,stopareaid) select lineid, id as stopareaid from routingconstraint;

insert into stopareastoparea (parentId,childId) select routingconstraintid as parentId, stopareaid as childId from routingconstraint_stoparea;

-- drop deprecated tables
drop table routingconstraint_stoparea cascade;
drop table routingconstraint;

-- connect journeypattern to route
ALTER TABLE journeypattern ADD COLUMN routeid bigint;
ALTER TABLE journeypattern ALTER COLUMN routeid SET STORAGE PLAIN;
COMMENT ON COLUMN journeypattern.routeid IS 'Route Reference';

update journeypattern j set routeId=(select distinct vj.routeid from vehicleJourney vj where vj.journeyPatternId = j.id);

-- drop previous foreign keys 
\pset tuples_only
\set var_sch '\'':SCH'\''
\o /tmp/dropconstraints.sql
SELECT 'ALTER TABLE '||relname||' DROP CONSTRAINT '||conname||';'
 FROM pg_constraint 
 INNER JOIN pg_class ON conrelid=pg_class.oid 
 INNER JOIN pg_namespace ON pg_namespace.oid=pg_class.relnamespace 
 where nspname = :var_sch and contype='f' order by  relname,conname;
\o 
\set ECHO all
\i /tmp/dropconstraints.sql

-- create foreing keys 
ALTER TABLE connectionlink ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrivalid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE connectionlink ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departureid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE line ADD CONSTRAINT line_company_fkey FOREIGN KEY (companyid) REFERENCES company(id) ON DELETE SET NULL;
ALTER TABLE line ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (ptnetworkid) REFERENCES ptnetwork(id) ON DELETE SET NULL;
ALTER TABLE journeypattern ADD CONSTRAINT jp_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE route ADD CONSTRAINT route_line_fkey FOREIGN KEY (lineid) REFERENCES line(id) ON DELETE CASCADE;
ALTER TABLE routingconstraints_lines ADD CONSTRAINT routingconstraint_line_fkey FOREIGN KEY (lineid) REFERENCES line(id) ON DELETE CASCADE;
ALTER TABLE routingconstraints_lines ADD CONSTRAINT routingconstraint_stoparea_fkey FOREIGN KEY (stopareaid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE stoparea ADD CONSTRAINT area_parent_fkey FOREIGN KEY (parentid) REFERENCES stoparea(id) ON DELETE SET NULL;
ALTER TABLE stopareastoparea ADD CONSTRAINT stoparea_child_fkey FOREIGN KEY (childid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE stopareastoparea ADD CONSTRAINT stoparea_parent_fkey FOREIGN KEY (parentid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE stoppoint ADD CONSTRAINT stoppoint_area_fkey FOREIGN KEY (stopareaid) REFERENCES stoparea(id);
ALTER TABLE stoppoint ADD CONSTRAINT stoppoint_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE timetable_date ADD CONSTRAINT tm_date_fkey FOREIGN KEY (timetableid) REFERENCES timetable(id) ON DELETE CASCADE;
ALTER TABLE timetable_period ADD CONSTRAINT tm_period_fkey FOREIGN KEY (timetableid) REFERENCES timetable(id) ON DELETE CASCADE;
ALTER TABLE timetablevehiclejourney ADD CONSTRAINT vjtm_tm_fkey FOREIGN KEY (timetableid) REFERENCES timetable(id) ON DELETE CASCADE;
ALTER TABLE timetablevehiclejourney ADD CONSTRAINT vjtm_vj_fkey FOREIGN KEY (vehiclejourneyid) REFERENCES vehiclejourney(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journeypatternid) REFERENCES journeypattern(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourneyatstop ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stoppointid) REFERENCES stoppoint(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourneyatstop ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehiclejourneyid) REFERENCES vehiclejourney(id) ON DELETE CASCADE;

