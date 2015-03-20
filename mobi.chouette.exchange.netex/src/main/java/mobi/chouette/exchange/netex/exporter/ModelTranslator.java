/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mobi.chouette.exchange.netex.exporter;

import mobi.chouette.model.AccessLink;
import mobi.chouette.model.AccessPoint;
import mobi.chouette.model.Company;
import mobi.chouette.model.ConnectionLink;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.JourneyPattern;
import mobi.chouette.model.Line;
import mobi.chouette.model.NeptuneIdentifiedObject;
import mobi.chouette.model.Network;
import mobi.chouette.model.Route;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.StopPoint;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import mobi.chouette.model.type.ConnectionLinkTypeEnum;
import mobi.chouette.model.type.DayTypeEnum;
import mobi.chouette.model.type.PTDirectionEnum;
import mobi.chouette.model.type.PTNetworkSourceTypeEnum;
import mobi.chouette.model.type.TransportModeNameEnum;

import org.apache.log4j.Logger;


/**
 * 
 * @author marc
 */
public class ModelTranslator
{
   private static final Logger logger = Logger.getLogger(ModelTranslator.class);

   public String convertToNMTOKEN(String s)
   {
      return s.replaceAll(" ", "-");
   }

   public String netexId(NeptuneIdentifiedObject model)
   {
      if (model == null)
         return null;
      return model.objectIdPrefix() + ":" + netexModelName(model) + ":"
            + model.objectIdSuffix();
   }

   public String netexMockId(NeptuneIdentifiedObject model, String mock)
   {
      if (model == null)
         return null;
      return model.objectIdPrefix() + ":" + mock + ":" + model.objectIdSuffix();
   }

   public String trainNumberId(Long number)
   {
      return "Local:TrainNumber:" + number;
   }

   public Long readTrainNumberId(String trainNumber)
   {
      try
      {
         String number = trainNumber.replaceFirst("Local:TrainNumber:", "");
         return Long.parseLong(number);
      } catch (Exception e)
      {
         return null;
      }
   }

   public String netexModelName(NeptuneIdentifiedObject model)
   {
      if (model == null)
         return null;
      if (model instanceof StopArea)
      {
         return "StopArea";
      } else if (model instanceof AccessPoint)
      {
         return "AccessPoint";
      } else if (model instanceof Company)
      {
         return "Company";
      } else if (model instanceof AccessLink)
      {
         return "AccessLink";
      } else if (model instanceof StopPoint)
      {
         return "StopPoint";
      } else if (model instanceof Network)
      {
         return "GroupOfLine";
      } else if (model instanceof Line)
      {
         return "Line";
      } else if (model instanceof Route)
      {
         return "Route";
      } else if (model instanceof GroupOfLine)
      {
         return "GroupOfLine";
      } else if (model instanceof JourneyPattern)
      {
         return "JourneyPattern";
      } else if (model instanceof ConnectionLink)
      {
         return "ConnectionLink";
      } else if (model instanceof Timetable)
      {
         return "Timetable";
      } else if (model instanceof VehicleJourney)
      {
         return "VehicleJourney";
      } else
      {
         return null;
      }
   }

   private String firstLetterUpcase(String word)
   {
      StringBuilder sb = new StringBuilder(word); // Puts the first caracter
      // upcase
      sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
      return sb.toString();
   }

   public String toPTDirection(PTDirectionEnum ptDirection)
   {
      if (ptDirection == null)
         return null;
      return ptDirection.toString();
   }

   public PTDirectionEnum readPTDirection(String netexPTDirection)
   {
      if (netexPTDirection == null)
         return null;
      // netexSourceType is a free text
      PTDirectionEnum ptDirection = null;

      try
      {
         ptDirection = PTDirectionEnum
               .valueOf(firstLetterUpcase(netexPTDirection));
      } catch (Exception e)
      {
         logger.error("unable to translate " + netexPTDirection
               + " as PTDirection");
      }
      return ptDirection;
   }

   public String toPTNetworkSourceType(PTNetworkSourceTypeEnum sourceType)
   {
      if (sourceType == null)
         return null;
      return sourceType.toString();
   }

   public PTNetworkSourceTypeEnum readPTNetworkSourceType(String netexSourceType)
   {
      if (netexSourceType == null)
         return null;
      // netexSourceType is a free text
      PTNetworkSourceTypeEnum sourceType = null;
      try
      {
         sourceType = PTNetworkSourceTypeEnum
               .valueOf(firstLetterUpcase(netexSourceType));
      } catch (Exception e)
      {
         logger.error("unable to translate " + netexSourceType
               + " as PTNetworkSourceType");
      }
      return sourceType;
   }

   public String toLinkType(ConnectionLinkTypeEnum linkType)
   {
      if (linkType == null)
         return null;
      switch (linkType)
      {
      case Underground:
         return "indoors";
      case Overground:
         return "outdoors";
      case Mixed:
         return "mixed";
      default:
         return "unknown";
      }
   }

   public ConnectionLinkTypeEnum readLinkType(String netexLinkType)
   {
      if (netexLinkType == null)
         return null;
      if (netexLinkType.equals("indoors"))
         return ConnectionLinkTypeEnum.Underground;
      else if (netexLinkType.equals("outdoors"))
         return ConnectionLinkTypeEnum.Overground;
      else if (netexLinkType.equals("mixed"))
         return ConnectionLinkTypeEnum.Mixed;
      else
         return null;
   }

   // AllVehicleModesOfTransportEnumeration
   // <xsd:enumeration value="all"/>
   // <xsd:enumeration value="unknown"/>
   // <xsd:enumeration value="bus"/>
   // <xsd:enumeration value="trolleyBus"/>
   // <xsd:enumeration value="tram"/>
   // <xsd:enumeration value="coach"/>
   // <xsd:enumeration value="rail"/>
   // <xsd:enumeration value="intercityRail"/>
   // <xsd:enumeration value="urbanRail"/>
   // <xsd:enumeration value="metro"/>
   // <xsd:enumeration value="air"/>
   // <xsd:enumeration value="water"/>
   // <xsd:enumeration value="cableway"/>
   // <xsd:enumeration value="funicular"/>
   // <xsd:enumeration value="taxi"/>
   // <xsd:enumeration value="selfDrive">
   public String toTransportModeNetex(TransportModeNameEnum transportMode)
   {
      switch (transportMode)
      {
      case Air:
         return "air";
      case Train:
         return "rail";
      case LongDistanceTrain:
         return "intercityRail";
      case LongDistanceTrain_2:
         return "intercityRail";
      case LocalTrain:
         return "urbanRail";
      case RapidTransit:
         return "urbanRail";
      case Metro:
         return "metro";
      case Tramway:
         return "tram";
      case Coach:
         return "coach";
      case Bus:
         return "bus";
      case Ferry:
         return "water";
      case Waterborne:
         return "water";
      case PrivateVehicle:
         return "selfDrive";
      case Walk:
         return "selfDrive";
      case Trolleybus:
         return "trolleyBus";
      case Bicycle:
         return "selfDrive";
      case Shuttle:
         return "rail";
      case Taxi:
         return "taxi";
      case Val:
         return "rail";
      case Other:
         return "unknown";
      default:
         return "";
      }
   }

   public TransportModeNameEnum readTransportMode(String netexMode)
   {
      if (netexMode == null)
         return null;
      else if (netexMode.equals("air"))
         return TransportModeNameEnum.Air;
      else if (netexMode.equals("rail"))
         return TransportModeNameEnum.Train;
      else if (netexMode.equals("intercityRail"))
         return TransportModeNameEnum.LongDistanceTrain;
      else if (netexMode.equals("urbanRail"))
         return TransportModeNameEnum.LocalTrain;
      else if (netexMode.equals("metro"))
         return TransportModeNameEnum.Metro;
      else if (netexMode.equals("tram"))
         return TransportModeNameEnum.Tramway;
      else if (netexMode.equals("coach"))
         return TransportModeNameEnum.Coach;
      else if (netexMode.equals("bus"))
         return TransportModeNameEnum.Bus;
      else if (netexMode.equals("water"))
         return TransportModeNameEnum.Ferry;
      else if (netexMode.equals("selfDrive"))
         return TransportModeNameEnum.Walk;
      else if (netexMode.equals("trolleyBus"))
         return TransportModeNameEnum.Trolleybus;
      else if (netexMode.equals("taxi"))
         return TransportModeNameEnum.Taxi;
      else if (netexMode.equals("unknown"))
         return TransportModeNameEnum.Other;
      else
         return TransportModeNameEnum.Other;
   }

   public String toDayTypeNetex(DayTypeEnum dayType)
   {
      if (dayType == null)
         return null;

      switch (dayType)
      {
      case Monday:
         return "Monday";
      case Tuesday:
         return "Tuesday";
      case Wednesday:
         return "Wednesday";
      case Thursday:
         return "Thursday";
      case Friday:
         return "Friday";
      case Saturday:
         return "Saturday";
      case Sunday:
         return "Sunday";
      default:
         return null;
      }
   }

   public DayTypeEnum readDayType(String dayType)
   {
      if (dayType == null)
         return null;
      else if (dayType.equals("Monday"))
         return DayTypeEnum.Monday;
      else if (dayType.equals("Tuesday"))
         return DayTypeEnum.Tuesday;
      else if (dayType.equals("Wednesday"))
         return DayTypeEnum.Wednesday;
      else if (dayType.equals("Thursday"))
         return DayTypeEnum.Thursday;
      else if (dayType.equals("Friday"))
         return DayTypeEnum.Friday;
      else if (dayType.equals("Saturday"))
         return DayTypeEnum.Saturday;
      else if (dayType.equals("Sunday"))
         return DayTypeEnum.Sunday;
      else
         return null;
   }

}
