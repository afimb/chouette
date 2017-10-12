package mobi.chouette.scheduler;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;
@Log4j
public class ReferentialLockManagerFactory {

		private static ReferentialLockManager LOCK_MANAGER;

		private static final Object LOCK = new Object();

		public static final ReferentialLockManager getLockManager() {
			if (LOCK_MANAGER == null) {

				synchronized (LOCK) {
					if (LOCK_MANAGER == null) {
						ContenerChecker contenerChecker = null;
						try {
							contenerChecker = CDI.current().select(ContenerChecker.class).get();
						} catch (Exception e) {
							log.warn("Failed to access CDI, using default fileStore impl");
						}

						if (contenerChecker != null) {
							String implBeanName = System.getProperty(contenerChecker.getContext() + PropertyNames.REFERENTIAL_LOCK_MANAGER_IMPLEMENTATION);
							if (implBeanName != null) {
								Set<Bean<?>> beans = CDI.current().getBeanManager().getBeans(implBeanName);

								if (beans.size() > 0) {
									Bean<ReferentialLockManager> bean = (Bean<ReferentialLockManager>) beans.iterator().next();
									CreationalContext<ReferentialLockManager> ctx = CDI.current().getBeanManager().createCreationalContext(bean);
									LOCK_MANAGER = (ReferentialLockManager)
											CDI.current().getBeanManager().getReference(bean, ReferentialLockManager.class, ctx);
								} else {
									throw new IllegalArgumentException("Referential lock manager implementation with bean name: " + implBeanName + " not found");
								}
							} else {
								log.warn("No Referential lock manager implementation defined, using LocalReferentialLockManager as default");
							}

						}

						if (LOCK_MANAGER == null) {
							LOCK_MANAGER = new LocalReferentialLockManager();
						}
					}
				}

			}
			return LOCK_MANAGER;
		}


}
