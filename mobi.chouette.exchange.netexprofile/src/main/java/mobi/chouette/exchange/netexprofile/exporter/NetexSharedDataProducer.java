package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static mobi.chouette.exchange.netexprofile.Constant.EXPORTABLE_NETEX_DATA;

public class NetexSharedDataProducer extends NetexProducer implements Constant {

    private static final String SHARED_DATA_FILE_NAME = "_Shared-Data.xml";

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);

        Path filePath = new File(outputPath.toFile(), SHARED_DATA_FILE_NAME).toPath();

        NetexFileWriter writer = new NetexFileWriter();
        writer.writeXmlFile(filePath, exportableData, exportableNetexData, NetexFragmentMode.SHARED);

        reporter.addFileReport(context, SHARED_DATA_FILE_NAME, IO_TYPE.OUTPUT);
    }
}
