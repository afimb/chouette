package fr.certu.chouette.service.database.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import fr.certu.chouette.dao.jdbc.Purge;
import fr.certu.chouette.dao.jdbc.PurgeReport;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IDatabasePurgeManager;

public class DatabasePurgeManager implements IDatabasePurgeManager {
	public static final String PURGE_LOG_DIR = "purge/";
	static{
		new File(PURGE_LOG_DIR).mkdirs();
	}
	
	private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss");
	private static final Logger logger = Logger.getLogger(DatabasePurgeManager.class);
	private Purge purge;

	@Override
	public HashMap<String, String> purgeDatabase(Date boundaryDate, boolean before) {
		PurgeReport report = null;
		try {
			report = purge.purgeAllItems(boundaryDate, before);
		} catch (SQLException e) {
			report = new PurgeReport(boundaryDate, before);
			report.addError(e.getMessage());
			logger.error(e.getMessage(),e);
			throw new ServiceException(CodeIncident.ERREUR_SQL, e);
		} finally {
			try{
				String filename = PURGE_LOG_DIR+"purge_"+dateTimeFormat.format(report.getPurgeDate())+".log";
				File reportFile = new File(filename);
				BufferedWriter bw = new BufferedWriter(new FileWriter(reportFile));
				bw.write(report.toString());
				bw.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
				throw new ServiceException(CodeIncident.ERREUR_FICHIER, e);
			}
		}
				
		return report.getSummary();
	}
	
	public void setPurge(Purge purge) {
		this.purge = purge;
	}
}
