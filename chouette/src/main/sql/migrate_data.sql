-- appel par psql -U [user] -W -f migration.sql -v SCH=[nouveau schema] [nombase]

\set SEQ '\'':SCH'.hibernate_sequence\''

begin

-- differer les contraintes
SET CONSTRAINTS ALL DEFERRED ;

-- compie des tables
insert into :SCH.Company (select * from Company) ; 
insert into :SCH.ConnectionLink (select * from ConnectionLink) ; 
insert into :SCH.JourneyPattern (select * from JourneyPattern) ; 
insert into :SCH.PtNetwork (select * from PtNetwork) ; 
insert into :SCH.Line (select * from Line) ; 
insert into :SCH.Route (select * from Route) ; 
insert into :SCH.StopArea (select * from StopArea) ; 
insert into :SCH.StopPoint (select * from StopPoint) ;
insert into :SCH.Timetable (select * from Timetable) ; 
insert into :SCH.TimetableVehicleJourney (select * from TimetableVehicleJourney) ; 
insert into :SCH.VehicleJourney (select * from VehicleJourney) ; 
insert into :SCH.VehicleJourneyAtStop (select * from VehicleJourneyAtStop) ; 
insert into :SCH.routingConstraint (select * from itl) ; 
insert into :SCH.routingConstraint_stoparea (select * from itl_stoparea) ; 
insert into :SCH.timetable_date (select * from timetable_date) ; 
insert into :SCH.timetable_period (select * from timetable_period) ; 

-- synchrinsation de la s�quence
SELECT setval(:SEQ, nextval('hibernate_sequence') );

-- suppression des anciennes tables
drop table Company cascade;
drop table ConnectionLink cascade;
drop table JourneyPattern cascade;
drop table PtNetwork cascade;
drop table Line cascade;
drop table Route cascade;
drop table StopArea cascade;
drop table StopPoint cascade;
drop table TimetableVehicleJourney cascade;
drop table Timetable cascade;
drop table VehicleJourneyAtStop cascade;
drop table VehicleJourney cascade;
drop table itl cascade;
drop table itl_stoparea cascade;
drop table timetable_date cascade;
drop table timetable_period cascade;

commit;

