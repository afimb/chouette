package fr.certu.chouette.validation.checkpoint;

import java.util.List;

import org.json.JSONObject;

import fr.certu.chouette.model.neptune.Line;
import fr.certu.chouette.plugin.validation.ICheckPointPlugin;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class LineCheckPoints extends AbstractValidation implements ICheckPointPlugin<Line>
{

	@Override
	public void check(List<Line> beans, JSONObject parameters,
			PhaseReportItem report) 
	{
		// TODO Auto-generated method stub
		
	}


}
