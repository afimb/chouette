-- usage : psql -U chouette -W -f schema.sql -v SCH=chouette -d chouette_v17

SET search_path TO :SCH ;
-- add underscore on each fields to separate names (exclude objectid)
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

ALTER TABLE groupofline_line RENAME COLUMN group_of_line_id TO zzzz;
ALTER TABLE groupofline_line RENAME COLUMN line_id TO zzzz;

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

ALTER TABLE Line RENAME COLUMN PTNetworkId TO ptnetwork_id;
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
ALTER TABLE Route RENAME COLUMN registrationNumber TO registration_number;
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
ALTER TABLE StopArea RENAME COLUMN liftAvailability TO lift_availability;
ALTER TABLE StopArea RENAME COLUMN mobilityrestrictedsuitability TO mobility_restricted_suitability;
ALTER TABLE StopArea RENAME COLUMN stairsAvailability TO stairs_availability;
ALTER TABLE StopArea RENAME COLUMN areatype TO area_type;
ALTER TABLE StopArea RENAME COLUMN nearesttopicname TO nearest_topic_name;
ALTER TABLE StopArea RENAME COLUMN fareCode TO fare_code;

ALTER TABLE stopareastoparea RENAME COLUMN parentId TO parent_id;
ALTER TABLE stopareastoparea RENAME COLUMN childId TO child_id;

ALTER TABLE StopPoint RENAME COLUMN objectVersion TO object_version;
ALTER TABLE StopPoint RENAME COLUMN creationTime TO creation_time;
ALTER TABLE StopPoint RENAME COLUMN creatorId TO creator_id;
ALTER TABLE StopPoint RENAME COLUMN registrationNumber TO registration_number;
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
ALTER TABLE VehicleJourney RENAME COLUMN registrationNumber TO registration_number;
ALTER TABLE VehicleJourney RENAME COLUMN statusValue TO status_value;
ALTER TABLE VehicleJourney RENAME COLUMN transportMode TO transport_mode_name;
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


-- vehiclejourneyatstop primary key changes
ALTER TABLE vehiclejourneyatstop DROP CONSTRAINT vehiclejourneyatstop_pkey;
ALTER TABLE vehiclejourneyatstop ADD COLUMN id bigserial;
ALTER TABLE vehiclejourneyatstop ADD CONSTRAINT vehiclejourneyatstop_pkey  PRIMARY KEY (id);

-- purge bad relationship in stopareastoparea
DELETE FROM stopareastoparea WHERE parent_id NOT IN (SELECT id FROM stoparea WHERE area_type = 'itl');

-- add journeypattern first and last stoppoint
ALTER TABLE journeypattern ADD COLUMN departure_stop_point_id bigint;
ALTER TABLE journeypattern ALTER COLUMN departure_stop_point_id SET STORAGE PLAIN;
COMMENT ON COLUMN journeypattern.departure_stop_point_id IS 'Departure StopPoint Reference';

ALTER TABLE journeypattern ADD COLUMN arrival_stop_point_id bigint;
ALTER TABLE journeypattern ALTER COLUMN arrival_stop_point_id SET STORAGE PLAIN;
COMMENT ON COLUMN journeypattern.arrival_stop_point_id IS 'Arrival StopPoint Reference';

ALTER TABLE journeypattern
  ADD CONSTRAINT arrival_point_fkey FOREIGN KEY (arrival_stop_point_id)
      REFERENCES stoppoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;
      
ALTER TABLE journeypattern
  ADD CONSTRAINT departure_point_fkey FOREIGN KEY (departure_stop_point_id)
      REFERENCES stoppoint (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

UPDATE journeypattern j SET departure_stop_point_id = (SELECT h.stop_point_id 
                                                    FROM vehiclejourneyatstop h, vehiclejourney v 
                                                    WHERE h.is_departure = true 
                                                      AND h.vehicle_journey_id = v.id 
                                                      AND v.journey_pattern_id = j.id 
                                                      LIMIT 1); 
                                                      
UPDATE journeypattern j SET arrival_stop_point_id = (SELECT h.stop_point_id 
                                                    FROM vehiclejourneyatstop h, vehiclejourney v 
                                                    WHERE h.is_arrival = true 
                                                      AND h.vehicle_journey_id = v.id 
                                                      AND v.journey_pattern_id = j.id 
                                                      LIMIT 1); 
                                                      
