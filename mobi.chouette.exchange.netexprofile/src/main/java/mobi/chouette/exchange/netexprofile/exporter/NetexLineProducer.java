package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NetexLineProducer implements Constant {

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        String rootDirectory = jobData.getPathName();

        NetexprofileExportParameters parameters = (NetexprofileExportParameters) context.get(CONFIGURATION);
        boolean addExtension = parameters.isAddExtension();

        String projectionType = parameters.getProjectionType();
        if (projectionType != null && !projectionType.isEmpty()) {
            if (!projectionType.toUpperCase().startsWith("EPSG:")) {
                projectionType = "EPSG:" + projectionType;
            }
        }

        Metadata metadata = (Metadata) context.get(METADATA);

        /*
        for (StopArea stopArea : collection.getStopAreas()) {
            stopArea.toProjection(projectionType);
        }
        */

        JaxbNetexFileConverter writer = JaxbNetexFileConverter.getInstance();
        Path dir = Paths.get(rootDirectory, OUTPUT);
        String fileName = collection.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        File file = new File(dir.toFile(), fileName);
        //writer.write(AbstractJaxbNeptuneProducer.tridentFactory.createChouettePTNetwork(rootObject), file );

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(collection.getLine())));
        }
    }
}
