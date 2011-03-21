package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.plugin.report.ReportItem;

public class GroupOfLineProducer extends AbstractModelProducer<GroupOfLine, chouette.schema.GroupOfLine>{

	@Override
	public GroupOfLine produce(chouette.schema.GroupOfLine xmlGroupOfLine, ReportItem report) {
		GroupOfLine groupOfLine = new GroupOfLine();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(groupOfLine, xmlGroupOfLine,report);
		// Name optional
		groupOfLine.setName(getNonEmptyTrimedString(xmlGroupOfLine.getName()));		
		// Comment optional
		groupOfLine.setComment(getNonEmptyTrimedString(xmlGroupOfLine.getComment()));
		
		
		return groupOfLine;
	}

}
