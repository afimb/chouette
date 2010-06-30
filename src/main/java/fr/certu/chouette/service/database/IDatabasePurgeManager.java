package fr.certu.chouette.service.database;

import java.util.Date;
import java.util.HashMap;

import fr.certu.chouette.dao.jdbc.PurgeReport;

public interface IDatabasePurgeManager {
	HashMap<String, String> purgeDatabase(Date boundaryDate, boolean before);
}
