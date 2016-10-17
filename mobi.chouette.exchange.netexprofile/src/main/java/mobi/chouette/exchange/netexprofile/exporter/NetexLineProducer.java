package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.StopArea;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NetexLineProducer implements Constant {

	public void produce(Context context) throws Exception {

		ExportableData collection = (ExportableData) context.get(EXPORTABLE_DATA);
		JobData jobData = (JobData) context.get(JOB_DATA);
		String rootDirectory = jobData.getPathName();

		NetexExportParameters parameters = (NetexExportParameters) context.get(CONFIGURATION);
		String projectionType = parameters.getProjectionType();
		if (projectionType != null && !projectionType.isEmpty()) {
			if (!projectionType.toUpperCase().startsWith("EPSG:"))
				projectionType = "EPSG:" + projectionType;
		}
		for (StopArea stopArea : collection.getStopAreas()) {
			stopArea.toProjection(projectionType);
		}
		Metadata metadata = (Metadata) context.get(METADATA);

		Path dir = Paths.get(rootDirectory, OUTPUT);
		String fileName = collection.getLine().getId() + ".xml";
		File file = new File(dir.toFile(), fileName);

		NetexFileWriter writer = new NetexFileWriter();
		writer.writeXmlFile(collection, file);

		ActionReporter actionReporter = ActionReporter.Factory.getInstance();
		actionReporter.setFileState(context, fileName, IO_TYPE.OUTPUT, ActionReporter.FILE_STATE.OK);

		if (metadata != null) {
			metadata.getResources().add(
					metadata.new Resource(fileName, NeptuneObjectPresenter.getName(collection.getLine().getNetwork()),
							NeptuneObjectPresenter.getName(collection.getLine())));
		}

	}

}
