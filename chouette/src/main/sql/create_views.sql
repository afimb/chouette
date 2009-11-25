
create view Company as select 
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
          
create view ConnectionLink as select 
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
          
create view JourneyPattern as select 
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
          
create view Line as select 
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
         
create view PtNetwork as select 
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
         
create view Route as select 
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
         
create view StopArea as select 
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
         
create view StopPoint as select 
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
         
create view Timetable as select 
         id  , 
         objectId  , 
         objectVersion  , 
         creationTime  , 
         creatorId  , 
         version  , 
         comment  , 
         intDayTypes  
         from :SCH.Timetable;
         
create view TimetableVehicleJourney as select 
         id  , 
         timetableId  as idTableauMarche, 
         vehicleJourneyId as idCourse
         from :SCH.TimetableVehicleJourney;
         
create view VehicleJourney as select 
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
         
create view VehicleJourneyAtStop as select 
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
         
create view itl as select 
         id  , 
         objectId  , 
         lineId as idLigne , 
         name as nom
         from :SCH.routingConstraint;
         
create view itl_stoparea as select 
         routingConstraintId  as idItl, 
         stopareaId as idStopArea , 
         position  
         from :SCH.routingConstraint_stoparea;
         
create view timetable_date as select 
         timetableId  , 
         date  , 
         position   
         from :SCH.timetable_date;
         
create view timetable_period as select 
         timetableId  , 
         periodStart  as debut, 
         periodEnd  as fin, 
         position  
         from :SCH.timetable_period;
         
