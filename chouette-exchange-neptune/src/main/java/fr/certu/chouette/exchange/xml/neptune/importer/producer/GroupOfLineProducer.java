package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import org.trident.schema.trident.GroupOfLineType;

import fr.certu.chouette.model.neptune.GroupOfLine;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;
/**
 * 
 * @author Michel Etienne
 *
 */
public class GroupOfLineProducer extends AbstractModelProducer<GroupOfLine, GroupOfLineType>{

	@Override
	public GroupOfLine produce(String sourceFile,GroupOfLineType xmlGroupOfLine, ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) {
		GroupOfLine groupOfLine = new GroupOfLine();
		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(groupOfLine, xmlGroupOfLine,importReport);
		// Name optional
		groupOfLine.setName(getNonEmptyTrimedString(xmlGroupOfLine.getName()));		
		
		// Comment optional
		groupOfLine.setComment(getNonEmptyTrimedString(xmlGroupOfLine.getComment()));
		// remove line refs for cross file checking
		List<String> xmlLineIds = new ArrayList<String>(xmlGroupOfLine.getLineId());
		xmlGroupOfLine.getLineId().clear();
		
		GroupOfLine sharedBean = getOrAddSharedData(sharedData, groupOfLine, sourceFile, xmlGroupOfLine,validationReport);
		if (sharedBean != null) groupOfLine = sharedBean;
		
		xmlGroupOfLine.getLineId().addAll(xmlLineIds);
		// LineIds [O..w]
		
		for(String xmlLineId : xmlLineIds)
		{
			String lineId = getNonEmptyTrimedString(xmlLineId);
			if(lineId == null)
			{
				// should not happen
			}
			else
			{
				groupOfLine.addLineId(lineId);
			}
		}
		
		
		return groupOfLine;
	}

}
