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
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDayCodeHeaderDKO;
import mobi.chouette.exchange.regtopp.model.RegtoppDestinationDST;
import mobi.chouette.exchange.regtopp.model.RegtoppInterchangeSAM;
import mobi.chouette.exchange.regtopp.model.RegtoppLineLIN;
import mobi.chouette.exchange.regtopp.model.RegtoppPathwayGAV;
import mobi.chouette.exchange.regtopp.model.RegtoppPeriodPER;
import mobi.chouette.exchange.regtopp.model.RegtoppRemarkMRK;
import mobi.chouette.exchange.regtopp.model.RegtoppRoutePointRUT;
import mobi.chouette.exchange.regtopp.model.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.RegtoppTableVersionTAB;
import mobi.chouette.exchange.regtopp.model.RegtoppTripIndexTIX;
import mobi.chouette.exchange.regtopp.model.RegtoppTripPatternTMS;
import mobi.chouette.exchange.regtopp.model.RegtoppVehicleJourneyVLP;
import mobi.chouette.exchange.regtopp.model.RegtoppZoneSON;
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
			FileContentParser parser = new FileContentParser();
			String name = f.getName().toUpperCase();
			String extension = name.substring(name.lastIndexOf(".")+1);
			List<Class> regtoppClasses = new ArrayList<Class>();
			switch(extension) {
			
			case "TIX":
				regtoppClasses.add(RegtoppTripIndexTIX.class);
				break;
			case "TMS":
				regtoppClasses.add(RegtoppTripPatternTMS.class);
				break;
			case "HPL":
				regtoppClasses.add(RegtoppStopHPL.class);
				break;
			case "DKO":
				regtoppClasses.add(RegtoppDayCodeHeaderDKO.class);
				regtoppClasses.add(RegtoppDayCodeDKO.class);
				break;
			case "DST":
				regtoppClasses.add(RegtoppDestinationDST.class);
				break;
			case "MRK":
				regtoppClasses.add(RegtoppRemarkMRK.class);
				break;
			case "GAV":
				regtoppClasses.add(RegtoppPathwayGAV.class);
				break;
			case "SAM":
				regtoppClasses.add(RegtoppInterchangeSAM.class);
				break;
			case "SON":
				regtoppClasses.add(RegtoppZoneSON.class);
				break;
			case "LIN":
				regtoppClasses.add(RegtoppLineLIN.class);
				break;
			case "VLP":
				regtoppClasses.add(RegtoppVehicleJourneyVLP.class);
				break;
			case "TAB":
				regtoppClasses.add(RegtoppTableVersionTAB.class);
				break;
			case "PER":
				regtoppClasses.add(RegtoppPeriodPER.class);
				break;
			case "RUT":
				regtoppClasses.add(RegtoppRoutePointRUT.class);
				break;
			case "FRM":
				log.info("Ignoring version file "+extension);
				continue;
			default:
				log.warn("Ignoring unknown file "+extension);
				continue;

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
