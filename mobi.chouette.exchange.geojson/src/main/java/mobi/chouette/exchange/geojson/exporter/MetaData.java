package mobi.chouette.exchange.geojson.exporter;

import java.io.File;
import java.io.IOException;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.metadata.Metadata;
import mobi.chouette.exchange.metadata.NeptuneObjectPresenter;
import mobi.chouette.model.Line;

public class MetaData implements Constant {

	public static void addTableOfContentsEntry(Context context, File file,
			Line line) throws IOException {
		Metadata metadata = (Metadata) context.get(METADATA);
		if (metadata != null) {
			metadata.getResources().add(
					metadata.new Resource(file.getName(),
							NeptuneObjectPresenter.getName(line.getNetwork()),
							NeptuneObjectPresenter.getName(line)));
		}
	}

	public static void updateBoundingBox(Context context, double longitude,
			double latitude) throws IOException {
		Metadata metadata = (Metadata) context.get(METADATA);
		if (metadata != null) {
			metadata.getSpatialCoverage().update(longitude, latitude);
		}
	}

}
