-- call by :  psql -U [user] -W -f migrate_data.sql -v SCH=[new schema] [database name]

\set SEQ '\'':SCH'.hibernate_sequence\''

BEGIN ;

-- differs constraints
SET CONSTRAINTS ALL DEFERRED ;

-- copy tables content
insert into :SCH.Company (select id,objectid,objectversion,creationtime,creatorid,"name",shortname,organisationalunit,operatingdepartmentname,code,phone,fax,email,registrationnumber from PUBLIC.Company) ; 
insert into :SCH.ConnectionLink (select id,iddepart,idarrivee,objectid,objectversion,creationtime,creatorid,"name","comment",linkdistance,linktype,defaultduration,frequenttravellerduration,occasionaltravellerduration,mobilityrestrictedtravellerduration,mobilityrestrictedsuitability,stairsavailability,liftavailability from PUBLIC.ConnectionLink) ; 
insert into :SCH.JourneyPattern (select id,objectid,objectversion,creationtime,creatorid,registrationnumber,"name",publishedname,"comment" from PUBLIC.JourneyPattern) ; 
insert into :SCH.Line (select id,idreseau,idtransporteur,objectid,objectversion,creationtime,creatorid,"name","number",publishedname,transportmodename,registrationnumber,"comment" from PUBLIC.Line) ; 
insert into :SCH.PtNetwork (select id,objectid,objectversion,creationtime,creatorid,versiondate,description,"name",registrationnumber,sourcename,sourceidentifier,"comment" from PUBLIC.PtNetwork) ; 
insert into :SCH.Route (select id,idretour,idligne,objectid,objectversion,creationtime,creatorid,"name",publishedname,"number",direction,"comment",wayback from PUBLIC.Route) ; 
insert into :SCH.routingConstraint (select id,objectid,idligne,nom from PUBLIC.itl) ; 
insert into :SCH.routingConstraint_stoparea (select iditl,idstoparea,"position" from PUBLIC.itl_stoparea) ; 
insert into :SCH.StopArea (select id,idparent,objectid,objectversion,creationtime,creatorid,"name","comment",areatype,registrationnumber,nearesttopicname,farecode,longitude,latitude,longlattype,x,y,projectiontype,countrycode,streetname from PUBLIC.StopArea) ; 
insert into :SCH.StopPoint (select id,iditineraire,idphysique,modifie,"position",objectid,objectversion,creationtime,creatorid from PUBLIC.StopPoint) ;
insert into :SCH.Timetable (select id,objectid,objectversion,creationtime,creatorid,"version","comment",intdaytypes from PUBLIC.Timetable) ; 
insert into :SCH.timetable_date (select timetableid,"date","position" from PUBLIC.timetable_date) ; 
insert into :SCH.timetable_period (select timetableid,debut,fin,"position" from PUBLIC.timetable_period) ; 
insert into :SCH.TimetableVehicleJourney (select id,idtableaumarche,idcourse from PUBLIC.TimetableVehicleJourney) ; 
insert into :SCH.VehicleJourney (select id,iditineraire,idmission,objectid,objectversion,creationtime,creatorid,publishedjourneyname,publishedjourneyidentifier,transportmode,vehicletypeidentifier,statusvalue,facility,"number","comment" from PUBLIC.VehicleJourney) ; 
insert into :SCH.VehicleJourneyAtStop (select id,idcourse,idarret,modifie,arrivaltime,departuretime,waitingtime,connectingserviceid,boardingalightingpossibility,depart from PUBLIC.VehicleJourneyAtStop) ; 

-- synchronize sequence
SELECT setval(:SEQ, nextval('PUBLIC.hibernate_sequence') );

-- delete old tables
drop table PUBLIC.Company cascade;
drop table PUBLIC.ConnectionLink cascade;
drop table PUBLIC.JourneyPattern cascade;
drop table PUBLIC.PtNetwork cascade;
drop table PUBLIC.Line cascade;
drop table PUBLIC.Route cascade;
drop table PUBLIC.StopArea cascade;
drop table PUBLIC.StopPoint cascade;
drop table PUBLIC.TimetableVehicleJourney cascade;
drop table PUBLIC.timetable_date cascade;
drop table PUBLIC.timetable_period cascade;
drop table PUBLIC.Timetable cascade;
drop table PUBLIC.VehicleJourneyAtStop cascade;
drop table PUBLIC.VehicleJourney cascade;
drop table PUBLIC.itl_stoparea cascade;
drop table PUBLIC.itl cascade;

drop sequence PUBLIC.hibernate_sequence;

COMMIT ;


