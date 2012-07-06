-- usage : psql -U chouette -W -f schema.sql -v SCH=chouette -v PREFIX=ninoxe -d chouette_v17

SET search_path TO :SCH ;

-- add underscore on each fields to separate names (exclude objectid) (database naming conventions)
ALTER TABLE AccessLink RENAME COLUMN objectVersion TO object_version;
ALTER TABLE AccessLink RENAME COLUMN creationTime TO creation_time;
ALTER TABLE AccessLink RENAME COLUMN creatorId TO creator_id;
ALTER TABLE AccessLink RENAME COLUMN linkDistance TO link_distance;
ALTER TABLE AccessLink RENAME COLUMN liftAvailability TO lift_availability;
ALTER TABLE AccessLink RENAME COLUMN mobilityrestrictedsuitability TO mobility_restricted_suitability;
ALTER TABLE AccessLink RENAME COLUMN stairsAvailability TO stairs_availability;
ALTER TABLE AccessLink RENAME COLUMN defaultduration TO default_duration;
ALTER TABLE AccessLink RENAME COLUMN frequenttravellerduration TO frequent_traveller_duration;
ALTER TABLE AccessLink RENAME COLUMN occasionaltravellerduration TO occasional_traveller_duration;
ALTER TABLE AccessLink RENAME COLUMN mobilityRestrictedTravellerDuration TO mobility_restricted_traveller_duration;
ALTER TABLE AccessLink RENAME COLUMN linkType TO link_type;
ALTER TABLE AccessLink RENAME COLUMN intUserNeeds TO int_user_needs;
ALTER TABLE AccessLink RENAME COLUMN linkOrientation TO link_orientation;
ALTER TABLE AccessLink RENAME COLUMN accesspointid TO access_point_id;
ALTER TABLE AccessLink RENAME COLUMN stopareaid TO stop_area_id;

ALTER TABLE AccessPoint RENAME COLUMN objectVersion TO object_version;
ALTER TABLE AccessPoint RENAME COLUMN creationTime TO creation_time;
ALTER TABLE AccessPoint RENAME COLUMN creatorId TO creator_id;
ALTER TABLE AccessPoint RENAME COLUMN longlattype TO long_lat_type;
ALTER TABLE AccessPoint RENAME COLUMN projectiontype TO projection_type;
ALTER TABLE AccessPoint RENAME COLUMN countrycode TO country_code;
ALTER TABLE AccessPoint RENAME COLUMN streetname TO street_name;
ALTER TABLE AccessPoint RENAME COLUMN containedIn TO contained_in;
ALTER TABLE AccessPoint RENAME COLUMN openningTime TO openning_time;
ALTER TABLE AccessPoint RENAME COLUMN closingTime TO closing_time;
ALTER TABLE AccessPoint RENAME COLUMN liftAvailability TO lift_availability;
ALTER TABLE AccessPoint RENAME COLUMN mobilityrestrictedsuitability TO mobility_restricted_suitability;
ALTER TABLE AccessPoint RENAME COLUMN stairsAvailability TO stairs_availability;

ALTER TABLE Company RENAME COLUMN objectVersion TO object_version;
ALTER TABLE Company RENAME COLUMN creationTime TO creation_time;
ALTER TABLE Company RENAME COLUMN creatorId TO creator_id;
ALTER TABLE Company RENAME COLUMN shortName TO short_name;
ALTER TABLE Company RENAME COLUMN organizationalUnit TO organizational_unit;
ALTER TABLE Company RENAME COLUMN operatingDepartmentName TO operating_department_name;
ALTER TABLE Company RENAME COLUMN registrationNumber TO registration_number;

ALTER TABLE ConnectionLink RENAME COLUMN objectVersion TO object_version;
ALTER TABLE ConnectionLink RENAME COLUMN creationTime TO creation_time;
ALTER TABLE ConnectionLink RENAME COLUMN creatorId TO creator_id;
ALTER TABLE ConnectionLink RENAME COLUMN linkDistance TO link_distance;
ALTER TABLE ConnectionLink RENAME COLUMN departureid TO departure_id;
ALTER TABLE ConnectionLink RENAME COLUMN arrivalid TO arrival_id;
ALTER TABLE ConnectionLink RENAME COLUMN liftAvailability TO lift_availability;
ALTER TABLE ConnectionLink RENAME COLUMN mobilityrestrictedsuitability TO mobility_restricted_suitability;
ALTER TABLE ConnectionLink RENAME COLUMN stairsAvailability TO stairs_availability;
ALTER TABLE ConnectionLink RENAME COLUMN defaultduration TO default_duration;
ALTER TABLE ConnectionLink RENAME COLUMN frequenttravellerduration TO frequent_traveller_duration;
ALTER TABLE ConnectionLink RENAME COLUMN occasionaltravellerduration TO occasional_traveller_duration;
ALTER TABLE ConnectionLink RENAME COLUMN mobilityRestrictedTravellerDuration TO mobility_restricted_traveller_duration;
ALTER TABLE ConnectionLink RENAME COLUMN linkType TO link_type;
ALTER TABLE ConnectionLink RENAME COLUMN intUserNeeds TO int_user_needs;

ALTER TABLE Facility RENAME COLUMN objectVersion TO object_version;
ALTER TABLE Facility RENAME COLUMN creationTime TO creation_time;
ALTER TABLE Facility RENAME COLUMN creatorId TO creator_id;
ALTER TABLE Facility RENAME COLUMN stopAreaId TO stop_area_id;
ALTER TABLE Facility RENAME COLUMN lineid TO line_id;
ALTER TABLE Facility RENAME COLUMN connectionLinkid TO connection_link_id;
ALTER TABLE Facility RENAME COLUMN stoppointid TO stop_point_id;
ALTER TABLE Facility RENAME COLUMN freeAccess TO free_access;
ALTER TABLE Facility RENAME COLUMN longLatType TO long_lat_type;
ALTER TABLE Facility RENAME COLUMN projectiontype TO projection_type;
ALTER TABLE Facility RENAME COLUMN countrycode TO country_code;
ALTER TABLE Facility RENAME COLUMN streetname TO street_name;
ALTER TABLE Facility RENAME COLUMN containedIn TO contained_in;

ALTER TABLE facilityFeature RENAME COLUMN facilityId TO facility_id;
ALTER TABLE facilityFeature RENAME COLUMN choiceCode TO choice_code;

ALTER TABLE GroupOfLine RENAME COLUMN objectVersion TO object_version;
ALTER TABLE GroupOfLine RENAME COLUMN creationTime TO creation_time;
ALTER TABLE GroupOfLine RENAME COLUMN creatorId TO creator_id;

ALTER TABLE groupofline_line RENAME COLUMN groupoflineid TO group_of_line_id;
ALTER TABLE groupofline_line RENAME COLUMN lineid TO line_id;

ALTER TABLE JourneyPattern RENAME COLUMN objectVersion TO object_version;
ALTER TABLE JourneyPattern RENAME COLUMN creationTime TO creation_time;
ALTER TABLE JourneyPattern RENAME COLUMN creatorId TO creator_id;
ALTER TABLE JourneyPattern RENAME COLUMN registrationNumber TO registration_number;
ALTER TABLE JourneyPattern RENAME COLUMN publishedName TO published_name;
ALTER TABLE JourneyPattern RENAME COLUMN routeId TO route_id;

ALTER TABLE journeyPattern_stopPoint RENAME COLUMN journeyPatternId TO journey_pattern_id;
ALTER TABLE journeyPattern_stopPoint RENAME COLUMN stopPointId TO stop_point_id;

ALTER TABLE Line RENAME COLUMN objectVersion TO object_version;
ALTER TABLE Line RENAME COLUMN creationTime TO creation_time;
ALTER TABLE Line RENAME COLUMN creatorId TO creator_id;
ALTER TABLE Line RENAME COLUMN registrationNumber TO registration_number;
ALTER TABLE Line RENAME COLUMN publishedName TO published_name;
ALTER TABLE Line RENAME COLUMN transportModeName TO transport_mode_name;
ALTER TABLE Line RENAME COLUMN mobilityRestrictedSuitable TO mobility_restricted_suitability;

ALTER TABLE Line DROP COLUMN userNeeds ; -- will be recreated by 

ALTER TABLE Line RENAME COLUMN PTNetworkId TO network_id;
ALTER TABLE Line RENAME COLUMN companyId TO company_id;


ALTER TABLE routingconstraints_lines RENAME COLUMN lineId TO line_id;
ALTER TABLE routingconstraints_lines RENAME COLUMN stopareaid TO stop_area_id;

ALTER TABLE PTLink RENAME COLUMN objectVersion TO object_version;
ALTER TABLE PTLink RENAME COLUMN creationTime TO creation_time;
ALTER TABLE PTLink RENAME COLUMN creatorId TO creator_id;
ALTER TABLE PTLink RENAME COLUMN linkDistance TO link_distance;
ALTER TABLE PTLink RENAME COLUMN startOfLinkId TO start_of_link_id;
ALTER TABLE PTLink RENAME COLUMN endOfLinkId TO end_of_link_id;
ALTER TABLE PTLink RENAME COLUMN routeId TO route_id;

ALTER TABLE PTNetwork RENAME COLUMN objectVersion TO object_version;
ALTER TABLE PTNetwork RENAME COLUMN creationTime TO creation_time;
ALTER TABLE PTNetwork RENAME COLUMN creatorId TO creator_id;
ALTER TABLE PTNetwork RENAME COLUMN registrationNumber TO registration_number;
ALTER TABLE PTNetwork RENAME COLUMN versionDate TO version_date;
ALTER TABLE PTNetwork RENAME COLUMN sourceType TO source_type;
ALTER TABLE PTNetwork RENAME COLUMN sourceName TO source_name;
ALTER TABLE PTNetwork RENAME COLUMN sourceIdentifier TO source_identifier;

ALTER TABLE Route RENAME COLUMN objectVersion TO object_version;
ALTER TABLE Route RENAME COLUMN creationTime TO creation_time;
ALTER TABLE Route RENAME COLUMN creatorId TO creator_id;
ALTER TABLE Route RENAME COLUMN oppositeRouteId TO opposite_route_id;
ALTER TABLE Route RENAME COLUMN publishedName TO published_name;
ALTER TABLE Route RENAME COLUMN lineId TO line_id;

ALTER TABLE StopArea RENAME COLUMN objectVersion TO object_version;
ALTER TABLE StopArea RENAME COLUMN creationTime TO creation_time;
ALTER TABLE StopArea RENAME COLUMN creatorId TO creator_id;
ALTER TABLE StopArea RENAME COLUMN registrationNumber TO registration_number;
ALTER TABLE StopArea RENAME COLUMN longlattype TO long_lat_type;
ALTER TABLE StopArea RENAME COLUMN projectiontype TO projection_type;
ALTER TABLE StopArea RENAME COLUMN countrycode TO country_code;
ALTER TABLE StopArea RENAME COLUMN streetname TO street_name;
ALTER TABLE StopArea RENAME COLUMN parentId TO parent_id;
ALTER TABLE StopArea RENAME COLUMN areatype TO area_type;
ALTER TABLE StopArea RENAME COLUMN nearesttopicname TO nearest_topic_name;
ALTER TABLE StopArea RENAME COLUMN fareCode TO fare_code;
COMMENT ON COLUMN stoparea.parent_id IS 'parent StopArea';


ALTER TABLE stopareastoparea RENAME COLUMN parentId TO parent_id;
ALTER TABLE stopareastoparea RENAME COLUMN childId TO child_id;
COMMENT ON TABLE stopareastoparea
  IS 'Routing constraint applicable on stop areas';
COMMENT ON COLUMN stopareastoparea.parent_id IS 'routing constraint reference';
COMMENT ON COLUMN stopareastoparea.child_id IS 'stoparea reference';

ALTER TABLE StopPoint RENAME COLUMN objectVersion TO object_version;
ALTER TABLE StopPoint RENAME COLUMN creationTime TO creation_time;
ALTER TABLE StopPoint RENAME COLUMN creatorId TO creator_id;
ALTER TABLE StopPoint RENAME COLUMN stopareaid TO stop_area_id;
ALTER TABLE StopPoint RENAME COLUMN routeId TO route_id;

ALTER TABLE TimeSlot RENAME COLUMN objectVersion TO object_version;
ALTER TABLE TimeSlot RENAME COLUMN creationTime TO creation_time;
ALTER TABLE TimeSlot RENAME COLUMN creatorId TO creator_id;
ALTER TABLE TimeSlot RENAME COLUMN beginningSlotTime TO beginning_slot_time;
ALTER TABLE TimeSlot RENAME COLUMN endSlotTime TO end_slot_time;
ALTER TABLE TimeSlot RENAME COLUMN firstDepartureTimeInSlot TO first_departure_time_in_slot;
ALTER TABLE TimeSlot RENAME COLUMN lastDepartureTimeInSlot TO last_departure_time_in_slot;

ALTER TABLE Timetable RENAME COLUMN objectVersion TO object_version;
ALTER TABLE Timetable RENAME COLUMN creationTime TO creation_time;
ALTER TABLE Timetable RENAME COLUMN creatorId TO creator_id;
ALTER TABLE Timetable RENAME COLUMN intDayTypes TO int_day_types;

ALTER TABLE timetable_date RENAME COLUMN timetableId TO time_table_id;

ALTER TABLE timetable_period RENAME COLUMN timetableId TO time_table_id;
ALTER TABLE timetable_period RENAME COLUMN periodstart TO period_start;
ALTER TABLE timetable_period RENAME COLUMN periodend TO period_end;

ALTER TABLE timetablevehiclejourney RENAME COLUMN timetableId TO time_table_id;
ALTER TABLE timetablevehiclejourney RENAME COLUMN vehiclejourneyId TO vehicle_journey_id;

ALTER TABLE VehicleJourney RENAME COLUMN objectVersion TO object_version;
ALTER TABLE VehicleJourney RENAME COLUMN creationTime TO creation_time;
ALTER TABLE VehicleJourney RENAME COLUMN creatorId TO creator_id;
ALTER TABLE VehicleJourney RENAME COLUMN statusValue TO status_value;
ALTER TABLE VehicleJourney RENAME COLUMN transportMode TO transport_mode;
ALTER TABLE VehicleJourney RENAME COLUMN publishedJourneyName TO published_journey_name;
ALTER TABLE VehicleJourney RENAME COLUMN publishedJourneyIdentifier TO published_journey_identifier;
ALTER TABLE VehicleJourney RENAME COLUMN vehicleTypeIdentifier TO vehicle_type_identifier;
ALTER TABLE VehicleJourney RENAME COLUMN routeId TO route_id;
ALTER TABLE VehicleJourney RENAME COLUMN journeyPatternId TO journey_pattern_id;
ALTER TABLE VehicleJourney RENAME COLUMN timeSlotId TO time_slot_id;
ALTER TABLE VehicleJourney RENAME COLUMN companyId TO company_id;

ALTER TABLE VehicleJourneyAtStop RENAME COLUMN vehiclejourneyid TO vehicle_journey_id;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN stopPointId TO stop_point_id;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN connectingServiceId TO connecting_service_id;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN boardingAlightingPossibility TO boarding_alighting_possibility;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN arrivalTime TO arrival_time;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN departureTime TO departure_time;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN waitingTime TO waiting_time;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN elapseDuration TO elapse_duration;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN headwayFrequency TO headway_frequency;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN isDeparture TO is_departure;
ALTER TABLE VehicleJourneyAtStop RENAME COLUMN isArrival TO is_arrival;


-- rename all tables with underscore and pluralize (database naming conventions)
ALTER TABLE AccessLink RENAME to access_links;
ALTER SEQUENCE accesslink_id_seq RENAME to access_links_id_seq;
ALTER TABLE access_links ALTER COLUMN id SET DEFAULT nextval('access_links_id_seq'::regclass);

ALTER TABLE AccessPoint RENAME to access_points;
ALTER SEQUENCE accesspoint_id_seq RENAME to access_points_id_seq;
ALTER TABLE access_points ALTER COLUMN id SET DEFAULT nextval('access_points_id_seq'::regclass);

ALTER TABLE Company RENAME to companies ;   
ALTER SEQUENCE company_id_seq RENAME to companies_id_seq;
ALTER TABLE companies ALTER COLUMN id SET DEFAULT nextval('companies_id_seq'::regclass);

ALTER TABLE ConnectionLink RENAME to connection_links ;   
ALTER SEQUENCE ConnectionLink_id_seq RENAME to connection_links_id_seq;
ALTER TABLE connection_links ALTER COLUMN id SET DEFAULT nextval('connection_links_id_seq'::regclass);

ALTER TABLE Facility RENAME to facilities ;   
ALTER SEQUENCE Facility_id_seq RENAME to facilities_id_seq;
ALTER TABLE facilities ALTER COLUMN id SET DEFAULT nextval('facilities_id_seq'::regclass);

ALTER TABLE facilityFeature RENAME to facilities_features ;   

ALTER TABLE GroupOfLine RENAME to group_of_lines ;   
ALTER SEQUENCE GroupOfLine_id_seq RENAME to group_of_lines_id_seq;
ALTER TABLE group_of_lines ALTER COLUMN id SET DEFAULT nextval('group_of_lines_id_seq'::regclass);

ALTER TABLE groupofline_line RENAME to group_of_lines_lines ;   

ALTER TABLE JourneyPattern RENAME to journey_patterns ;   
ALTER SEQUENCE JourneyPattern_id_seq RENAME to journey_patterns_id_seq;
ALTER TABLE journey_patterns ALTER COLUMN id SET DEFAULT nextval('journey_patterns_id_seq'::regclass);

ALTER TABLE journeyPattern_stopPoint RENAME to journey_patterns_stop_points ;   

ALTER TABLE Line RENAME to lines ;   
ALTER SEQUENCE Line_id_seq RENAME to lines_id_seq;
ALTER TABLE lines ALTER COLUMN id SET DEFAULT nextval('lines_id_seq'::regclass);

ALTER TABLE routingconstraints_lines RENAME to routing_constraints_lines ;   

ALTER TABLE PTLink RENAME to pt_links;
ALTER SEQUENCE PTLink_id_seq RENAME to pt_links_id_seq;
ALTER TABLE pt_links ALTER COLUMN id SET DEFAULT nextval('pt_links_id_seq'::regclass);

ALTER TABLE PTNetwork RENAME to networks ;   
ALTER SEQUENCE ptnetwork_id_seq RENAME to networks_id_seq;
ALTER TABLE networks ALTER COLUMN id SET DEFAULT nextval('networks_id_seq'::regclass);

ALTER TABLE Route RENAME to routes ;   
ALTER SEQUENCE Route_id_seq RENAME to routes_id_seq;
ALTER TABLE routes ALTER COLUMN id SET DEFAULT nextval('routes_id_seq'::regclass);

ALTER TABLE StopArea RENAME to stop_areas ;   
ALTER SEQUENCE StopArea_id_seq RENAME to stop_areas_id_seq;
ALTER TABLE stop_areas ALTER COLUMN id SET DEFAULT nextval('stop_areas_id_seq'::regclass);

ALTER TABLE stopareastoparea RENAME to stop_areas_stop_areas ;   

ALTER TABLE StopPoint RENAME to stop_points ;   
ALTER SEQUENCE StopPoint_id_seq RENAME to stop_points_id_seq;
ALTER TABLE stop_points ALTER COLUMN id SET DEFAULT nextval('stop_points_id_seq'::regclass);

ALTER TABLE TimeSlot RENAME to time_slots ;
ALTER SEQUENCE TimeSlot_id_seq RENAME to time_slots_id_seq;
ALTER TABLE time_slots ALTER COLUMN id SET DEFAULT nextval('time_slots_id_seq'::regclass);

ALTER TABLE Timetable RENAME to time_tables ;
ALTER SEQUENCE Timetable_id_seq RENAME to time_tables_id_seq;
ALTER TABLE time_tables ALTER COLUMN id SET DEFAULT nextval('time_tables_id_seq'::regclass);

ALTER TABLE timetable_date RENAME to time_table_dates ;

ALTER TABLE timetable_period RENAME to time_table_periods ;

ALTER TABLE timetablevehiclejourney RENAME to time_tables_vehicle_journeys ;

ALTER TABLE VehicleJourney RENAME to vehicle_journeys ;
ALTER SEQUENCE VehicleJourney_id_seq RENAME to vehicle_journeys_id_seq;
ALTER TABLE vehicle_journeys ALTER COLUMN id SET DEFAULT nextval('vehicle_journeys_id_seq'::regclass);

ALTER TABLE VehicleJourneyAtStop RENAME to vehicle_journey_at_stops ;


-- vehiclejourneyatstop primary key changes
ALTER TABLE vehicle_journey_at_stops DROP CONSTRAINT vehiclejourneyatstop_pkey;
ALTER TABLE vehicle_journey_at_stops ADD COLUMN id bigserial;
ALTER TABLE vehicle_journey_at_stops ADD CONSTRAINT vehicle_journey_at_stop_pkey PRIMARY KEY (id);

-- purge bad relationship in stopareastoparea
DELETE FROM stop_areas_stop_areas WHERE parent_id NOT IN (SELECT id FROM stop_areas WHERE area_type = 'itl');

-- add journeypattern first and last stoppoint
ALTER TABLE journey_patterns ADD COLUMN departure_stop_point_id bigint;
ALTER TABLE journey_patterns ALTER COLUMN departure_stop_point_id SET STORAGE PLAIN;
COMMENT ON COLUMN journey_patterns.departure_stop_point_id IS 'Departure StopPoint Reference';

ALTER TABLE journey_patterns ADD COLUMN arrival_stop_point_id bigint;
ALTER TABLE journey_patterns ALTER COLUMN arrival_stop_point_id SET STORAGE PLAIN;
COMMENT ON COLUMN journey_patterns.arrival_stop_point_id IS 'Arrival StopPoint Reference';

ALTER TABLE journey_patterns
  ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id)
      REFERENCES stop_points (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
ALTER TABLE journey_patterns
  ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id)
      REFERENCES stop_points (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

UPDATE journey_patterns j SET departure_stop_point_id = (SELECT h.stop_point_id 
                                                    FROM vehicle_journey_at_stops h, vehicle_journeys v 
                                                    WHERE h.is_departure = true 
                                                      AND h.vehicle_journey_id = v.id 
                                                      AND v.journey_pattern_id = j.id 
                                                      LIMIT 1); 
                                                      
UPDATE journey_patterns j SET arrival_stop_point_id = (SELECT h.stop_point_id 
                                                    FROM vehicle_journey_at_stops h, vehicle_journeys v 
                                                    WHERE h.is_arrival = true 
                                                      AND h.vehicle_journey_id = v.id 
                                                      AND v.journey_pattern_id = j.id 
                                                      LIMIT 1); 


-- create tables for rails migrations
CREATE TABLE schema_migrations
(
  version character varying(255) NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schema_migrations
  OWNER TO chouette;

-- insert into schema_migrations values ('20120126110946'); -- _create_referentials.rb
insert into schema_migrations values ('20120213131553'); -- _create_chouette_line.rb
insert into schema_migrations values ('20120214101458'); -- _create_chouette_company.rb
insert into schema_migrations values ('20120214101645'); -- _create_chouette_ptnetwork.rb
-- insert into schema_migrations values ('20120413142837'); -- _devise_create_users.rb
insert into schema_migrations values ('20120416095045'); -- _create_chouette_stop_area.rb
insert into schema_migrations values ('20120416095046'); -- _create_time_table.rb
insert into schema_migrations values ('20120425080337'); -- _create_chouette_route.rb
insert into schema_migrations values ('20120425125542'); -- _create_chouette_stop_point.rb
insert into schema_migrations values ('20120426141032'); -- _create_chouette_connection_link.rb
-- insert into schema_migrations values ('20120515134710'); -- _create_imports.rb
-- insert into schema_migrations values ('20120516172252'); -- _create_delayed_jobs.rb
-- insert into schema_migrations values ('20120523123806'); -- _add_fields_to_referentials.rb
insert into schema_migrations values ('20120525092203'); -- _create_chouette_journey_pattern.rb
insert into schema_migrations values ('20120525092204'); -- _create_chouette_journey_pattern_stop_point.rb
insert into schema_migrations values ('20120525092205'); -- _create_chouette_time_slot.rb
insert into schema_migrations values ('20120525092206'); -- _create_chouette_vehicle_journey.rb
insert into schema_migrations values ('20120525092207'); -- _create_chouette_vehicle_journey_at_stop.rb
insert into schema_migrations values ('20120525092208'); -- _create_chouette_time_table_vehicle_journey.rb
insert into schema_migrations values ('20120525092209'); -- _create_chouette_access_point.rb
insert into schema_migrations values ('20120525092210'); -- _create_chouette_access_link.rb
insert into schema_migrations values ('20120525092211'); -- _create_chouette_facility.rb
insert into schema_migrations values ('20120525092212'); -- _create_chouette_facility_feature.rb
insert into schema_migrations values ('20120525092213'); -- _create_chouette_group_of_line.rb
insert into schema_migrations values ('20120525092214'); -- _create_chouette_group_of_line_line.rb
insert into schema_migrations values ('20120525092215'); -- _create_chouette_routing_constrains_line.rb
insert into schema_migrations values ('20120525092216'); -- _create_chouette_stoparea_stoparea.rb
-- insert into schema_migrations values ('20120529154848'); -- _create_import_log_messages.rb
-- insert into schema_migrations values ('20120531070108'); -- _add_type_and_options_to_import.rb
insert into schema_migrations values ('20120531091529'); -- _create_chouette_pt_link.rb
-- insert into schema_migrations values ('20120607064150'); -- _create_exports.rb
-- insert into schema_migrations values ('20120607064625'); -- _create_export_log_messages.rb
-- insert into schema_migrations values ('20120611090254'); -- _resize_argument_to_import_log_messages.rb
-- insert into schema_migrations values ('20120611090325'); -- _resize_argument_to_export_log_messages.rb
-- insert into schema_migrations values ('20120612071936'); -- _add_file_type_to_import.rb
-- insert into schema_migrations values ('20120620064014'); -- _add_references_to_export.rb
-- insert into schema_migrations values ('20120620081726'); -- _create_file_validations.rb
-- insert into schema_migrations values ('20120620081755'); -- _create_file_validation_log_messages.rb

SET search_path TO public ;
-- create tables for rails migrations
CREATE TABLE schema_migrations
(
  version character varying(255) NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE schema_migrations
  OWNER TO chouette;
insert into schema_migrations values ('20120126110946'); -- _create_referentials.rb
insert into schema_migrations values ('20120523123806'); -- _add_fields_to_referentials.rb

CREATE TABLE referentials
(
  id serial NOT NULL,
  name character varying(255),
  slug character varying(255),
  created_at timestamp without time zone,
  updated_at timestamp without time zone,
  prefix character varying(255),
  projection_type character varying(255),
  time_zone character varying(255),
  CONSTRAINT referentials_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE referentials
  OWNER TO chouette;

\set var_sch '\'':SCH'\''
\set var_prefix '\'':PREFIX'\''

insert into referentials (name,slug,created_at,updated_at,prefix,projection_type,time_zone) 
  values (var_sch,var_sch,localtimestamp,localtimestamp,var_prefix,null,'Paris');