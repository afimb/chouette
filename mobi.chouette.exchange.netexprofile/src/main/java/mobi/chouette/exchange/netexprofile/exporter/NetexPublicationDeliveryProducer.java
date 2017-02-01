package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.exporter.producer.AbstractJaxbNetexProducer;
import mobi.chouette.exchange.netexprofile.jaxb.JaxbNetexFileConverter;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import org.rutebanken.netex.model.PublicationDeliveryStructure;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;

public class NetexPublicationDeliveryProducer implements Constant {

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

        // TODO find out if it is mandatory to set projection on stop areas
        /*
        for (StopArea stopArea : collection.getStopAreas()) {
            stopArea.toProjection(projectionType);
        }
        */

        Metadata metadata = (Metadata) context.get(METADATA);

        PublicationDeliveryStructure rootObject = AbstractJaxbNetexProducer.netexFactory.createPublicationDeliveryStructure()
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef("NSR")
                .withDescription(AbstractJaxbNetexProducer.netexFactory.createMultilingualString().withValue(collection.getLine().getName()));

        Path dir = Paths.get(rootDirectory, OUTPUT);
        String fileName = collection.getLine().getObjectId().replaceAll(":", "-") + ".xml";
        File file = new File(dir.toFile(), fileName);

        JaxbNetexFileConverter writer = JaxbNetexFileConverter.getInstance();
        writer.write(AbstractJaxbNetexProducer.netexFactory.createPublicationDelivery(rootObject), file);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
                    NeptuneObjectPresenter.getName(collection.getLine())));
        }
    }

}
