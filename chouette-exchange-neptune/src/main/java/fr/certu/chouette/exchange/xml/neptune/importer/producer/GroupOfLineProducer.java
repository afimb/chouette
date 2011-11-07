package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import fr.certu.chouette.exchange.xml.neptune.importer.SharedImportedData;
import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.plugin.report.ReportItem;
/**
 * 
 * @author mamadou keira
 *
 */
public class GroupOfLineProducer extends AbstractModelProducer<GroupOfLine, chouette.schema.GroupOfLine>{

	@Override
	public GroupOfLine produce(chouette.schema.GroupOfLine xmlGroupOfLine, ReportItem report,SharedImportedData sharedData) {
		GroupOfLine groupOfLine = new GroupOfLine();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(groupOfLine, xmlGroupOfLine,report);
		// Name optional
		groupOfLine.setName(getNonEmptyTrimedString(xmlGroupOfLine.getName()));		
		// Comment optional
		groupOfLine.setComment(getNonEmptyTrimedString(xmlGroupOfLine.getComment()));
		// LineIds [1..n]
		String[] castorLineIds = xmlGroupOfLine.getLineId();
		for(String castorLineId : castorLineIds){
			String lineId = getNonEmptyTrimedString(castorLineId);
			if(lineId != null)
				groupOfLine.addLineId(lineId);
			else{
				//TODO
			}
		}
		
		return groupOfLine;
	}

}
