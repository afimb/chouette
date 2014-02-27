package fr.certu.chouette.exchange.xml.neptune.importer.producer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.trident.schema.trident.DayTypeType;
import org.trident.schema.trident.PeriodType;
import org.trident.schema.trident.TimetableType;

import fr.certu.chouette.model.neptune.Period;
import fr.certu.chouette.model.neptune.Timetable;
import fr.certu.chouette.model.neptune.type.DayTypeEnum;
import fr.certu.chouette.plugin.exchange.SharedImportedData;
import fr.certu.chouette.plugin.exchange.UnsharedImportedData;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.PhaseReportItem;

public class TimetableProducer extends AbstractModelProducer<Timetable, TimetableType> {

	@Override
	public Timetable produce(String sourceFile,TimetableType xmlTimetable,ReportItem importReport, PhaseReportItem validationReport,SharedImportedData sharedData, UnsharedImportedData unshareableData) 
	{
		Timetable timetable= new Timetable();

		// objectId, objectVersion, creatorId, creationTime
		populateFromCastorNeptune(timetable, xmlTimetable, importReport);

		timetable.setComment(getNonEmptyTrimedString(xmlTimetable.getComment()));

		timetable.setVersion(getNonEmptyTrimedString(xmlTimetable.getVersion()));

		// DayType optional
		if (xmlTimetable.getDayType() != null)
		{
			for(DayTypeType xmlDayType : xmlTimetable.getDayType())
				try
			{
					timetable.addDayType(DayTypeEnum.valueOf(xmlDayType.value()));
			}
			catch (IllegalArgumentException e) 
			{
				// TODO: traiter le cas de non correspondance
			}
		}

		// 
		if(xmlTimetable.getCalendarDay() != null){
			for(XMLGregorianCalendar calendarDay : xmlTimetable.getCalendarDay()){
				timetable.addCalendarDay(getSqlDate(calendarDay));
			}
		}

		if(xmlTimetable.getPeriod() != null){
			for(PeriodType xmlPeriod : xmlTimetable.getPeriod()){
				timetable.addPeriod(new Period(getSqlDate(xmlPeriod.getStartOfPeriod()),getSqlDate(xmlPeriod.getEndOfPeriod())));
			}
		}


		List<String> vehicleJourneys = new ArrayList<String>(xmlTimetable.getVehicleJourneyId());
		xmlTimetable.getVehicleJourneyId().clear();

		Timetable  sharedBean = getOrAddSharedData(sharedData, timetable, sourceFile, xmlTimetable,validationReport);
		if (sharedBean != null) timetable = sharedBean;
		xmlTimetable.getVehicleJourneyId().addAll(vehicleJourneys);


		for(String vehicleJourneyId : vehicleJourneys){
			timetable.addVehicleJourneyId(getNonEmptyTrimedString(vehicleJourneyId));
		}


		return timetable;
	}

}
