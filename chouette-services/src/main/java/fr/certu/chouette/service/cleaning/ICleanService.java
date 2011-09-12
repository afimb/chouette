package fr.certu.chouette.service.cleaning;

import java.sql.Date;

import fr.certu.chouette.plugin.report.Report;


public interface ICleanService 
{
     Report purgeAllItems(Date boundaryDate, boolean before);
}
