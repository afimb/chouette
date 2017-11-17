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

	// Check memory for every Xth liveness check
	private static final int CHECK_MEMORY_FREQUENCY = 10;

	// Require 500 mb to allocated to verify application has enough memory
	private static final int CHECK_MEMORY_BYTES_SIZE = 500 * 1000 * 1024;

	private int checkMemoryCnt = 0;

	@EJB
	private DbStatusChecker dbStatusChecker;

	public boolean isReady() {
		log.debug("Checking readiness...");
		return dbStatusChecker.isDbUp();
	}

	public boolean isLive() {
		if (++checkMemoryCnt >= CHECK_MEMORY_FREQUENCY) {
			checkMemoryCnt = 0;
			return checkMemory();
		}
		return true;
	}


	private boolean checkMemory() {
		boolean ok = false;
		try {
			byte[] bytes = new byte[CHECK_MEMORY_BYTES_SIZE];
			ok = true;
		} catch (Throwable t) {
			log.fatal("Failed to allocate " + CHECK_MEMORY_BYTES_SIZE + " bytes for memory check. Liveness test failed, should cause application to be shut down.");
		}
		return ok;
	}

}
