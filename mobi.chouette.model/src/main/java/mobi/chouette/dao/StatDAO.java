package mobi.chouette.dao;

import java.sql.Date;
import java.util.List;

import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.Stat;

public interface StatDAO extends GenericDAO<Stat>{
	void addStatToDatabase(Date date, String action, String type);
	List<Stat> getCurrentYearStats();
	void removeObsoleteStatFromDatabase(Date date);
}
