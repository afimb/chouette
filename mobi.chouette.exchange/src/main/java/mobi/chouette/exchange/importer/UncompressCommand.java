package mobi.chouette.exchange.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Color;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.common.JobData;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.common.file.FileStore;
import mobi.chouette.common.file.FileStoreFactory;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.exchange.report.ReportConstant;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.apache.commons.io.FilenameUtils;

/**
 * execute use in context :
 * <ul>
 * <li>REPORT</li>
 * <li>JOB_DATA</li>
 * </ul>
 *
 * @author michel
 */
@Log4j
public class UncompressCommand implements Command, ReportConstant {

	public static final String COMMAND = "UncompressCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReporter reporter = ActionReporter.Factory.getInstance();
		JobData jobData = (JobData) context.get(JOB_DATA);

		String path = jobData.getPathName();
		String file = jobData.getInputFilename();
		if (file == null) {
			reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS, "Missing input file");
			return result;
		}
		Path filename = Paths.get(path, file);
		Path target = Paths.get(path, INPUT);
		if (!Files.exists(target)) {
			Files.createDirectories(target);
		}

		FileStore fileStore = FileStoreFactory.getFileStore();
		if (FilenameUtils.getExtension(filename.toString()).equalsIgnoreCase("zip")) {
			reporter.addZipReport(context, file, IO_TYPE.INPUT);
			File tmpZip = null;
			try {
				tmpZip = File.createTempFile("archive", ".zip");
				org.apache.commons.io.FileUtils.copyInputStreamToFile(fileStore.getFileContent(filename), tmpZip);
				FileUtil.uncompress(tmpZip.getAbsolutePath(), target.toString());
				result = SUCCESS;
			} catch (Exception e) {
				log.error(e.getMessage());
				reporter.addZipErrorInReport(context, file, ActionReporter.FILE_ERROR_CODE.READ_ERROR, e.getMessage());
				reporter.setActionError(context, ActionReporter.ERROR_CODE.INVALID_PARAMETERS, "invalid_zip");
			} finally {
				if (tmpZip != null) {
					tmpZip.delete();
				}
			}
		} else {
			Path targetFile = Paths.get(target.toString(), filename.toString());
			org.apache.commons.io.FileUtils.copyInputStreamToFile(fileStore.getFileContent(filename), targetFile.toFile());
			result = SUCCESS;
		}

		log.info(Color.MAGENTA + monitor.stop() + Color.NORMAL);
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		@Override
		protected Command create(InitialContext context) throws IOException {
			Command result = new UncompressCommand();
			return result;
		}
	}

	static {
		CommandFactory.factories
				.put(UncompressCommand.class.getName(), new DefaultCommandFactory());
	}
}
