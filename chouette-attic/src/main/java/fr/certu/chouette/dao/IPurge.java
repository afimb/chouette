package fr.certu.chouette.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import fr.certu.chouette.dao.jdbc.PurgeReport;

public interface IPurge {
	public PurgeReport purgeAllItems(Date boundaryDate, boolean before) throws SQLException;
}
