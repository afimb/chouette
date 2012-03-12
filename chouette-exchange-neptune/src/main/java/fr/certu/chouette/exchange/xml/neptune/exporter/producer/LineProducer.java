package fr.certu.chouette.exchange.xml.neptune.exporter.producer;

import chouette.schema.LineExtension;
import chouette.schema.types.TransportModeNameType;
import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;
import fr.certu.chouette.model.neptune.type.TransportModeNameEnum;

public class LineProducer extends AbstractCastorNeptuneProducer<chouette.schema.Line, Line> {

   @Override
   public chouette.schema.Line produce(Line line) {
      chouette.schema.Line castorLine = new chouette.schema.Line();

      //
      populateFromModel(castorLine, line);

      castorLine.setComment(getNotEmptyString(line.getComment()));
      castorLine.setName(line.getName());
      castorLine.setNumber(line.getNumber());
      castorLine.setPublishedName(line.getPublishedName());
      castorLine.setPtNetworkIdShortcut(getNonEmptyObjectId(line.getPtNetwork()));

      try {
         TransportModeNameEnum transportModeName = line.getTransportModeName();
         if(transportModeName != null){
            castorLine.setTransportModeName(TransportModeNameType.fromValue(transportModeName.value()));
         }
      } catch (IllegalArgumentException e) {
         // TODO generate report
      }

      castorLine.setRegistration(getRegistration(line.getRegistrationNumber()));
      if(line.getLineEnds() != null){
         castorLine.setLineEnd(line.getLineEnds());
      }
      castorLine.setRouteId(NeptuneIdentifiedObject.extractObjectIds(line.getRoutes()));

      boolean hasExtensions = false;
      LineExtension castorLineExtension = new LineExtension();
      if (line.getUserNeeds() != null && !line.getUserNeeds().isEmpty())
      {
         castorLineExtension.setAccessibilitySuitabilityDetails(extractAccessibilitySuitabilityDetails(line.getUserNeeds()));
         hasExtensions = true;
      }
      if (line.getMobilityRestrictedSuitable() != null && line.getMobilityRestrictedSuitable())
      {
         castorLineExtension.setMobilityRestrictedSuitability(line.getMobilityRestrictedSuitable());
         hasExtensions = true;
      }
      // castorLineExtension.setStableId(stableId); ???
      if (hasExtensions)
         castorLine.setLineExtension(castorLineExtension);

      return castorLine;
   }
}
