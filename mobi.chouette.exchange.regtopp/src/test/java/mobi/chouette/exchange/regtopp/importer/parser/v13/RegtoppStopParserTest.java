package mobi.chouette.exchange.regtopp.importer.parser.v13;

import mobi.chouette.exchange.regtopp.model.v13.RegtoppStopPointSTP;
import mobi.chouette.model.StopArea;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class RegtoppStopParserTest {

    private final RegtoppStopParser regtoppStopParser = new RegtoppStopParser();

    @Test
    public void setBoardingPositionCommentFromStpDescription() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Bærum rådhus (Ved Rådhustorget)");

        String parentStopAreaName = "Bærum rådhus";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName, "StopArea's name should not have been changed");
        assertEquals(boardingPosition.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getComment(), "Ved Rådhustorget");
    }

    @Test
    public void doNotSetBoardingPositionCommentToSameAsStopAreaName() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        String parentStopAreaName = "Some stop place";
        regtoppStopPointSTP.setDescription(parentStopAreaName);

        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName, "StopArea's name should not have been changed");
        assertEquals(boardingPosition.getName(), parentStopAreaName);
        assertNull(boardingPosition.getComment(), null);
    }

    @Test
    public void setBoardingPositionCommentFromStpDescriptionIfNoMatchingPattern() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Some non matching description");

        String parentStopAreaName = "Parent stop name";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getComment(), regtoppStopPointSTP.getDescription());
    }

    @Test
    public void setBoardingPositionCommentFromStpDescriptionWithSpaces() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Allèen  (mot Manglerud ) (Plf A)");

        String parentStopAreaName = "Allèen";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName, "StopArea's name should not have been changed");
        assertEquals(boardingPosition.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getComment(), "mot Manglerud");
    }

    @Test
    public void setBoardingPositionCommentWithPlatformCode() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Allèen (Plf A)");

        String parentStopAreaName = "Allèen";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName, "StopArea's name should not have been changed");
        assertEquals(boardingPosition.getName(), parentStopAreaName);

        // We are not currently supporting extraction of single parenthesis with designation/registration number.
        // So this is currently expected to be mapped to comment.
        assertEquals(boardingPosition.getComment(), "Plf A");
    }

    @Test
    public void setBoardingPositionCommentFromDescriptionWithMultipleParentheses() {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Økern T (fra sentrum/mot nord)(Plf C)");

        String parentStopAreaName = "Økern T";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName, "StopArea's name should not have been changed");
        assertEquals(boardingPosition.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getComment(), "fra sentrum/mot nord");
    }

    @Test
    public void setParentStopNameFromStopPointIfEmpty() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Bærum rådhus (Ved Rådhustorget)");

        StopArea parentStopArea = new StopArea();
        // No name set

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), "Bærum rådhus");
        assertEquals(boardingPosition.getName(), "Bærum rådhus");
        assertEquals(boardingPosition.getComment(), "Ved Rådhustorget");
    }

    @Test
    public void setParentStopNameFromStopPointIfEmptyAndMultipleParentheses() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();
        regtoppStopPointSTP.setDescription("Økern T(mot vest/mot sentrum)(Plf A)");

        StopArea parentStopArea = new StopArea();
        // No name set

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), "Økern T");
        assertEquals(boardingPosition.getName(), "Økern T");
        assertEquals(boardingPosition.getComment(), "mot vest/mot sentrum");
        assertEquals(boardingPosition.getRegistrationNumber(), "A");
    }

    @Test
    public void setBoardingPositionNameFromParentStop() throws Exception {
        RegtoppStopPointSTP regtoppStopPointSTP = new RegtoppStopPointSTP();

        String parentStopAreaName = "Parent stop name";
        StopArea parentStopArea = new StopArea();
        parentStopArea.setName(parentStopAreaName);

        StopArea boardingPosition = new StopArea();

        regtoppStopParser.setNameAndComment(regtoppStopPointSTP, boardingPosition, parentStopArea);

        assertEquals(parentStopArea.getName(), parentStopAreaName);
        assertEquals(boardingPosition.getName(), parentStopAreaName);
    }

}