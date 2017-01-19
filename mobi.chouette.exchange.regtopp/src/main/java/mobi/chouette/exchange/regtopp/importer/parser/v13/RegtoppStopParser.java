package mobi.chouette.exchange.regtopp.importer.parser.v13;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.regtopp.importer.RegtoppImportParameters;
import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;
import mobi.chouette.exchange.regtopp.importer.index.Index;
import mobi.chouette.exchange.regtopp.importer.parser.ObjectIdCreator;
import mobi.chouette.exchange.regtopp.model.AbstractRegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.enums.StopType;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopHPL;
import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.model.StopArea;
import mobi.chouette.model.type.ChouetteAreaEnum;
import mobi.chouette.model.util.ObjectFactory;
import mobi.chouette.model.util.Referential;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mobi.chouette.common.Constant.*;

@Log4j
public class RegtoppStopParser extends mobi.chouette.exchange.regtopp.importer.parser.v11.RegtoppStopParser {

    /**
     * Pattern for matching content in parentheses.
     */
    private static final Pattern PATTERN = Pattern.compile("\\((.[^\\)]*)\\)");

    @Override
    public void parse(Context context) throws Exception {
        try {
            RegtoppImporter importer = (RegtoppImporter) context.get(PARSER);
            Referential referential = (Referential) context.get(REFERENTIAL);
            RegtoppImportParameters configuration = (RegtoppImportParameters) context.get(CONFIGURATION);
            String projection = configuration.getCoordinateProjection();

            Index<List<RegtoppStopPointSTP>> stopPointsByStopId = importer.getStopPointsByStopId();

            for (AbstractRegtoppStopHPL abstractStop : importer.getStopById()) {
                RegtoppStopHPL stop = (RegtoppStopHPL) abstractStop;
                if (shouldImportHPL(abstractStop) && (stop.getType() == StopType.Stop
                        || (stop.getType() == StopType.Other && !stop.getFullName().equals("Lokasjonspunkt")))) {
                    String objectId = ObjectIdCreator.createStopAreaId(configuration, stop.getStopId());

                    StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
                    stopArea.setName(StringUtils.trimToNull(stop.getFullName()));
                    // stopArea.setRegistrationNumber(stop.getShortName());
                    stopArea.setAreaType(PARENT_STOP_PLACE_TYPE);

                    convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);

                    List<RegtoppStopPointSTP> stopPoints = stopPointsByStopId.getValue(stop.getStopId());
                    if (stopPoints != null) {
                        for (RegtoppStopPointSTP regtoppStopPoint : stopPoints) {
                            String chouetteStopPointId = ObjectIdCreator.createStopAreaId(configuration,
                                    regtoppStopPoint.getFullStopId());
                            StopArea boardingPosition = ObjectFactory.getStopArea(referential, chouetteStopPointId);

                            convertAndSetCoordinates(boardingPosition, regtoppStopPoint.getX(), regtoppStopPoint.getY(),
                                    projection);
                            boardingPosition.setAreaType(ChouetteAreaEnum.BoardingPosition);
                            boardingPosition
                                    .setRegistrationNumber(StringUtils.trimToNull(regtoppStopPoint.getStopPointDesignation()));

                            setNameAndComment(regtoppStopPoint, boardingPosition, stopArea);

                            boardingPosition.setParent(stopArea);
                        }
                    }

                    if (stopArea.getName() == null) {
                        // Fallback, must have name
                        stopArea.setName("Noname");
                    }

                } else {
                    // TODO parse other node types (if really used, only Ruter
                    // uses this)
                    log.warn("Ignoring HPL stop of type Other: " + stop);
                }

            }

        } catch (Exception e) {
            log.error("Error parsing StopArea", e);
            throw e;
        }
    }

    public void setNameAndComment(RegtoppStopPointSTP regtoppStopPoint, StopArea boardingPosition, StopArea stopArea) {

        if (StringUtils.isNotBlank(regtoppStopPoint.getDescription())) {
            String description = regtoppStopPoint.getDescription().trim();

            if (StringUtils.isNotBlank(stopArea.getName())) {

                boolean descriptionAndStopAreaNameEqual = description.equals(stopArea.getName());

                if (!descriptionAndStopAreaNameEqual && description.startsWith(stopArea.getName())) {
                    // Remove stop place name from comment
                    String comment = description.substring(stopArea.getName().length()).trim();
                    boardingPosition.setComment(extractCommentFromParentheses(comment));
                } else if (!descriptionAndStopAreaNameEqual) {
                    boardingPosition.setComment(description.trim());
                }
            } else if (!splitAndSetNameAndComment(description, boardingPosition, stopArea)) {
                boardingPosition.setComment("");
                stopArea.setName(description);
            }
        }
        if (StringUtils.isNotBlank(stopArea.getName())) {
            // Use parent stop area name
            boardingPosition.setName(stopArea.getName());
        }
        log.debug("Parent stop area name: '" + stopArea.getName()
                + "', boarding position name: '" + boardingPosition.getName()
                + "', boarding position comment: '" + boardingPosition.getComment() + "'");
    }

    private String extractCommentFromParentheses(String description) {
        String newDescription;
        Matcher matcher = PATTERN.matcher(description);

        if (matcher.find() && matcher.groupCount() > 0) {
            newDescription = matcher.group(1).trim();

            if(matcher.find()) {
                System.out.println(matcher.group(1));
            }
        } else {
            newDescription = description.trim();
        }

        return newDescription;

    }

    /**
     * If description contains parentheses, split it up to stop area name and boarding position comment.
     * Useful only if parent stop area name is not set.
     *
     * @param description      description with stop area name and parentheses containing description
     * @param boardingPosition to set comment on
     * @param stopArea         to set name
     */
    private boolean splitAndSetNameAndComment(String description, StopArea boardingPosition, StopArea stopArea) {
        if (description.matches(".*\\(.*\\)")) {
            int indexOfStart = description.indexOf('(');
            int indexOfEnd = description.indexOf(')');
            boardingPosition.setComment(description.substring(indexOfStart + 1, indexOfEnd).trim());
            stopArea.setName(description.substring(0, indexOfStart).trim());
            return true;
        }
        return false;
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
