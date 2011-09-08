package fr.certu.chouette.tool;

import java.sql.Date;

import fr.certu.chouette.plugin.report.Report;


public interface ICleanTool 
{
     Report purgeAllItems(Date boundaryDate, boolean before);
}
