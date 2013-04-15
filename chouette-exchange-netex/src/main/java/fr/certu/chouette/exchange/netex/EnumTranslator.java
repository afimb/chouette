/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.certu.chouette.exchange.netex;

import fr.certu.chouette.model.neptune.type.ConnectionLinkTypeEnum;
import fr.certu.chouette.model.neptune.type.PTDirectionEnum;
import fr.certu.chouette.model.neptune.type.PTNetworkSourceTypeEnum;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;


/**
 *
 * @author marc
 */
public class EnumTranslator {
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
        catch ( Exception e) {}
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
        catch ( Exception e) {}
        return sourceType;
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
}
