package mobi.chouette.exchange.netex.exporter.writer;

import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import mobi.chouette.exchange.netex.exporter.ExportableData;
import mobi.chouette.exchange.netex.exporter.ModelTranslator;
import mobi.chouette.model.Period;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.type.DayTypeEnum;

public class ServiceCalendarFrameWriter extends AbstractWriter{
	

	
	public static void write(Writer writer, ExportableData data ) throws IOException 
	{
		SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		ModelTranslator modelTranslator = new ModelTranslator();
		Set<Timetable> timetables = data.getTimetables();

		writer.write("<!--   -->\n");
		writer.write("<!-- Calendars : NeTEx Day Types and Day Type Assignment mapped to NEPTUNE timetable -->\n");
		writer.write("<!-- Each NEPTUNE timetable is mapped to a separate ServiceCalendarFrame  -->\n");
		// #set($counter = 0)
		int counter = 0;
		// #foreach( $timetable in $timetables )
		for (Timetable timetable : timetables) {
			timetable.computeLimitOfPeriods();
		writer.write("<ServiceCalendarFrame version=\"any\" id=\""+timetable.objectIdPrefix()+":ServiceCalendarFrame:SFC"+counter+"\">\n");
		writer.write("  <ServiceCalendar version=\"any\" id=\""+timetable.objectIdPrefix()+":ServiceCalendar:SFC"+counter+"\">\n");
		writer.write("    <FromDate>"+shortDateFormat.format(timetable.getStartOfPeriod())+"</FromDate>\n");
		writer.write("    <ToDate>"+shortDateFormat.format(timetable.getEndOfPeriod())+"</ToDate>\n");
		writer.write("  </ServiceCalendar>\n");
		writer.write("  <!--- === Day Types ==== -->\n");
		writer.write("  <dayTypes>\n");
		writer.write("    <DayType version=\""+timetable.getObjectVersion()+"\" id=\""+modelTranslator.netexId(timetable)+"\">\n");
		//      #if ( $timetable.version )
		if (isSet(timetable.getVersion()))
		writer.write("      <Name>"+toXml(timetable.getVersion())+"</Name>\n");
		//      #end
		//      #if ( $timetable.comment )
		if (isSet(timetable.getComment()))
		writer.write("      <ShortName>"+toXml(timetable.getComment())+"</ShortName>\n");
		//      #end
		//      #if ( $timetable.dayTypes && $timetable.dayTypes.size() > 0 )
		if (nonEmpty(timetable.getDayTypes())) {
		writer.write("      <properties>\n");
		//        #foreach( $dayType in $timetable.getDayTypes() )
		for (DayTypeEnum dayType : timetable.getDayTypes()) {
		//            #if($dayType && $!modelTranslator.toDayTypeNetex($!dayType))      
		writer.write("        <PropertyOfDay>\n");
		writer.write("          <DaysOfWeek>"+modelTranslator.toDayTypeNetex(dayType)+"</DaysOfWeek>\n");
		writer.write("        </PropertyOfDay>\n");
		}
		//            #end
		//        #end
		writer.write("      </properties>\n");
		}
		//      #end
		writer.write("    </DayType>\n");
		writer.write("  </dayTypes>\n");
		writer.write("  <!--- === Day assignments ==== -->\n");
		//  #if ( $timetable.peculiarDates.size() > 0 )
		List<Date> peculiarDates = timetable.getEffectiveDates(); 
		if (nonEmpty(peculiarDates)) {
		writer.write("  <operatingDays>\n");
		//    #foreach( $day in $timetable.peculiarDates )
		for (Date day : peculiarDates) {
		writer.write("    <OperatingDay version=\"any\" id=\""+modelTranslator.netexMockId(timetable,"OperatingDay")+"D"+shortDateFormat.format(day)+"\">\n");
		writer.write("      <CalendarDate>"+shortDateFormat.format(day)+"</CalendarDate>\n");
		writer.write("    </OperatingDay>\n");
		}
		//    #end		
		writer.write("  </operatingDays> \n"); 
		}
		//  #end		
		writer.write("  <!--- === Period assignments ==== -->\n");
		//  #if ( $timetable.effectivePeriods.size() > 0 )
		List<Period> effectivePeriods = timetable.getEffectivePeriods();
		if (nonEmpty(effectivePeriods)) {
		writer.write("  <operatingPeriods>\n");
		//    #foreach( $period in $timetable.effectivePeriods )
		for (Period period : effectivePeriods) {
		writer.write("    <OperatingPeriod version=\"any\" id=\""+modelTranslator.netexMockId(timetable,"OperatingPeriod")+"S"+shortDateFormat.format(period.getStartDate())+"E"+shortDateFormat.format(period.getEndDate())+"\">\n");
		writer.write("      <FromDate>"+shortDateFormat.format(period.getStartDate())+"</FromDate>\n");
		writer.write("      <ToDate>"+shortDateFormat.format(period.getEndDate())+"</ToDate>\n");
		writer.write("    </OperatingPeriod>\n");
		}
		//    #end		
		writer.write("  </operatingPeriods>  \n");
		}
		//  #end		
		writer.write("  <!--- === Day Type assignments ==== -->\n");
		writer.write("  <dayTypeAssignments>\n");
		//    #foreach( $period in $timetable.effectivePeriods )
		for (Period period : effectivePeriods) {
		writer.write("    <DayTypeAssignment version=\"any\">\n");
		writer.write("      <OperatingPeriodRef ref=\""+modelTranslator.netexMockId(timetable,"OperatingPeriod")+"S"+shortDateFormat.format(period.getStartDate())+"E"+shortDateFormat.format(period.getEndDate())+"\"/>\n");
		writer.write("      <DayTypeRef version=\""+timetable.getObjectVersion()+"\" ref=\""+modelTranslator.netexId(timetable)+"\"/>\n");
		writer.write("   </DayTypeAssignment>\n");
		}
		//    #end		
		//    #foreach( $day in $timetable.peculiarDates )
		for (Date day : peculiarDates) {
		writer.write("    <DayTypeAssignment version=\"any\">\n");
		writer.write("      <OperatingDayRef ref=\""+modelTranslator.netexMockId(timetable,"OperatingDay")+"D"+shortDateFormat.format(day)+"\"/>\n");
		writer.write("      <DayTypeRef version=\""+timetable.getObjectVersion()+"\" ref=\""+modelTranslator.netexId(timetable)+"\"/>\n");
		writer.write("    </DayTypeAssignment>\n");
		}
		//    #end
		writer.write("  </dayTypeAssignments>  \n");
		writer.write("</ServiceCalendarFrame>\n");
		//#set($counter = $counter + 1)
		counter++;
		}
		//#end

	}

}
