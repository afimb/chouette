package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.REFERENTIAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.parser.AbstractConverter;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser {

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
			String commercialStopAreaId = AbstractConverter.extractOriginalId(sa.getObjectId()).substring(0, 8);
			List<StopArea> list = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (list == null) {
				list = new ArrayList<StopArea>();
				boardingPositionsByStopArea.put(commercialStopAreaId, list);
			}
			list.add(sa);
		}

		for (String commercialStopAreaId : boardingPositionsByStopArea.keySet()) {
			List<StopArea> list = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (list.size() > 0) {
				// Create parent stopArea
				String objectId = AbstractConverter.composeObjectId(configuration.getObjectIdPrefix(), StopArea.STOPAREA_KEY, commercialStopAreaId);
				StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
				stopArea.setName(list.get(0).getName()); // TODO using name of first stoppoint, should be identical for all boarding positions according to spec
				stopArea.setAreaType(ChouetteAreaEnum.StopPlace);

				for (StopArea bp : list) {
					bp.setParent(stopArea);
				}

				// Calculate center coordinate
				// TODO currently using only first boarding position stop coordinates. Need to be centered
				stopArea.setLongitude(list.get(0).getLongitude());
				stopArea.setLatitude(list.get(0).getLatitude());
				stopArea.setLongLatType(list.get(0).getLongLatType());
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
