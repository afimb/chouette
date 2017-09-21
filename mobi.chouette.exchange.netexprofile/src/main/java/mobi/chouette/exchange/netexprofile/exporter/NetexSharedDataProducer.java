package mobi.chouette.exchange.netexprofile.exporter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.Marshaller;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

public class NetexSharedDataProducer extends NetexProducer implements Constant {

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);

        String filename = ExportedFilenamer.createSharedDataFilename(context);
        reporter.addFileReport(context, filename, IO_TYPE.OUTPUT);
		Path filePath = new File(outputPath.toFile(), filename).toPath();

        Marshaller marshaller = (Marshaller) context.get(MARSHALLER);
        NetexFileWriter writer = new NetexFileWriter();
        writer.writeXmlFile(context, filePath, exportableData, exportableNetexData, NetexFragmentMode.SHARED,marshaller);

    }

}
