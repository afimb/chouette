package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import org.trident.schema.trident.PTLinkType;

import fr.certu.chouette.model.neptune.PTLink;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class PTLinkProducer extends AbstractModelProducer<PTLink, PTLinkType>
{

   @Override
   public PTLink produce(String sourceFile, PTLinkType xmlPTLink,
         ReportItem importReport, PhaseReportItem validationReport,
         SharedImportedData sharedData, UnsharedImportedData unshareableData)
   {
      PTLink ptLink = new PTLink();

      // objectId, objectVersion, creatorId, creationTime
      populateFromCastorNeptune(ptLink, xmlPTLink, importReport);

      // Name optional : unused and unchecked
      // ptLink.setName(getNonEmptyTrimedString(xmlPTLink.getName()));

      // Comment optional : unused and unchecked
      // ptLink.setComment(getNonEmptyTrimedString(xmlPTLink.getComment2()));

      // StartOfLink mandatory
      ptLink.setStartOfLinkId(getNonEmptyTrimedString(xmlPTLink
            .getStartOfLink()));

      // EndOfLink mandatory
      ptLink.setEndOfLinkId(getNonEmptyTrimedString(xmlPTLink.getEndOfLink()));

      // LinkDistance optional
      ptLink.setLinkDistance(xmlPTLink.getLinkDistance());

      return ptLink;
   }

}
