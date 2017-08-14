package mobi.chouette.exchange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.parameters.ValidationParameters;

import org.apache.commons.io.FilenameUtils;

@Log4j
public abstract class AbstractInputValidator implements InputValidator, Constant {
	public boolean initReport(JobData data) {
		Context context = new Context();
		context.put(REPORT, new ActionReport());
		context.put(JOB_DATA, data);
		ProgressionCommand progression = new ProgressionCommand();
		progression.initialize(context, 1);
		return true;
	}

	/* (non-Javadoc)
	 * @see mobi.chouette.exchange.InputValidator#toValidation(java.lang.String)
	 */
	@Override
	public ValidationParameters toValidation(String validationParameters) {
		try {
			return JSONUtil.fromJSON(validationParameters,
					ValidationParameters.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	protected boolean checkFileExistenceInZip(String fileName, Path filePath, String format) {
		boolean isZipFileValid = true;
		String fileExtension = FilenameUtils.getExtension(fileName);
		if (fileExtension.equalsIgnoreCase("zip")) {
			isZipFileValid = false;
			ZipFile zipFile = null;
			File file = null;
			try {
				file = new File(filePath.toString());
				zipFile = new ZipFile(file);
				for (Enumeration<? extends ZipEntry> e = zipFile.entries();
						e.hasMoreElements();) {
					ZipEntry ze = e.nextElement();
					String name = ze.getName();
					if (name.endsWith("." + format) && !name.contains("metadata")) {
						isZipFileValid = true;
						break;
					}
				}
			}catch (IOException e) {
				log.error("Erreur ouverture fichier zip " + fileName);
			} 
		}
		
		return isZipFileValid;
	}
}
