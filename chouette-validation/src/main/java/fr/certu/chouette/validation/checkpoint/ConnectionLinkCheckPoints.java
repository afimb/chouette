package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.ConnectionLink;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class ConnectionLinkCheckPoints extends AbstractValidation implements ICheckPointPlugin<ConnectionLink>
{

	@Override
	public void check(List<ConnectionLink> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		
	}


}
