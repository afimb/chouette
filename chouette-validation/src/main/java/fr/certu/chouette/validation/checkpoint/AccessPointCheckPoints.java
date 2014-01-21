package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.AccessPoint;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class AccessPointCheckPoints extends AbstractValidation implements ICheckPointPlugin<AccessPoint>
{

	@Override
	public void check(List<AccessPoint> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		if (isEmpty(beans)) return;
		
	}


}
