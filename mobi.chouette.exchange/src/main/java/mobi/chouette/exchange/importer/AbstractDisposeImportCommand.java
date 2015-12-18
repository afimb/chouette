package mobi.chouette.exchange.importer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.exchange.validation.ValidationData;
import mobi.chouette.model.util.Referential;

import org.apache.commons.io.FileUtils;

@Log4j
public abstract class AbstractDisposeImportCommand implements Command, Constant {

	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = ERROR;

		try {
			ValidationData validationData = (ValidationData) context.get(VALIDATION_DATA);
			if (validationData != null)
				validationData.dispose();
			Referential cache = (Referential) context.get(CACHE);
			if (cache != null)
				cache.clear(false);
			Referential referential = (Referential) context.get(REFERENTIAL);
			if (referential != null)
				referential.dispose();
			
			result = SUCCESS;
			JobData jobData = (JobData) context.get(JOB_DATA);
			String path = jobData.getPathName();
			Path target = Paths.get(path, INPUT);
			if (Files.exists(target)) {
				try {
					FileUtils.deleteDirectory(target.toFile());
				} catch (Exception e) {
					log.warn("caonnot purge input directory " + e.getMessage());
				}
			}

		} catch (Exception e) {
			log.error(e, e);
			throw e;
		}

		return result;
	}

}
