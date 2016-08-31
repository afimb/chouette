package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.CentroidGenerator;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.type.LongLatTypeEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;


public class RegtoppStopParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser {

	private CentroidGenerator centroidGenerator = new CentroidGenerator();

	@Override
	public void parse(Context context) throws Exception {
		// Parse as v1.1D
		super.parse(context);

		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);


		// 1.2Novus specific

		// Build parent stop area (commercial stop point)

		// Group boarding positions by original stopId
		Map<String, List<StopArea>> boardingPositionsByStopArea = new HashMap<String, List<StopArea>>();
		for (StopArea sa : referential.getStopAreas().values()) {
			String commercialStopAreaId = ObjectIdCreator.extractOriginalId(sa.getObjectId()).substring(0, 8);
			List<StopArea> list = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (list == null) {
				list = new ArrayList<StopArea>();
				boardingPositionsByStopArea.put(commercialStopAreaId, list);
			}
			list.add(sa);
		}

		for (String commercialStopAreaId : boardingPositionsByStopArea.keySet()) {
			List<StopArea> boardingPositions = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (boardingPositions.size() > 0) {
				// Create parent stopArea
				String objectId = ObjectIdCreator.createStopAreaId(configuration, commercialStopAreaId);
				StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
				stopArea.setName(boardingPositions.get(0).getName()); // TODO using name of first stoppoint, should be identical for all boarding positions according to spec
				stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

				for (StopArea bp : boardingPositions) {
					bp.setParent(stopArea);
				}

				centroidGenerator.generate(boardingPositions, stopArea);
			}

		}
	}

	static {
		ParserFactory.register(RegtoppStopParser.class.getName(), new ParserFactory() {
			@Override
			protected Parser create() {
				return new RegtoppStopParser();
			}
		});
	}

}
