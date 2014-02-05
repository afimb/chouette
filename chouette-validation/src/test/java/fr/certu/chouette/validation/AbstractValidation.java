package fr.certu.chouette.validation;

import java.sql.Time;
import java.util.List;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Reporter;

import fr.certu.chouette.model.neptune.NeptuneLocalizedObject;
import fr.certu.chouette.plugin.report.Report;
import fr.certu.chouette.plugin.report.ReportItem;
import fr.certu.chouette.plugin.validation.report.CheckPointReportItem;
import fr.certu.chouette.plugin.validation.report.DetailReportItem;

public class AbstractValidation extends AbstractTestNGSpringContextTests
{
	public static void printReport(Report report)
	{
		if (report == null)
		{
			Reporter.log("no report");
		}
		else
		{
			//Reporter.log(report.toJSON());

			Reporter.log(report.getStatus().name()+" : "+report.getLocalizedMessage());
			printItems("---",report.getItems());
		}
	}

	/**
	 * @param indent
	 * @param items
	 */
	private static void printItems(String indent,List<ReportItem> items) 
	{
		if (items == null) return;
		for (ReportItem item : items) 
		{
			if (item instanceof CheckPointReportItem)
			{
				CheckPointReportItem citem = (CheckPointReportItem) item;
				Reporter.log(indent+citem.getStatus().name()+" : "+citem.getMessageKey());

			}
			else if (item instanceof DetailReportItem)
			{
				DetailReportItem ditem = (DetailReportItem) item;
				Reporter.log(indent+ditem.toJSON());
			}
			else
			{
				Reporter.log(indent+item.getStatus().name()+" : "+item.getLocalizedMessage());
			}
			printItems(indent+"---",item.getItems());
		}

	}
	
	/**
	 * calculate distance on spheroid
	 * 
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static double distance(NeptuneLocalizedObject obj1,NeptuneLocalizedObject obj2)
	{
		double long1rad = Math.toRadians(obj1.getLongitude().doubleValue());
		double lat1rad = Math.toRadians(obj1.getLatitude().doubleValue());
		double long2rad = Math.toRadians(obj2.getLongitude().doubleValue());
		double lat2rad = Math.toRadians(obj2.getLatitude().doubleValue());

		double alpha = Math.cos(lat1rad)*Math.cos(lat2rad)*Math.cos(long2rad-long1rad) + Math.sin(lat1rad)*Math.sin(lat2rad);

		double distance = 6378. * Math.acos(alpha);

		return distance * 1000.;
	}

	public static long diffTime(Time first, Time last) 
	{
		if (first == null || last == null) return Long.MIN_VALUE; // TODO
		long diff = last.getTime()/1000L - first.getTime() / 1000L;
		if (diff < 0) diff += 86400L; // step upon midnight : add one day in seconds  
		return diff;
	}


}
