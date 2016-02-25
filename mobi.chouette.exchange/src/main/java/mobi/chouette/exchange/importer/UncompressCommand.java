package mobi.chouette.exchange.importer;

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
import mobi.chouette.exchange.report.ActionError;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileError;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.report.ReportConstant;

import org.apache.commons.io.FilenameUtils;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * execute use in context : 
 * <ul>
 * <li>REPORT</li>
 * <li>JOB_DATA</li>
 * </ul>
 * 
 * @author michel
 *
 */
@Log4j
public class UncompressCommand implements Command, ReportConstant {

	public static final String COMMAND = "UncompressCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		boolean result = ERROR;
		Monitor monitor = MonitorFactory.start(COMMAND);
		ActionReport report = (ActionReport) context.get(REPORT);
		JobData jobData = (JobData) context.get(JOB_DATA);

		String path = jobData.getPathName();
		String file = jobData.getInputFilename(); 
		if (file == null)
		{
			report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"Missing input file"));
			return result;
		}
		Path filename = Paths.get(path, file);
		Path target = Paths.get(path, INPUT);
		if (!Files.exists(target)) {
			Files.createDirectories(target);
		}
		if (FilenameUtils.getExtension(filename.toString()).equalsIgnoreCase("zip"))
		{
			FileInfo zip = new FileInfo(file,FILE_STATE.OK);

			try {
				report.setZip(zip);
				FileUtil.uncompress(filename.toString(), target.toString());
				result = SUCCESS;
			} catch (Exception e) {
				log.error(e.getMessage());
				zip.addError(new FileError(FileError.CODE.READ_ERROR,e.getMessage()));
				report.setFailure(new ActionError(ActionError.CODE.INVALID_PARAMETERS,"invalid_zip"));
			}
		}
		else
		{
			org.apache.commons.io.FileUtils.copyFileToDirectory(filename.toFile(), target.toFile());
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
