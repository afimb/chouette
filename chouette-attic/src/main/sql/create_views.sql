
create view PUBLIC.Company as select 
          id , 
          objectId , 
          objectVersion , 
          creationTime , 
          creatorId , 
          name , 
          shortName , 
          organizationalUnit as organisationalUnit, 
          operatingDepartmentName , 
          code , 
          phone , 
          fax , 
          email , 
          registrationNumber
          from :SCH.Company;
          
create view PUBLIC.ConnectionLink as select 
          id , 
          departureId as idDepart, 
          arrivalId as idArrivee, 
          objectId , 
          objectVersion , 
          creationTime , 
          creatorId , 
          name , 
          comment , 
          linkDistance , 
          linkType , 
          defaultDuration , 
          frequentTravellerDuration , 
          occasionalTravellerDuration , 
          mobilityRestrictedTravellerDuration , 
          mobilityRestrictedSuitability , 
          stairsAvailability , 
          liftAvailability 
          from :SCH.ConnectionLink;
          
create view PUBLIC.JourneyPattern as select 
          id , 
          objectId , 
          objectVersion , 
          creationTime , 
          creatorId , 
          registrationNumber , 
          name, 
          publishedName , 
          comment 
          from :SCH.JourneyPattern;
          

create view PUBLIC.Line as select 
         id , 
         PTNetworkId as idReseau, 
         companyId as idTransporteur, 
         objectId , 
         objectVersion , 
         creationTime , 
         creatorId , 
         name , 
         number , 
         publishedName , 
         transportModeName , 
         registrationNumber , 
         comment 
         from :SCH.Line;
         
create view PUBLIC.PtNetwork as select 
         id , 
         objectId , 
         objectVersion , 
         creationTime , 
         creatorId , 
         versionDate , 
         description , 
         name , 
         registrationNumber , 
         sourceName , 
         sourceIdentifier , 
         comment 
         from :SCH.PtNetwork;
         
create view PUBLIC.Route as select 
         id  , 
         oppositeRouteId as idRetour, 
         lineId as idLigne, 
         objectId , 
         objectVersion , 
         creationTime , 
         creatorId , 
         name , 
         publishedName , 
         number , 
         direction , 
         comment , 
         wayBack 
         from :SCH.Route;
         
create view PUBLIC.StopArea as select 
         id , 
         parentId as idParent, 
         objectId , 
         objectVersion , 
         creationTime , 
         creatorId , 
         name , 
         comment , 
         areaType , 
         registrationNumber , 
         nearestTopicName , 
         fareCode , 
         longitude  , 
         latitude , 
         longLatType , 
         x , 
         y  , 
         projectionType , 
         countryCode , 
         streetName  
         from :SCH.StopArea;
         
create view PUBLIC.StopPoint as select 
         id  , 
         routeId  as idItineraire, 
         stopAreaId  as idPhysique, 
         isModified as modifie, 
         position  , 
         objectId  , 
         objectVersion  , 
         creationTime  , 
         creatorId   
         from :SCH.StopPoint;
         
create view PUBLIC.Timetable as select 
         id  , 
         objectId  , 
         objectVersion  , 
         creationTime  , 
         creatorId  , 
         version  , 
         comment  , 
         intDayTypes  
         from :SCH.Timetable;
         
create view PUBLIC.TimetableVehicleJourney as select 
         id  , 
         timetableId  as idTableauMarche, 
         vehicleJourneyId as idCourse
         from :SCH.TimetableVehicleJourney;
         
create view PUBLIC.VehicleJourney as select 
         id  , 
         routeId as idItineraire, 
         journeyPatternId  as idMission, 
         objectId  , 
         objectVersion  , 
         creationTime  , 
         creatorId  , 
         publishedJourneyName  , 
         publishedJourneyIdentifier  , 
         transportMode  , 
         vehicleTypeIdentifier  , 
         statusValue  , 
         facility  , 
         number  , 
         comment  
         from :SCH.VehicleJourney;
         
create view PUBLIC.VehicleJourneyAtStop as select 
         id  , 
         vehicleJourneyId  as idCourse, 
         stopPointId  as idArret, 
         isModified  as modifie, 
         arrivalTime  , 
         departureTime  , 
         waitingTime  , 
         connectingServiceId  , 
         boardingAlightingPossibility  , 
         isDeparture  as depart
         from :SCH.VehicleJourneyAtStop;

create view PUBLIC.itl as select 
         id  , 
         objectId  , 
         lineId as idLigne , 
         name as nom
         from :SCH.routingConstraint;
         
create view PUBLIC.itl_stoparea as select 
         routingConstraintId  as idItl, 
         stopareaId as idStopArea , 
         position  
         from :SCH.routingConstraint_stoparea;
         
create view PUBLIC.timetable_date as select 
         timetableId  , 
         date  , 
         position   
         from :SCH.timetable_date;
         
create view PUBLIC.timetable_period as select 
         timetableId  , 
         periodStart  as debut, 
         periodEnd  as fin, 
         position  
         from :SCH.timetable_period;
         
