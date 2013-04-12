/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex;

import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;


/**
 *
 * @author marc
 */
public class EnumTranslator {

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
            if ( netexMode.equals("air")) 
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
}
