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

-- routingconstraint   
CREATE SEQUENCE routingconstraint_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE routingconstraint ALTER COLUMN id SET DEFAULT nextval('routingconstraint_id_seq'::regclass);

SELECT SETVAL('routingconstraint_id_seq'::regclass,(SELECT case when count(*)=0 then 1 ELSE max(id) + 1 END FROM routingconstraint),false);

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


