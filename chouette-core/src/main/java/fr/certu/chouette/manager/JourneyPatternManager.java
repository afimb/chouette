package fr.certu.chouette.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.certu.chouette.common.ChouetteException;
import fr.certu.chouette.filter.DetailLevelEnum;
import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.JourneyPattern;
import fr.certu.chouette.model.neptune.StopPoint;
import fr.certu.chouette.model.neptune.VehicleJourney;
import fr.certu.chouette.model.user.User;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.validation.ValidationParameters;
import fr.certu.chouette.plugin.validation.ValidationReport;

@SuppressWarnings("unchecked")
public class JourneyPatternManager extends AbstractNeptuneManager<JourneyPattern> 
{

	public JourneyPatternManager() 
	{
		super(JourneyPattern.class);
	}

	@Override
	protected Report propagateValidation(User user, List<JourneyPattern> beans,
			ValidationParameters parameters,boolean propagate) 
	throws ChouetteException 
	{
		Report globalReport = new ValidationReport();

		// aggregate dependent objects for validation
		Set<StopPoint> stopPoints = new HashSet<StopPoint>();
		List<VehicleJourney> vehicleJourneys = new ArrayList<VehicleJourney>();
		for (JourneyPattern bean : beans) 
		{
			if (bean.getStopPoints() != null)
				stopPoints.addAll(bean.getStopPoints());
			if (bean.getVehicleJourneys() != null)
				vehicleJourneys.addAll(bean.getVehicleJourneys());
		}

		// propagate validation on StopPoints
		if (stopPoints.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<StopPoint> manager = (AbstractNeptuneManager<StopPoint>) getManager(StopPoint.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, Arrays.asList(stopPoints.toArray(new StopPoint[0])), parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, Arrays.asList(stopPoints.toArray(new StopPoint[0])), parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}

		// propagate validation on journey patterns
		if (vehicleJourneys.size() > 0)
		{
			Report report = null;
			AbstractNeptuneManager<VehicleJourney> manager = (AbstractNeptuneManager<VehicleJourney>) getManager(VehicleJourney.class);
			if (manager.canValidate())
			{
				report = manager.validate(user, vehicleJourneys, parameters,propagate);
			}
			else
			{
				report = manager.propagateValidation(user, vehicleJourneys, parameters,propagate);
			}
			if (report != null)
			{
				globalReport.addAll(report.getItems());
				globalReport.updateStatus(report.getStatus());
			}
		}		

		return globalReport;
	}
	@Override
	public void remove(User user,JourneyPattern journeyPattern,boolean propagate) throws ChouetteException{
		INeptuneManager<VehicleJourney> vjManager = (INeptuneManager<VehicleJourney>) getManager(VehicleJourney.class);
		Filter filter = Filter.getNewEqualsFilter("journeyPattern.id", journeyPattern.getId());
		DetailLevelEnum level = DetailLevelEnum.ATTRIBUTE;
		List<VehicleJourney> vehicleJourneys = vjManager.getAll(user, filter, level);
		if(vehicleJourneys != null && !vehicleJourneys.isEmpty())
			vjManager.removeAll(user, vehicleJourneys,propagate);
		super.remove(user, journeyPattern,propagate);
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
