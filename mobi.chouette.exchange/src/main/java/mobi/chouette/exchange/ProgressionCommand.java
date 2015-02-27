package mobi.chouette.exchange;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InitialContext;

import org.apache.commons.io.FileUtils;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JSONUtils;
import mobi.chouette.common.chain.Command;
import mobi.chouette.common.chain.CommandFactory;
import mobi.chouette.exchange.report.Report;
import mobi.chouette.exchange.report.Progression.STEP;
import mobi.chouette.exchange.validator.report.CheckPoint;
import mobi.chouette.exchange.validator.report.Detail;
import mobi.chouette.exchange.validator.report.ValidationReport;

@Log4j
public class ProgressionCommand implements Command, Constant {

	public static final String COMMAND = "ProgressionCommand";
	
	public void initialize(Context context)
	{
		context.put(REPORT, new Report());
		context.put(MAIN_VALIDATION_REPORT, new ValidationReport());
		saveReport(context);
	}

	public void start(Context context,int stepCount)
	{
		Report report =  (Report) context.get(REPORT);
		report.getProgression().setStep(STEP.PROCESSING);
		report.getProgression().setTotal(stepCount);
		report.getProgression().setRealized(0);
		saveReport(context);

	}
	
	public void terminate(Context context)
	{
		Report report =  (Report) context.get(REPORT);
		report.getProgression().setStep(STEP.FINALISATION);
		saveReport(context);

	}

	public void dispose(Context context)
	{
		Report report =  (Report) context.get(REPORT);
		report.setProgression(null);
		saveReport(context);
		if (context.containsKey(VALIDATION_REPORT))
		{
			mergeValidationReports(context);
			saveMainValidationReport(context);
		}
	}

	private void saveReport(Context context)
	{
		Report report = (Report) context.get(REPORT);
		Path path = Paths.get(context.get(PATH).toString(), REPORT_FILE);
		String data = JSONUtils.toJSON(report).replaceAll("\\},\\{", "\n},\n{");
		try {
			FileUtils.writeStringToFile(path.toFile(), data);
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			log.error("failed to save report",e);
		}
		
	}
	
	private void saveMainValidationReport(Context context)
	{
        ValidationReport report = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
        
        Path path = Paths.get(context.get(PATH).toString(), VALIDATION_FILE);
        
        String data  = JSONUtils.toJSON(report).replaceAll("\\},\\{","\n},\n{");
      
        try {
			FileUtils.writeStringToFile(path.toFile(), data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("failed to save validation report",e);
		}
		
		
	}
	
	private void mergeValidationReports(Context context)
	{
		ValidationReport  validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
		ValidationReport mainValidationReport = (ValidationReport) context.get(MAIN_VALIDATION_REPORT);
		for (CheckPoint checkPoint : validationReport.getCheckPoints())
		{
			String name = checkPoint.getName();
			CheckPoint mainCheckPoint = mainValidationReport.findCheckPointByName(name);
			if (mainCheckPoint == null)
			{
				mainValidationReport.getCheckPoints().add(checkPoint);
			}
			else
			{
				
				if (checkPoint.getSeverity().ordinal() > mainCheckPoint.getSeverity().ordinal())
					mainCheckPoint.setSeverity(checkPoint.getSeverity());
				if (checkPoint.getState().ordinal() > mainCheckPoint.getState().ordinal())
					mainCheckPoint.setState(checkPoint.getState());
				for (Detail detail : checkPoint.getDetails()) 
				{
					mainCheckPoint.getDetails().add(detail);
				}
				mainCheckPoint.setDetailCount(mainCheckPoint.getDetailCount()+checkPoint.getDetailCount());
			}
		}

	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		boolean result = SUCCESS;

		if (context.containsKey(VALIDATION_REPORT))
		{
			mergeValidationReports(context);
			saveMainValidationReport(context);
		}
		Report report = (Report) context.get(REPORT);
		report.getProgression().setRealized(report.getProgression().getRealized()+1);
		saveReport(context);
		// reset validationReport
		context.put(VALIDATION_REPORT, new ValidationReport());
		return result;
	}

	public static class DefaultCommandFactory extends CommandFactory {

		private ProgressionCommand instance;

		@Override
		protected Command create(InitialContext context) throws IOException {
			if (instance == null) {
				instance = new ProgressionCommand();
			}
			return instance;
		}
	}

	static {
		CommandFactory.factories.put(ProgressionCommand.class.getName(),
				new DefaultCommandFactory());
	}
}
