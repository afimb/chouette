package mobi.chouette.service;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.DbStatusChecker;

@Singleton(name = HealthService.BEAN_NAME)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Log4j
public class HealthService {

	public static final String BEAN_NAME = "HealthService";

	@EJB
	private DbStatusChecker dbStatusChecker;

	public boolean isReady() {
		return dbStatusChecker.isDbUp();
	}

}
