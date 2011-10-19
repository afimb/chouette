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
-- accesslink
DROP SEQUENCE IF EXISTS accesslink_id_seq ;
CREATE SEQUENCE accesslink_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;  
ALTER TABLE accesslink ALTER COLUMN id SET DEFAULT nextval('accesslink_id_seq'::regclass);
SELECT SETVAL('accesslink_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM accesslink),false);
  
-- accesspoint
DROP SEQUENCE IF EXISTS accesspoint_id_seq ;
CREATE SEQUENCE accesspoint_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE accesspoint ALTER COLUMN id SET DEFAULT nextval('accesspoint_id_seq'::regclass);
SELECT SETVAL('accesspoint_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM accesspoint),false);


-- company   
DROP SEQUENCE IF EXISTS company_id_seq ;
CREATE SEQUENCE company_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE company ALTER COLUMN id SET DEFAULT nextval('company_id_seq'::regclass);
SELECT SETVAL('company_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM company),false);

-- connectionlink   
DROP SEQUENCE IF EXISTS connectionlink_id_seq ;
CREATE SEQUENCE 
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE connectionlink ALTER COLUMN id SET DEFAULT nextval('connectionlink_id_seq'::regclass);
SELECT SETVAL('connectionlink_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM connectionlink),false);

-- facility   
DROP SEQUENCE IF EXISTS facility_id_seq ;
CREATE SEQUENCE facility_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE facility ALTER COLUMN id SET DEFAULT nextval('facility_id_seq'::regclass);
SELECT SETVAL('facility_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM facility),false);

-- groupofline   
DROP SEQUENCE IF EXISTS groupofline_id_seq ;
CREATE SEQUENCE groupofline_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE groupofline ALTER COLUMN id SET DEFAULT nextval('groupofline_id_seq'::regclass);
SELECT SETVAL('groupofline_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM groupofline),false);

-- journeypattern   
DROP SEQUENCE IF EXISTS journeypattern_id_seq ;
CREATE SEQUENCE journeypattern_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE journeypattern ALTER COLUMN id SET DEFAULT nextval('journeypattern_id_seq'::regclass);
SELECT SETVAL('journeypattern_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM journeypattern),false);

-- line  
DROP SEQUENCE IF EXISTS line_id_seq ; 
CREATE SEQUENCE line_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE line ALTER COLUMN id SET DEFAULT nextval('line_id_seq'::regclass);
SELECT SETVAL('line_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM line),false);

-- ptlink   
DROP SEQUENCE IF EXISTS ptlink_id_seq ; 
CREATE SEQUENCE ptlink_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE ptlink ALTER COLUMN id SET DEFAULT nextval('ptlink_id_seq'::regclass);
SELECT SETVAL('ptlink_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM ptlink),false);

-- PTNetwork   
DROP SEQUENCE IF EXISTS ptnetwork_id_seq ; 
CREATE SEQUENCE ptnetwork_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE ptnetwork ALTER COLUMN id SET DEFAULT nextval('ptnetwork_id_seq'::regclass);
SELECT SETVAL('ptnetwork_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM ptnetwork),false);

-- route   
DROP SEQUENCE IF EXISTS route_id_seq ; 
CREATE SEQUENCE route_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE route ALTER COLUMN id SET DEFAULT nextval('route_id_seq'::regclass);
SELECT SETVAL('route_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM route),false);

-- stoparea   
DROP SEQUENCE IF EXISTS stoparea_id_seq ; 
CREATE SEQUENCE stoparea_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoparea ALTER COLUMN id SET DEFAULT nextval('stoparea_id_seq'::regclass);
SELECT SETVAL('stoparea_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoparea),false);

-- stoppoint   
DROP SEQUENCE IF EXISTS stoppoint_id_seq ; 
CREATE SEQUENCE stoppoint_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE stoppoint ALTER COLUMN id SET DEFAULT nextval('stoppoint_id_seq'::regclass);
SELECT SETVAL('stoppoint_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM stoppoint),false);

-- timeslot   
DROP SEQUENCE IF EXISTS timeslot_id_seq ; 
CREATE SEQUENCE timeslot_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE timeslot ALTER COLUMN id SET DEFAULT nextval('timeslot_id_seq'::regclass);
SELECT SETVAL('timeslot_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM timeslot),false);

-- TimeSlot is redefined
ALTER TABLE timeslot DROP COLUMN beginningslottime;
ALTER TABLE timeslot DROP COLUMN firstdeparturetimeinslot;
ALTER TABLE timeslot DROP COLUMN endslottime;
ALTER TABLE timeslot DROP COLUMN lastdeparturetimeinslot;
ALTER TABLE timeslot ADD COLUMN beginningslottime time without time zone;
ALTER TABLE timeslot ADD COLUMN endslottime time without time zone;
ALTER TABLE timeslot ADD COLUMN firstdeparturetimeinslot time without time zone;
ALTER TABLE timeslot ADD COLUMN lastdeparturetimeinslot time without time zone;
ALTER TABLE timeslot ALTER COLUMN beginningslottime SET STORAGE PLAIN;
ALTER TABLE timeslot ALTER COLUMN endslottime SET STORAGE PLAIN;
ALTER TABLE timeslot ALTER COLUMN firstdeparturetimeinslot SET STORAGE PLAIN;
ALTER TABLE timeslot ALTER COLUMN lastdeparturetimeinslot SET STORAGE PLAIN;

-- timetable   
DROP SEQUENCE IF EXISTS timetable_id_seq ; 
CREATE SEQUENCE timetable_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE timetable ALTER COLUMN id SET DEFAULT nextval('timetable_id_seq'::regclass);
SELECT SETVAL('timetable_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM timetable),false);

-- vehiclejourney   
DROP SEQUENCE IF EXISTS vehiclejourney_id_seq ; 
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
drop table routingconstraint_stoparea;
drop table routingconstraint;

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
ALTER TABLE accesslink ADD CONSTRAINT aclk_acpt_fkey FOREIGN KEY (accesspointid) REFERENCES accesspoint(id) ON DELETE CASCADE;
ALTER TABLE accesslink ADD CONSTRAINT aclk_area_fkey FOREIGN KEY (stopareaid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE connectionlink ADD CONSTRAINT colk_endarea_fkey FOREIGN KEY (arrivalid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE connectionlink ADD CONSTRAINT colk_startarea_fkey FOREIGN KEY (departureid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE facility ADD CONSTRAINT faci_area_fkey FOREIGN KEY (stopareaid) REFERENCES stoparea(id) ON DELETE CASCADE;
ALTER TABLE facility ADD CONSTRAINT faci_colk_fkey FOREIGN KEY (connectionlinkid) REFERENCES connectionlink(id) ON DELETE CASCADE;
ALTER TABLE facility ADD CONSTRAINT faci_line_fkey FOREIGN KEY (lineid) REFERENCES line(id) ON DELETE CASCADE;
ALTER TABLE facility ADD CONSTRAINT faci_stpt_fkey FOREIGN KEY (stoppointid) REFERENCES stoppoint(id) ON DELETE CASCADE;
ALTER TABLE facilityfeature ADD CONSTRAINT facility_feature_fkey FOREIGN KEY (facilityid) REFERENCES facility(id) ON DELETE CASCADE;
ALTER TABLE journeypattern ADD CONSTRAINT jp_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE journeypattern_stoppoint ADD CONSTRAINT jpsp_jp_fkey FOREIGN KEY (journeypatternid) REFERENCES journeypattern(id) ON DELETE CASCADE;
ALTER TABLE journeypattern_stoppoint ADD CONSTRAINT jpsp_stoppoint_fkey FOREIGN KEY (stoppointid) REFERENCES stoppoint(id) ON DELETE CASCADE;
ALTER TABLE line ADD CONSTRAINT line_company_fkey FOREIGN KEY (companyid) REFERENCES company(id) ON DELETE SET NULL;
ALTER TABLE line ADD CONSTRAINT line_glines_fkey FOREIGN KEY (groupoflineid) REFERENCES groupofline(id) ON DELETE SET NULL;
ALTER TABLE line ADD CONSTRAINT line_ptnetwork_fkey FOREIGN KEY (ptnetworkid) REFERENCES ptnetwork(id) ON DELETE SET NULL;
ALTER TABLE ptlink ADD CONSTRAINT ptlink_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE ptlink ADD CONSTRAINT ptlk_endstpt_fkey FOREIGN KEY (endoflinkid) REFERENCES stoppoint(id);
ALTER TABLE ptlink ADD CONSTRAINT ptlk_startstpt_fkey FOREIGN KEY (startoflinkid) REFERENCES stoppoint(id);
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
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_company_fkey FOREIGN KEY (companyid) REFERENCES company(id) ON DELETE SET NULL;
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_jp_fkey FOREIGN KEY (journeypatternid) REFERENCES journeypattern(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_route_fkey FOREIGN KEY (routeid) REFERENCES route(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourney ADD CONSTRAINT vj_timeslot_fkey FOREIGN KEY (timeslotid) REFERENCES timeslot(id) ON DELETE SET NULL;
ALTER TABLE vehiclejourneyatstop ADD CONSTRAINT vjas_sp_fkey FOREIGN KEY (stoppointid) REFERENCES stoppoint(id) ON DELETE CASCADE;
ALTER TABLE vehiclejourneyatstop ADD CONSTRAINT vjas_vj_fkey FOREIGN KEY (vehiclejourneyid) REFERENCES vehiclejourney(id) ON DELETE CASCADE;

