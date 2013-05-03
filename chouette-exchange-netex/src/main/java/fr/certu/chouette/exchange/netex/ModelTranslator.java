/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.AccessLink;
import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.model.neptune.Company;
import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.Route;
import fr.certu.chouette.model.neptune.StopArea;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.neptune.type.ChouetteAreaEnum;
import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import fr.certu.chouette.model.neptune.type.ServiceStatusValueEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

/**
 *
 * @author marc
 */
public class ModelTranslator {
	private static final Logger logger = Logger.getLogger(ModelTranslator.class);
	
    public String convertToNMTOKEN( String s) {
        return s.replaceAll( " ", "-");
    }
    
    public String netexId( NeptuneIdentifiedObject model) {
        if (model==null)
            return null;
        return model.objectIdPrefix() + ":" + netexModelName( model) + 
                ":" +  model.objectIdSuffix();
    }
    public String netexMockId( NeptuneIdentifiedObject model, String mock) {
    if (model==null)
        return null;
    return model.objectIdPrefix() + ":" + mock + 
            ":" +  model.objectIdSuffix();
    }
    public String trainNumberId( Long number) {
        return "Local:TrainNumber:"+number;
    }
    public Long readTrainNumberId( String trainNumber) {
        try {
            String number = trainNumber.replaceFirst( "Local:TrainNumber:", "");
            return Long.parseLong(number);
        } catch (Exception e) {
            return null;
        }
    }


    public String netexModelName( NeptuneIdentifiedObject model) {
        if (model==null)
            return null;
        if ( model instanceof StopArea) {
            return "StopArea";
        } else if ( model instanceof AccessPoint) {
            return "AccessPoint";
        } else if ( model instanceof Company) {
            return "Company";
        } else if ( model instanceof AccessLink) {
            return "AccessLink";
        } else if ( model instanceof StopPoint) {
            return "StopPoint";
        } else if ( model instanceof PTNetwork) {
            return "GroupOfLine";
        } else if ( model instanceof Line) {
            return "Line";
        } else if ( model instanceof Route) {
            return "Route";
        } else if ( model instanceof GroupOfLine) {
            return "GroupOfLine";
        } else if ( model instanceof JourneyPattern) {
            return "JourneyPattern";
        } else if ( model instanceof ConnectionLink) {
            return "ConnectionLink";
        } else if ( model instanceof Timetable) {
            return "Timetable";
        } else if ( model instanceof VehicleJourney) {
            return "VehicleJourney";
        } else {
            return null;
        }
    }
    
    private String firstLetterUpcase(String word)
    {
        StringBuilder sb = new StringBuilder(word); // Puts the first caracter upcase
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
        return sb.toString();
    }
    
    public String toPTDirection( PTDirectionEnum ptDirection) {
        if (ptDirection==null)
            return null;
        return ptDirection.toString();
    }
    public PTDirectionEnum readPTDirection( String netexPTDirection) {
        if (netexPTDirection==null)
            return null;
        // netexSourceType is a free text
        PTDirectionEnum ptDirection = null;
        try { ptDirection = PTDirectionEnum.fromValue(firstLetterUpcase( netexPTDirection)); } 
        catch ( Exception e) {
        	logger.error("unable to translate "+netexPTDirection+" as PTDirection");
        }
        return ptDirection;
    }
    
    public String toPTNetworkSourceType( PTNetworkSourceTypeEnum sourceType) {
        if (sourceType==null)
            return null;
        return sourceType.toString();
    }
    public PTNetworkSourceTypeEnum readPTNetworkSourceType( String netexSourceType) {
        if (netexSourceType==null)
            return null;
        // netexSourceType is a free text
        PTNetworkSourceTypeEnum sourceType = null;
        try { sourceType = PTNetworkSourceTypeEnum.fromValue(firstLetterUpcase( netexSourceType)); } 
        catch ( Exception e) {
        	logger.error("unable to translate "+netexSourceType+" as PTNetworkSourceType");
        }
        return sourceType;
    }
    
    public ServiceStatusValueEnum readServiceAlteration( String netexServiceAlteration) {
        if ( netexServiceAlteration==null)
            return null;
        if (netexServiceAlteration.equals("planned"))
            return ServiceStatusValueEnum.NORMAL;
        else if (netexServiceAlteration.equals("cancellation"))
            return ServiceStatusValueEnum.CANCELLED;
        else if (netexServiceAlteration.equals("extraJourney"))
            return ServiceStatusValueEnum.INCREASEDSERVICE;
        else 
            return null;
    }
    
    public String toServiceAlteration( ServiceStatusValueEnum serviceStatusValue) {
        if (serviceStatusValue==null)
            return null;
        switch(serviceStatusValue) {
            case NORMAL:
            case DELAYED:
            case EARLY:
                return "planned";
            case CANCELLED:
            case DISRUPTED:
            case NOTSTOPPING:
            case REROUTED:
            case REDUCEDSERVICE:
                return "cancellation";
            case INCREASEDSERVICE:
                return "extraJourney";
             default:
                 return null;
        }
    }
    
    public String toLinkType( ConnectionLinkTypeEnum linkType) {
        if (linkType==null)
            return null;
        switch(linkType) {
            case UNDERGROUND:
                return "indoors";
            case OVERGROUND:
                return "outdoors";
            case MIXED:
                return "mixed";
             default:
                 return "unknown";
        }
    }
    public ConnectionLinkTypeEnum readLinkType( String netexLinkType) {
        if ( netexLinkType==null)
            return null;
        if (netexLinkType.equals("indoors"))
            return ConnectionLinkTypeEnum.UNDERGROUND;
        else if (netexLinkType.equals("outdoors"))
            return ConnectionLinkTypeEnum.OVERGROUND;
        else if (netexLinkType.equals("mixed"))
            return ConnectionLinkTypeEnum.MIXED;
        else 
            return null;
    }

    // AllVehicleModesOfTransportEnumeration
//			<xsd:enumeration value="all"/>
//			<xsd:enumeration value="unknown"/>
//			<xsd:enumeration value="bus"/>
//			<xsd:enumeration value="trolleyBus"/>
//			<xsd:enumeration value="tram"/>
//			<xsd:enumeration value="coach"/>
//			<xsd:enumeration value="rail"/>
//			<xsd:enumeration value="intercityRail"/>
//			<xsd:enumeration value="urbanRail"/>
//			<xsd:enumeration value="metro"/>
//			<xsd:enumeration value="air"/>
//			<xsd:enumeration value="water"/>
//			<xsd:enumeration value="cableway"/>
//			<xsd:enumeration value="funicular"/>
//			<xsd:enumeration value="taxi"/>
//			<xsd:enumeration value="selfDrive">    
    public String toTransportModeNetex( TransportModeNameEnum transportMode) {
        switch(transportMode) {
            case AIR:
                return "air";
            case TRAIN:
                return "rail";
            case LONGDISTANCETRAIN:
                return "intercityRail";
            case LONGDISTANCETRAIN_2:
                return "intercityRail";
            case LOCALTRAIN:
                return "urbanRail";
            case RAPIDTRANSIT:
                return "urbanRail";
            case METRO:
                return "metro";
            case TRAMWAY:
                return "tram";
            case COACH:
                return "coach";
            case BUS:
                return "bus";
            case FERRY:
                return "water";
            case WATERBORNE:
                return "water";
            case PRIVATEVEHICLE:
                return "selfDrive";
            case WALK:
                return "selfDrive";
            case TROLLEYBUS:
                return "trolleyBus";
            case BICYCLE:
                return "selfDrive";
            case SHUTTLE:
                return "rail";
            case TAXI:
                return "taxi";
            case VAL:
                return "rail";
            case OTHER:
                return "unknown";
            default:
                return "";
            }
        }
        
        public TransportModeNameEnum readTransportMode( String netexMode) {
            if ( netexMode==null)
                return null;
            else if ( netexMode.equals("air")) 
                return TransportModeNameEnum.AIR;
            else if ( netexMode.equals("rail")) 
                return TransportModeNameEnum.TRAIN;
            else if ( netexMode.equals("intercityRail")) 
                return TransportModeNameEnum.LONGDISTANCETRAIN;
            else if ( netexMode.equals("urbanRail")) 
                return TransportModeNameEnum.LOCALTRAIN;
            else if ( netexMode.equals("metro")) 
                return TransportModeNameEnum.METRO;
            else if ( netexMode.equals("tram")) 
                return TransportModeNameEnum.TRAMWAY;
            else if ( netexMode.equals("coach")) 
                return TransportModeNameEnum.COACH;
            else if ( netexMode.equals("bus")) 
                return TransportModeNameEnum.BUS;
            else if ( netexMode.equals("water")) 
                return TransportModeNameEnum.FERRY;
            else if ( netexMode.equals("selfDrive")) 
                return TransportModeNameEnum.WALK;
            else if ( netexMode.equals("trolleyBus")) 
                return TransportModeNameEnum.TROLLEYBUS;
            else if ( netexMode.equals("taxi")) 
                return TransportModeNameEnum.TAXI;
            else if ( netexMode.equals("unknown")) 
                return TransportModeNameEnum.OTHER;
            else  
                return TransportModeNameEnum.OTHER;
        }
        
        public String toDayTypeNetex( DayTypeEnum dayType) {
            if(dayType == null)
                return null;
           
            switch(dayType) {
                case MONDAY: 
                    return "Monday";
                case TUESDAY: 
                    return "Tuesday";
                case WEDNESDAY: 
                    return "Wednesday";                    
                case THURSDAY: 
                    return "Thursday";
                case FRIDAY: 
                    return "Friday";
                case SATURDAY: 
                    return "Saturday";
                case SUNDAY: 
                    return "Sunday";                
                default:
                    return null;
            }            
        }
        
        public DayTypeEnum readDayType( String dayType) {
            if ( dayType==null)
                return null;
            else if ( dayType.equals("Monday")) 
                return DayTypeEnum.MONDAY;
            else if ( dayType.equals("Tuesday")) 
                return DayTypeEnum.TUESDAY;
            else if ( dayType.equals("Wednesday")) 
                return DayTypeEnum.WEDNESDAY;
            else if ( dayType.equals("Thursday")) 
                return DayTypeEnum.THURSDAY;
            else if ( dayType.equals("Friday")) 
                return DayTypeEnum.FRIDAY;
            else if ( dayType.equals("Saturday")) 
                return DayTypeEnum.SATURDAY;
            else if ( dayType.equals("Sunday")) 
                return DayTypeEnum.SUNDAY;
            else
                return null;            
        }
        
        
}
