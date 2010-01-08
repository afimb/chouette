-- call by :  psql -U [user] -W -f migrate_data.sql -v SCH=[new schema] [database name]

\set SEQ '\'':SCH'.hibernate_sequence\''

begin

-- differs constraints
SET CONSTRAINTS ALL DEFERRED ;

-- copy tables content
insert into :SCH.Company (select id,objectid,objectversion,creationtime,creatorid,"name",shortname,organisationalunit,operatingdepartmentname,code,phone,fax,email,registrationnumber from Company) ; 
insert into :SCH.ConnectionLink (select id,iddepart,idarrivee,objectid,objectversion,creationtime,creatorid,"name","comment",linkdistance,linktype,defaultduration,frequenttravellerduration,occasionaltravellerduration,mobilityrestrictedtravellerduration,mobilityrestrictedsuitability,stairsavailability,liftavailability from ConnectionLink) ; 
insert into :SCH.JourneyPattern (select id,objectid,objectversion,creationtime,creatorid,registrationnumber,"name",publishedname,"comment" from JourneyPattern) ; 
insert into :SCH.Line (select id,idreseau,idtransporteur,objectid,objectversion,creationtime,creatorid,"name","number",publishedname,transportmodename,registrationnumber,"comment" from Line) ; 
insert into :SCH.PtNetwork (select id,objectid,objectversion,creationtime,creatorid,versiondate,description,"name",registrationnumber,sourcename,sourceidentifier,"comment" from PtNetwork) ; 
insert into :SCH.Route (select id,idretour,idligne,objectid,objectversion,creationtime,creatorid,"name",publishedname,"number",direction,"comment",wayback from Route) ; 
insert into :SCH.routingConstraint (select id,objectid,idligne,nom from itl) ; 
insert into :SCH.routingConstraint_stoparea (select iditl,idstoparea,"position" from itl_stoparea) ; 
insert into :SCH.StopArea (select id,idparent,objectid,objectversion,creationtime,creatorid,"name","comment",areatype,registrationnumber,nearesttopicname,farecode,longitude,latitude,longlattype,x,y,projectiontype,countrycode,streetname from StopArea) ; 
insert into :SCH.StopPoint (select id,iditineraire,idphysique,modifie,"position",objectid,objectversion,creationtime,creatorid from StopPoint) ;
insert into :SCH.Timetable (select id,objectid,objectversion,creationtime,creatorid,"version","comment",intdaytypes from Timetable) ; 
insert into :SCH.timetable_date (select timetableid,"date","position" from timetable_date) ; 
insert into :SCH.timetable_period (select timetableid,debut,fin,"position" from timetable_period) ; 
insert into :SCH.TimetableVehicleJourney (select id,idtableaumarche,idcourse from TimetableVehicleJourney) ; 
insert into :SCH.VehicleJourney (select id,iditineraire,idmission,objectid,objectversion,creationtime,creatorid,publishedjourneyname,publishedjourneyidentifier,transportmode,vehicletypeidentifier,statusvalue,facility,"number","comment" from VehicleJourney) ; 
insert into :SCH.VehicleJourneyAtStop (select id,idcourse,idarret,modifie,arrivaltime,departuretime,waitingtime,connectingserviceid,boardingalightingpossibility,depart from VehicleJourneyAtStop) ; 

-- synchronize sequence
SELECT setval(:SEQ, nextval('hibernate_sequence') );

-- delete old tables
-- drop table Company cascade;
-- drop table ConnectionLink cascade;
-- drop table JourneyPattern cascade;
-- drop table PtNetwork cascade;
-- drop table Line cascade;
-- drop table Route cascade;
-- drop table StopArea cascade;
-- drop table StopPoint cascade;
-- drop table TimetableVehicleJourney cascade;
-- drop table Timetable cascade;
-- drop table VehicleJourneyAtStop cascade;
-- drop table VehicleJourney cascade;
-- drop table itl cascade;
-- drop table itl_stoparea cascade;
-- drop table timetable_date cascade;
-- drop table timetable_period cascade;

commit;

