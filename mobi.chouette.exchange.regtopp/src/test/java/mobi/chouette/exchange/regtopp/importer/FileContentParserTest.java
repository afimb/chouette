package mobi.chouette.exchange.regtopp.importer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.testng.Assert;
import org.testng.annotations.Test;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.FileUtil;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCode;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeader;
import mobi.chouette.exchange.regtopp.model.RegtoppDestination;
import mobi.chouette.exchange.regtopp.model.RegtoppInterchange;
import mobi.chouette.exchange.regtopp.model.RegtoppLine;
import mobi.chouette.exchange.regtopp.model.RegtoppPathway;
import mobi.chouette.exchange.regtopp.model.RegtoppRemark;
import mobi.chouette.exchange.regtopp.model.RegtoppStop;
import mobi.chouette.exchange.regtopp.model.RegtoppStopTime;
import mobi.chouette.exchange.regtopp.model.RegtoppTrip;
import mobi.chouette.exchange.regtopp.model.RegtoppZone;
import mobi.chouette.exchange.regtopp.model.importer.FileContentParser;
import mobi.chouette.exchange.regtopp.model.importer.ParseableFile;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.report.FileInfo;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;

@Log4j
public class FileContentParserTest {
	@Test
	public void testAtBMappings() throws Exception {
		File regtoppArchive = new File("src/test/data/atb-20160118-20160619.zip");
		File dest = new File("target/"+System.currentTimeMillis());
		dest.mkdirs();
		dest.deleteOnExit();
		FileUtil.uncompress(regtoppArchive.getAbsolutePath(), dest.getAbsolutePath());

		ActionReport report = new ActionReport();

		
		File[] regtoppFiles = dest.listFiles();
		for(File f : regtoppFiles) {

			Context context = new Context();
			ValidationReport validationReport = new ValidationReport();
			CheckPoint checkPoint = new CheckPoint("2-GTFS-Stop-3", RESULT.OK, SEVERITY.ERROR);
			validationReport.getCheckPoints().add(checkPoint );
			context.put(Constant.MAIN_VALIDATION_REPORT, validationReport);
			context.put(Constant.REPORT, report);
			
			
			ValidationReporter reporter = new ValidationReporter();
			FileContentParser parser = null;
			String name = f.getName().toUpperCase();
			String extension = name.substring(name.lastIndexOf(".")+1);
			List<Class> regtoppClasses = new ArrayList<Class>();
			switch(extension) {
			case "FRM":
			case "PER":
			case "RUT":
			case "TAB":
			case "TMS":
				log.warn("Ignoring unknown file "+extension);
				continue;
			case "HPL":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppStop.class);
				break;
			case "TIX":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppTrip.class);
				break;
			case "TDA":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppStopTime.class);
				break;
			case "DKO":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppDayCodeHeader.class);
				regtoppClasses.add(RegtoppDayCode.class);
				break;
			case "DST":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppDestination.class);
				break;
			case "MRK":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppRemark.class);
				break;
			case "GAV":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppPathway.class);
				break;
			case "SAM":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppInterchange.class);
				break;
			case "SON":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppZone.class);
				break;
			case "LIN":
				parser = new FileContentParser();
				regtoppClasses.add(RegtoppLine.class);
				break;
			}
			
			
			Assert.assertNotNull(parser,"No parser registered for "+name);
			Assert.assertTrue(regtoppClasses.size() > 0,"No class registered for "+name);
			
			FileInfo fileInfo = new FileInfo(name,FILE_STATE.ERROR);
			report.getFiles().add(fileInfo);
			ParseableFile parseableFile = new ParseableFile(f, regtoppClasses, fileInfo);
			
			parser.parse(context, parseableFile, reporter );
			
			Assert.assertEquals(0,reporter.getExceptions().size(),"Validation exceptions: "+ToStringBuilder.reflectionToString(reporter));
			
			
		}
		
		for(FileInfo fileInfo : report.getFiles()) {
			Assert.assertEquals(FILE_STATE.OK,fileInfo.getStatus(),"Error parsing file");
		}
		
	}
}
