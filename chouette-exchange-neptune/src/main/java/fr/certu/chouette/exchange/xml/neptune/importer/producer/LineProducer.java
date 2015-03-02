package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.awt.Color;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.trident.schema.trident.LineExtensionType;

import fr.certu.chouette.exchange.xml.neptune.JsonExtension;
import fr.certu.chouette.exchange.xml.neptune.importer.Context;
import fr.certu.chouette.model.neptune.Footnote;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.PTNetwork;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;
import fr.certu.chouette.model.neptune.type.UserNeedEnum;
import fr.certu.chouette.plugin.exchange.report.ExchangeReportItem;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;

@Log4j
public class LineProducer 
      extends
      AbstractModelProducer<Line, org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.Line>
      implements JsonExtension
{
   @Override
   public Line produce(Context context,
         org.trident.schema.trident.ChouettePTNetworkType.ChouetteLineDescription.Line xmlLine)
   {
      Line line = new Line();
      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(context, line, xmlLine);

      // Name optional
      line.setName(getNonEmptyTrimedString(xmlLine.getName()));

      // Number optional
      line.setNumber(getNonEmptyTrimedString(xmlLine.getNumber()));

      // PublishedName optional
      line.setPublishedName(getNonEmptyTrimedString(xmlLine.getPublishedName()));

      // TransportModeName optional
      if (xmlLine.getTransportModeName() != null)
      {
         try
         {
            line.setTransportModeName(TransportModeNameEnum.valueOf(xmlLine
                  .getTransportModeName().value()));
         } catch (IllegalArgumentException e)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.UNKNOWN_ENUM, Report.STATE.ERROR,
                  "TransportModeName", xmlLine.getTransportModeName().value());
            context.getImportReport().addItem(item);
         }
      } else
      {
         // TransportModeName is mandatory in database
         log.warn("Line " + line.getObjectId()
               + " without TransportMode , forced to Other");
         line.setTransportModeName(TransportModeNameEnum.Other);
      }
      // LineEnd [0..w] : TODO
      List<String> jaxbLineEnds = xmlLine.getLineEnd();
      for (String lineEnd : jaxbLineEnds)
      {
         String realLineEnd = getNonEmptyTrimedString(lineEnd);
         if (realLineEnd == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR,
                  "LineEnd");
            context.getImportReport().addItem(item);
         } else
         {
            line.addLineEnd(realLineEnd);
         }
      }

      // RouteId [1..w]
      List<String> jaxbRouteIds = xmlLine.getRouteId();
      for (String routeId : jaxbRouteIds)
      {
         String realRouteId = getNonEmptyTrimedString(routeId);
         if (realRouteId == null)
         {
            ReportItem item = new ExchangeReportItem(
                  ExchangeReportItem.KEY.EMPTY_TAG, Report.STATE.ERROR,
                  "RouteId");
            context.getImportReport().addItem(item);
         } else
         {
            line.addRouteId(realRouteId);
         }
      }
      if (line.getRouteIds() == null)
      {
         ReportItem item = new ExchangeReportItem(
               ExchangeReportItem.KEY.EMPTY_LINE, Report.STATE.ERROR,
               line.getObjectId());
         context.getImportReport().addItem(item);
      }

      // Registration optional
      line.setRegistrationNumber(getRegistrationNumber(context,
            xmlLine.getRegistration()));

      // PtNetworkShortcut optional : correct old fashioned form
      String ptNetworkId = getNonEmptyTrimedString(xmlLine
            .getPtNetworkIdShortcut());
      if (ptNetworkId != null && ptNetworkId.contains(":PTNetwork:"))
      {
         ptNetworkId = ptNetworkId.replace(":PTNetwork:", ":"
               + PTNetwork.PTNETWORK_KEY + ":");
      }
      line.setPtNetworkIdShortcut(ptNetworkId);

      // Comment optional
      parseComment(getNonEmptyTrimedString(xmlLine.getComment()),line);

      // LineExtension optional
      LineExtensionType xmlLineExtension = xmlLine.getLineExtension();
      if (xmlLineExtension != null)
      {

         // MobilityRestrictedSuitability
         if (xmlLineExtension.isMobilityRestrictedSuitability() != null)
            line.setMobilityRestrictedSuitable(xmlLineExtension
                  .isMobilityRestrictedSuitability());

         if (xmlLineExtension.getAccessibilitySuitabilityDetails() != null)
         {
            for (Object xmlAccessibilitySuitabilityDetailsItem : xmlLineExtension
                  .getAccessibilitySuitabilityDetails()
                  .getMobilityNeedOrPsychosensoryNeedOrMedicalNeed())
            {

               try
               {
                  line.addUserNeed(UserNeedEnum
                        .fromValue(xmlAccessibilitySuitabilityDetailsItem
                              .toString()));
               } catch (IllegalArgumentException e)
               {
                  log.error("unknown userneeds enum "
                        + xmlAccessibilitySuitabilityDetailsItem.toString());
                  ReportItem item = new ExchangeReportItem(
                        ExchangeReportItem.KEY.UNKNOWN_ENUM,
                        Report.STATE.ERROR, "UserNeed",
                        xmlAccessibilitySuitabilityDetailsItem.toString());
                  context.getImportReport().addItem(item);
               }

            }
         }

      }

      // return null if in conflict with other files, else return object
      return checkUnsharedData(context, line, xmlLine);
   }
   
   protected void parseComment(String comment, Line line)
   {
      if (comment != null && comment.startsWith("{") && comment.endsWith("}"))
      {
         // parse json comment
         JSONObject json = new JSONObject(comment);
         line.setComment(json.optString(COMMENT,null));
         if (json.has(FOOTNOTES))
         {
            // scan footnotes
            JSONArray footNotes = json.getJSONArray(FOOTNOTES);
            for (int i = 0; i < footNotes.length(); i++)
            {
               JSONObject footNote = footNotes.getJSONObject(i);
               String key = footNote.optString(KEY,null);
               String code = footNote.optString(CODE,null);
               String label = footNote.optString(LABEL,null);
               if (key != null && code != null && label != null)
               {
                  Footnote note = new Footnote();
                  note.setLine(line);
                  note.setLabel(label);
                  note.setCode(code);
                  note.setKey(key);
                  line.getFootnotes().add(note);
               }
               
            }
         }
         if (json.has(FLEXIBLE_SERVICE))
         {
            line.setFlexibleService(Boolean.valueOf(json.getBoolean(FLEXIBLE_SERVICE)));
         }
         if (json.has(TEXT_COLOR))
         {
            try
            {
               Color.decode("0x"+json.getString(TEXT_COLOR));
               line.setTextColor(json.getString(TEXT_COLOR));
            }
            catch (Exception e)
            {
               log.error("cannot parse text color "+json.getString(TEXT_COLOR),e);
            }
         }
         if (json.has(LINE_COLOR))
         {
            try
            {
               Color.decode("0x"+json.getString(LINE_COLOR));
               line.setColor(json.getString(LINE_COLOR));
            }
            catch (Exception e)
            {
               log.error("cannot parse color "+json.getString(LINE_COLOR),e);
            }
         }
      }
      else
      {
         // normal comment
         line.setComment(comment);
      }
   }

}
