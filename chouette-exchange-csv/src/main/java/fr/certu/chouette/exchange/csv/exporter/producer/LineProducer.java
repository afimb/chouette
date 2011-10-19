package fr.certu.chouette.exchange.csv.exporter.producer;

import java.sql.Time;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.certu.chouette.model.neptune.AreaCentroid;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.VehicleJourneyAtStop;
import fr.certu.chouette.model.neptune.type.Address;
import fr.certu.chouette.model.neptune.type.BoardingAlightingPossibilityEnum;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ProjectedPoint;

public class LineProducer extends AbstractCSVNeptuneProducer<Line> {

	   public static final String       LINE_NAME_TITLE           = "Nom de la ligne";
	   private static final String      PUBLISHED_LINE_NAME_TITLE = "Nom public";
	   private static final String      NUMBER_TITLE              = "Numero de la ligne";
	   private static final String      COMMENT_TITLE             = "Commentaire de la ligne";
	   private static final String      TRANSPORT_MODE_NAME_TITLE = "Mode de Transport (BUS,METRO,RER,TRAIN ou TRAMWAY)";

	   private static final String      DIRECTION_TITLE           = "Direction (ALLER/RETOUR)";
	   private static final String      TIMETABLE_TITLE           = "Calendriers d'application";
	   private static final String      SPECIFIC_TITLE            = "Particularités";
	   
	   private static final String[]	BOARDING_POSITION_LINE_TITLES =  {"X","Y","Latitude","Longitude","Adresse","Code Postal","Zone","Liste des arrêts"};
	   
	   private static final int         X_COLUMN                  = 0;
	   private static final int         Y_COLUMN                  = 1;
	   private static final int         LATITUDE_COLUMN           = 2;
	   private static final int         LONGITUDE_COLUMN          = 3;
	   private static final int         ADDRESS_COLUMN            = 4;
	   private static final int         ZIPCODE_COLUMN            = 5;
	   private static final int         AERAZONE_COLUMN           = 6;
	   private static final int         STOPNAME_COLUMN           = 7;
	   
	   private static final String mfHoraireHM = "{0,number,00}:{1,number,00}";
	   
	@Override
	public List<String[]> produce(Line line) {
		List<String[]> csvLinesList = new ArrayList<String[]>();
		csvLinesList.add(createCSVLine(LINE_NAME_TITLE, line.getName()));
		csvLinesList.add(createCSVLine(PUBLISHED_LINE_NAME_TITLE, line.getPublishedName()));
		csvLinesList.add(createCSVLine(NUMBER_TITLE, line.getNumber()));
		csvLinesList.add(createCSVLine(COMMENT_TITLE, line.getComment()));
		csvLinesList.add(createCSVLine(TRANSPORT_MODE_NAME_TITLE, line.getTransportModeName().toString().toUpperCase()));
		
		List<Route> routes = line.getRoutes();
		if(routes.size() > 2)
			return null;
		
		int vehicleJourneysCount = 0;
		for(Route route : routes){
			List<JourneyPattern> journeyPatterns = route.getJourneyPatterns();
			for(JourneyPattern journeyPattern : journeyPatterns){
				vehicleJourneysCount += journeyPattern.getVehicleJourneys().size();
			}
		}
		
		// TODO : add titles lines 
		String[] vehicleJourneyDirectionCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
		vehicleJourneyDirectionCSVLine[TITLE_COLUMN] = DIRECTION_TITLE;
		csvLinesList.add(vehicleJourneyDirectionCSVLine);
		
		String[] vehicleJourneyTimetableCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
		vehicleJourneyTimetableCSVLine[TITLE_COLUMN] = TIMETABLE_TITLE;
		csvLinesList.add(vehicleJourneyTimetableCSVLine);
				
		String[] vehicleJourneySpecificCSVLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
		vehicleJourneySpecificCSVLine[TITLE_COLUMN] = SPECIFIC_TITLE;
		csvLinesList.add(vehicleJourneySpecificCSVLine);

		csvLinesList.add(BOARDING_POSITION_LINE_TITLES);

		HashMap<StopPoint, String[]> csvLinesByStopPoint = new HashMap<StopPoint, String[]>();
		for(Route route : routes){
			for(StopPoint stopPointOnRoute : route.getStopPoints()){
				StopArea boardingPosition = stopPointOnRoute.getContainedInStopArea();
				String[] csvLine = createBoardingPositionCsvLine(boardingPosition, vehicleJourneysCount);
				csvLinesByStopPoint.put(stopPointOnRoute, csvLine);
				csvLinesList.add(csvLine);
			}
			List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
			for(JourneyPattern journeyPattern : route.getJourneyPatterns()){
				vehicleJourneys.addAll(journeyPattern.getVehicleJourneys());
			}
			
			int vehicleJourneyColumn = TITLE_COLUMN + 1;
			for(VehicleJourney vehicleJourney : vehicleJourneys){
				vehicleJourneyDirectionCSVLine[vehicleJourneyColumn] = vehicleJourney.getRoute().getWayBack();
				List<Timetable> timetables = vehicleJourney.getTimetables();
				if(timetables != null && timetables.size() > 0){
					vehicleJourneyTimetableCSVLine[vehicleJourneyColumn] = timetables.get(0).getComment();
				}
				else{
					//TODO add report item
				}
				vehicleJourneySpecificCSVLine[vehicleJourneyColumn] = vehicleJourney.getVehicleTypeIdentifier();
				
				for(VehicleJourneyAtStop vehicleJourneyAtStop : vehicleJourney.getVehicleJourneyAtStops()){
					csvLinesByStopPoint.get(vehicleJourneyAtStop.getStopPoint())[vehicleJourneyColumn] = convertTimeToString(vehicleJourneyAtStop.getDepartureTime());
				}
				vehicleJourneyColumn++;
			}
		}
		
		return csvLinesList;
	}

	private String[] createBoardingPositionCsvLine(StopArea boardingPosition, int vehicleJourneysCount){
		String[] csvLine = new String[TITLE_COLUMN + 1 + vehicleJourneysCount];
		AreaCentroid areaCentroid = boardingPosition.getAreaCentroid();
		if(areaCentroid != null){
			ProjectedPoint projectedPoint = areaCentroid.getProjectedPoint();
			if(projectedPoint != null){
				csvLine[X_COLUMN] = asString(projectedPoint.getX());
				csvLine[Y_COLUMN] = asString(projectedPoint.getY());
			}
			csvLine[LATITUDE_COLUMN] = asString(areaCentroid.getLatitude());
			csvLine[LONGITUDE_COLUMN] = asString(areaCentroid.getLongitude());
			Address address = areaCentroid.getAddress();
			if(address != null){
				csvLine[ADDRESS_COLUMN] = address.getStreetName();
				csvLine[ZIPCODE_COLUMN] = address.getCountryCode();
			}
		}
		csvLine[AERAZONE_COLUMN] = getParentStopArea(boardingPosition.getParents()).getName();
		csvLine[STOPNAME_COLUMN] = boardingPosition.getName();		

		return csvLine;
	}

	private StopArea getParentStopArea(List<StopArea> parents) {
		for(StopArea parent : parents){
			if(!parent.getAreaType().equals(ChouetteAreaEnum.ITL)){
				return parent;
			}
		}
		return null;
	}
	
	public String convertTimeToString(Time time){
		long h = time.getTime()/1000;
		long s = h%60;
		h=h/60;
		long m = h % 60;
		h=h/60;
		return MessageFormat.format(mfHoraireHM,h,m);
	}
}
