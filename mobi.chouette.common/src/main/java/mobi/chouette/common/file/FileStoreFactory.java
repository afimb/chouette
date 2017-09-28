package mobi.chouette.common.file;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.PropertyNames;

@Log4j
public class FileStoreFactory {

	private static FileStore FILE_STORE;

	private static final Object LOCK = new Object();

	public static final FileStore getFileStore() {
		if (FILE_STORE == null) {

			synchronized (LOCK) {
				if (FILE_STORE == null) {
					ContenerChecker contenerChecker = null;
					try {
						contenerChecker = CDI.current().select(ContenerChecker.class).get();
					} catch (Exception e) {
						log.warn("Failed to access CDI, using default fileStore impl");
					}

					if (contenerChecker != null) {
						String implBeanName = System.getProperty(contenerChecker.getContext() + PropertyNames.FILE_STORE_IMPLEMENTATION);
						if (implBeanName != null) {
							Set<Bean<?>> beans = CDI.current().getBeanManager().getBeans(implBeanName);

							if (beans.size() > 0) {
								Bean<FileStore> bean = (Bean<FileStore>) beans.iterator().next();
								CreationalContext<FileStore> ctx = CDI.current().getBeanManager().createCreationalContext(bean);
								FILE_STORE = (FileStore)
										CDI.current().getBeanManager().getReference(bean, FileStore.class, ctx);
							} else {
								throw new IllegalArgumentException("FileStore implementation with bean name: " + implBeanName + " not found");
							}
						} else {
							log.warn("No FileStore implementation defined, using LocalFileStore as default");
						}

					}

					if (FILE_STORE == null) {
						FILE_STORE = new LocalFileStore();
					}
				}
			}

		}
		return FILE_STORE;
	}
}
