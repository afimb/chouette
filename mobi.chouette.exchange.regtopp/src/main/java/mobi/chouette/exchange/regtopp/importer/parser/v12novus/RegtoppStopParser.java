package mobi.chouette.exchange.regtopp.importer.parser.v12novus;

import static mobi.chouette.common.Constant.CONFIGURATION;
import static mobi.chouette.common.Constant.PARSER;
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
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.parser.CentroidGenerator;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v12novus.RegtoppStopHPL;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;

@Log4j
public class RegtoppStopParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser {

	private final CentroidGenerator centroidGenerator = new CentroidGenerator();

	@Override
	public void parse(Context context) throws Exception {
		RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
		Referential referential = (Referential) context.get(REFERENTIAL);
		RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);

		createBoardingPositions(importer, referential, configuration);
		Map<String, List<StopArea>> boardingPositionsByStopArea = groupBoardingPositions(referential);
		createParentStopArea(boardingPositionsByStopArea, configuration, referential);
	}

	public void createParentStopArea(Map<String, List<StopArea>> boardingPositionsByStopArea, RegtoppImportParameters configuration, Referential referential) {
		for (String commercialStopAreaId : boardingPositionsByStopArea.keySet()) {
			List<StopArea> boardingPositions = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (boardingPositions.size() > 0) {
				// Create parent stopArea
				String objectId = ObjectIdCreator.createStopPlaceId(configuration, commercialStopAreaId);
				StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
				stopArea.setName(boardingPositions.get(0).getName()); // TODO using name of first stop point, should be identical for all boarding positions according to spec
				stopArea.setAreaType(PARENT_STOP_PLACE_TYPE);

				for (StopArea bp : boardingPositions) {
					bp.setParent(stopArea);
				}

				centroidGenerator.generate(boardingPositions, stopArea);
			}
		}
	}

	public Map<String, List<StopArea>> groupBoardingPositions(Referential referential) {
		Map<String, List<StopArea>> boardingPositionsByStopArea = new HashMap<>();
		for (StopArea sa : referential.getStopAreas().values()) {
			String commercialStopAreaId = ObjectIdCreator.extractOriginalId(sa.getObjectId()).substring(0, 8);
			List<StopArea> list = boardingPositionsByStopArea.get(commercialStopAreaId);
			if (list == null) {
				list = new ArrayList<>();
				boardingPositionsByStopArea.put(commercialStopAreaId, list);
			}
			list.add(sa);
		}
		return boardingPositionsByStopArea;
	}

	private void createBoardingPositions(RegtoppImporter importer, Referential referential, RegtoppImportParameters configuration) throws Exception {
		for (AbstractRegtoppStopHPL stop : importer.getStopById()) {
			if(shouldImportHPL(stop)) {
				createBoardingPosition(stop, configuration, referential);
			}
		}
	}

	public void createBoardingPosition(AbstractRegtoppStopHPL s,
									   RegtoppImportParameters regtoppImportParameters, Referential referential) {
		
		RegtoppStopHPL stop = (RegtoppStopHPL) s;
		
    	String chouetteStopPointId;
    	
    	// Do not append stopPointId if values is "00"
    	if(stop.getStopPointId() == "00") {
    		chouetteStopPointId = ObjectIdCreator.createQuayId(regtoppImportParameters,
    				stop.getStopId());
    	} else {
    		chouetteStopPointId = ObjectIdCreator.createQuayId(regtoppImportParameters,
    				stop.getFullStopId());
    	}


		StopArea boardingPosition = ObjectFactory.getStopArea(referential, chouetteStopPointId);
		boardingPosition.setName(stop.getFullName());
		boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
		convertAndSetCoordinates(boardingPosition, stop.getX(), stop.getY(), regtoppImportParameters.getCoordinateProjection());
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
