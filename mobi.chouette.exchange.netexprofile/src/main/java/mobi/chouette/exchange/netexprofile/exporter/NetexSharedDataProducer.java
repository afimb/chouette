package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.netexprofile.exporter.producer.StopPlaceProducer;
import mobi.chouette.exchange.netexprofile.exporter.writer.AbstractNetexWriter.Mode;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.util.Referential;
import org.rutebanken.netex.model.StopPlace;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static mobi.chouette.exchange.netexprofile.Constant.EXPORTABLE_NETEX_DATA;

public class NetexSharedDataProducer extends NetexProducer implements Constant {

    private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);
        Referential referential = (Referential) context.get(REFERENTIAL);

        for (StopArea stopArea : referential.getSharedStopAreas().values()) {
            StopPlace stopPlace = stopPlaceProducer.produce(context, stopArea);
            exportableNetexData.getStopPlaces().add(stopPlace);
        }

        String fileName = "_SharedData.xml";
        Path filePath = new File(outputPath.toFile(), fileName).toPath();

        NetexFileWriter writer = new NetexFileWriter();
        writer.writeXmlFile(context, filePath, exportableData, exportableNetexData, Mode.shared);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);
    }
}
