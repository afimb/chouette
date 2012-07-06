-- usage : psql -U chouette -W -f views.sql -v SCH=chouette -d chouette_v17

SET search_path TO :SCH ;

-- create compatibility views : 
CREATE VIEW accesslink (id,objectid,objectversion,creationtime,creatorid,name,comment,linkdistance,liftavailability,mobilityrestrictedsuitability,
                        stairsavailability,defaultduration,frequenttravellerduration,occasionaltravellerduration,mobilityrestrictedtravellerduration,
                        linktype,intuserneeds,linkorientation,accesspointid,stopareaid,arrivalid) 
AS SELECT id,objectid,object_version,creationtime,creator_id,name,comment,link_distance,lift_availability,mobility_restricted_suitability,
                        stairs_availability,default_duration,frequent_traveller_duration,occasional_traveller_duration,mobility_restricted_traveller_duration,
                        link_type,int_user_needs,link_orientation,access_point_id,stop_area_id,null FROM access_links;

CREATE VIEW accesspoint (id,objectid,objectversion,creationtime,creatorid,name,comment,countrycode,streetname,longitude,latitude,longlattype,
                         x,y,projectiontype,containedin,openningtime,closingtime,type,liftavailability,mobilityrestrictedsuitability,stairsavailability) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,country_code,street_name,longitude,latitude,long_lat_type,
                         x,y,projection_type,contained_in,openning_time,closing_time,type,lift_availability,mobility_restricted_suitability,stairs_availability FROM access_points;

CREATE VIEW company (id,objectid,objectversion,creationtime,creatorid,name,shortname,organizationalunit,operatingdepartmentname,code,phone,
                     fax,email,registrationnumber) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,short_name,organizational_unit,operating_department_name,code,phone,
                     fax,email,registration_number FROM companies;

CREATE VIEW connectionlink (id,objectid,objectversion,creationtime,creatorid,name,comment,linkdistance,departureid,arrivalid,liftavailability,
                            mobilityrestrictedsuitability,stairsavailability,defaultduration,frequenttravellerduration,occasionaltravellerduration,
                            mobilityrestrictedtravellerduration,linktype,intuserneeds) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,link_distance,departure_id,arrival_id,lift_availability,
                            mobility_restricted_suitability,stairs_availability,default_duration,frequent_traveller_duration,occasional_traveller_duration,
                            mobility_restricted_traveller_duration,link_type,int_user_needs FROM connection_links;

CREATE VIEW facility (id,objectid,objectversion,creationtime,creatorid,name,comment,stopareaid,lineid,connectionlinkid,stoppointid,description,
                      freeaccess,longitude,latitude,longlattype,countrycode,streetname,x,y,projectiontype,containedin) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,stop_area_id,line_id,connection_link_id,stop_point_id,description,
                      free_access,longitude,latitude,long_lat_type,country_code,street_name,x,y,projection_type,contained_in FROM facilities;

CREATE VIEW facilityFeature (facilityid,choicecode) 
AS SELECT facility_id,choice_code FROM facilities_features;

CREATE VIEW groupofline (id,objectid,objectversion,creationtime,creatorid,name,comment) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment FROM group_of_lines;

CREATE VIEW groupofline_line (groupoflineid,lineid) 
AS SELECT group_of_line_id,line_id FROM group_of_lines_lines;

CREATE VIEW journeypattern (id,objectid,objectversion,creationtime,creatorid,name,comment,registrationnumber,publishedname,routeid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,registration_number,published_name,route_id FROM journey_patterns;

CREATE VIEW journeyPattern_stopPoint (journeypatternid,stoppointid) 
AS SELECT journey_pattern_id,stop_point_id FROM journey_patterns_stop_points;

CREATE VIEW line (id,objectid,objectversion,creationtime,creatorid,name,comment,"number",publishedname,registrationnumber,transportmodename,
                  mobilityrestrictedsuitable,userneeds,ptnetworkid,companyid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,"number",published_name,registration_number,transport_mode_name,
                  mobility_restricted_suitability,user_needs,network_id,company_id FROM lines;

CREATE VIEW PTLink (id,objectid,objectversion,creationtime,creatorid,name,comment,linkdistance,startoflinkid,endoflinkid,routeid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,link_distance,start_of_link_id,end_of_link_id,route_id  FROM pt_links;

CREATE VIEW PTNetwork (id,objectid,objectversion,creationtime,creatorid,versiondate,description,name,registrationnumber,sourcename,
                       sourceidentifier,comment,sourcetype) 
AS SELECT id,objectid,object_version,creation_time,creator_id,version_date,description,name,registration_number,source_name,
                       source_identifier,comment,source_type FROM networks;

CREATE VIEW route (id,objectid,objectversion,creationtime,creatorid,name,oppositerouteid,publishedname,"number",direction,comment,
                   wayback,lineid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,opposite_route_id,published_name,"number",direction,comment,
                   wayback,line_id FROM routes;

CREATE VIEW routingconstraints_lines (lineid,stopareaid) 
AS SELECT line_id,stop_area_id FROM routing_constraints_lines;

CREATE VIEW stopArea (id,objectid,objectversion,creationtime,creatorid,name,comment,areatype,registrationnumber,nearesttopicname,
                      farecode,longitude,latitude,longlattype,x,y,projectiontype,countrycode,streetname,parentid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,comment,area_type,registration_number,nearest_topic_name,
                      fare_code,longitude,latitude,long_lat_type,x,y,projection_type,country_code,street_name,parent_id FROM stop_areas;

CREATE VIEW StopAreaStopArea (parentid,childid) 
AS SELECT parent_id,child_id FROM stop_areas_stop_areas;

CREATE VIEW stoppoint (id,objectid,objectversion,creationtime,creatorid,"position",stopareaid,routeid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,"position",stop_area_id,route_id FROM stop_points;

CREATE VIEW TimeSlot (id,objectid,objectversion,creationtime,creatorid,name,beginningslottime,endslottime,firstdeparturetimeinslot,lastdeparturetimeinslot) 
AS SELECT id,objectid,object_version,creation_time,creator_id,name,beginning_slot_time,end_slot_time,first_departure_time_in_slot,last_departure_time_in_slot FROM time_slots;

CREATE VIEW timetable (id,objectid,objectversion,creationtime,creatorid,comment,version,intdaytypes) 
AS SELECT id,objectid,object_version,creation_time,creator_id,comment,version,int_day_types FROM time_tables;

CREATE VIEW timetable_date (timetableid,date,"position") 
AS SELECT time_table_id,date,"position" FROM time_table_dates;

CREATE VIEW timetable_period (timetableid,periodstart,periodend,"position") 
AS SELECT time_table_id,period_start,period_end,"position" FROM time_table_periods;

CREATE VIEW timetablevehiclejourney (timetableid,vehiclejourneyid) 
AS SELECT time_table_id,vehicle_journey_id FROM time_tables_vehicle_journeys;

CREATE VIEW vehiclejourney (id,objectid,objectversion,creationtime,creatorid,comment,statusvalue,transportmode,publishedjourneyname,
                            publishedjourneyidentifier,facility,vehicletypeidentifier,"number",routeid,journeypatternid,timeslotid,companyid) 
AS SELECT id,objectid,object_version,creation_time,creator_id,comment,status_value,transport_mode,published_journey_name,
                            published_journey_identifier,facility,vehicle_type_identifier,"number",route_id,journey_pattern_id,time_slot_id,company_id FROM vehicle_journeys;

CREATE VIEW vehiclejourneyatstop (vehiclejourneyid,stoppointid,connectingserviceid,boardingalightingpossibility,"position",arrivaltime,
                                  departuretime,waitingtime,elapseduration,headwayfrequency,isdeparture,isarrival) 
AS SELECT vehicle_journey_id,stop_point_id,connecting_service_id,boarding_alighting_possibility,"position",arrival_time,
                                  departure_time,waiting_time,elapse_duration,headway_frequency,is_departure,is_arrival FROM vehicle_journey_at_stops;

