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
     * Pattern for matching description content in parentheses.
     * For instance: 'Stop place name (some comment) (some optional platform with designation A)'
     */
    private static final Pattern PATTERN = Pattern.compile("(.[^\\)]*)?\\s?\\((.[^\\)]*)\\)?\\s?(\\(.[^\\)]*\\s([a-zA-Z0-9]{0,3})\\))?");

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
                    String objectId = ObjectIdCreator.createStopPlaceId(configuration, stop.getStopId());

                    StopArea stopArea = ObjectFactory.getStopArea(referential, objectId);
                    stopArea.setName(StringUtils.trimToNull(stop.getFullName()));
                    // stopArea.setRegistrationNumber(stop.getShortName());
                    stopArea.setAreaType(PARENT_STOP_PLACE_TYPE);

                    convertAndSetCoordinates(stopArea, stop.getX(), stop.getY(), projection);

                    List<RegtoppStopPointSTP> stopPoints = stopPointsByStopId.getValue(stop.getStopId());
                    if (stopPoints != null) {
                        for (RegtoppStopPointSTP regtoppStopPoint : stopPoints) {
                            String chouetteStopPointId = ObjectIdCreator.createQuayId(configuration,
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

    /**
     * Set stop place name, boarding position comment and if detected, platform code.
     * Stop area and boarding position name will be equal.
     *
     * @param regtoppStopPoint to get and parse description from
     * @param boardingPosition to optionally set comment and registration number (platform code)
     * @param stopArea if stop stop area does not have any name, it can sometimes be found in stop point description. It will only be set if already empty.
     */
    public void setNameAndComment(RegtoppStopPointSTP regtoppStopPoint, StopArea boardingPosition, StopArea stopArea) {

        if (StringUtils.isNotBlank(regtoppStopPoint.getDescription())) {
            String description = regtoppStopPoint.getDescription().trim();

            if (StringUtils.isNotBlank(stopArea.getName())) {
                boolean descriptionAndStopAreaNameEqual = description.equals(stopArea.getName());

                if (!descriptionAndStopAreaNameEqual && description.startsWith(stopArea.getName())) {
                    // Remove stop place name from comment
                    String comment = description.substring(stopArea.getName().length()).trim();
                    extractCommentFromParentheses(comment, stopArea, boardingPosition, false);
                } else if (!descriptionAndStopAreaNameEqual) {
                    boardingPosition.setComment(description.trim());
                }
            } else {
                extractCommentFromParentheses(regtoppStopPoint.getDescription(), stopArea, boardingPosition, true);
            }
        }
        if (StringUtils.isNotBlank(stopArea.getName())) {
            // Set parent stop area name
            boardingPosition.setName(stopArea.getName());
        }
        log.debug("Parent stop area name: '" + stopArea.getName()
                + "', boarding position name: '" + boardingPosition.getName()
                + "', boarding position comment: '" + boardingPosition.getComment() + "'");
    }

    private void extractCommentFromParentheses(String description, StopArea stopArea, StopArea boardingPosition, boolean setStopAreaName) {
        Matcher matcher = PATTERN.matcher(description);

        if (matcher.find() && matcher.groupCount() > 0) {
            if (matcher.group(1) != null && setStopAreaName) {
                String name = matcher.group(1).trim();
                stopArea.setName(name);
            }

            if (matcher.groupCount() > 1) {
                int group = 2;
                if (matcher.group(group) != null) {
                    String comment = matcher.group(group).trim();
                    boardingPosition.setComment(comment);
                }
            }

            if (matcher.groupCount() > 3) {
                int group = 4;
                if (matcher.group(group) != null && StringUtils.isEmpty(boardingPosition.getRegistrationNumber())) {
                    String designation = matcher.group(group).trim();
                    boardingPosition.setRegistrationNumber(designation);
                }
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
