package mobi.chouette.exchange.gtfs.exporter.producer;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.TransportModeConverterFactory;
import mobi.chouette.exchange.gtfs.GtfsChouetteIdGenerator;
import mobi.chouette.exchange.gtfs.JobDataTest;
import mobi.chouette.exchange.gtfs.exporter.GtfsExportParameters;
import mobi.chouette.exchange.report.ActionReport;
import mobi.chouette.exchange.validation.report.ValidationReport;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.apache.log4j.BasicConfigurator;

import org.apache.commons.io.FileUtils;

public abstract class AbstractProducerTest implements Constant{
	protected static InitialContext initialContext;

	protected void init() {
		Locale.setDefault(Locale.ENGLISH);
		if (initialContext == null) {
			try {
				initialContext = new InitialContext();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			BasicConfigurator.resetConfiguration();
			BasicConfigurator.configure();
		}
	}
	protected Context initExportContext() throws ClassNotFoundException, IOException {
		init();
		ContextHolder.setContext("chouette_gui"); // set tenant schema

		Context context = new Context();
		context.put(INITIAL_CONTEXT, initialContext);
		context.put(REPORT, new ActionReport());
		context.put(VALIDATION_REPORT, new ValidationReport());
		GtfsExportParameters configuration = new GtfsExportParameters();
		context.put(CONFIGURATION, configuration);
		GtfsChouetteIdGenerator chouetteIdGenerator = new GtfsChouetteIdGenerator();
		context.put(CHOUETTEID_GENERATOR, chouetteIdGenerator);
		configuration.setName("name");
		configuration.setUserName("userName");
		configuration.setOrganisationName("organisation");
		configuration.setReferentialName("test");
		configuration.setDefaultFormat("neptune");
		context.put(TRANSPORT_MODE_CONVERTER, TransportModeConverterFactory.create(configuration.getDefaultFormat()));
		configuration.setValidateAfterExport(true);
		JobDataTest test = new JobDataTest();
		context.put(JOB_DATA, test);
		test.setPathName("target/referential/test");
		test.setOutputFilename("gtfs.zip");
		File f = new File("target/referential/test");
		if (f.exists())
			try {
				FileUtils.deleteDirectory(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		f.mkdirs();
		test.setReferential("chouette_gui");
		test.setAction(EXPORTER);
		test.setType("gtfs");
		context.put("testng", "true");
		context.put(OPTIMIZED, Boolean.FALSE);
		return context;

	}
}
