package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.Detail;
import mobi.chouette.exchange.validation.report.ValidationReport;
import no.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO consider merging this class with AbstractNetexProfileValidator
@Log4j
public abstract class AbstractValidator implements Constant {

    protected static final String PREFIX = "2-NETEX-";
    protected static final String OBJECT_IDS = "encountered_ids";

    @SuppressWarnings("unchecked")
    public static void resetContext(Context context) {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        if (validationContext != null) {
            for (String key : validationContext.keySet()) {
                if (key.equals(OBJECT_IDS)) {
                    Set<String> objects = (Set<String>) validationContext.get(key);
                    objects.clear();
                } else {
                    Context localContext = (Context) validationContext.get(key);
                    localContext.clear();
                }

            }
        }
    }

    protected static void addItemToValidation(Context context, String prefix, String name, int count, String... severities) {
        ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
        for (int i = 1; i <= count; i++) {
            String key = prefix + name + "-" + i;
            if (validationReport.findCheckPointByName(key) == null) {
                if (severities[i - 1].equals("W")) {
                    log.info("Adding checkpoint " + key);
                    validationReport.addCheckPoint(
                            new CheckPoint(key, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
                } else {
                    log.info("Adding checkpoint " + key);
                    validationReport.addCheckPoint(
                            new CheckPoint(key, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.ERROR));
                }
            }
        }
    }

    protected void addValidationError(Context context, String checkPointKey) {
        ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
        CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
        checkPoint.setState(CheckPoint.RESULT.NOK);
    }

    protected void addValidationError(Context context, String checkPointKey, Detail detail) {
        ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
        CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
        checkPoint.addDetail(detail);
        checkPoint.setState(CheckPoint.RESULT.NOK);
    }

    @SuppressWarnings("unchecked")
    protected static Context getObjectContext(Context context, String localContextName, String objectId) {
        Context validationContext = (Context) context.get(VALIDATION_CONTEXT);
        if (validationContext == null) {
            validationContext = new Context();
            context.put(VALIDATION_CONTEXT, validationContext);
            validationContext.put(OBJECT_IDS, new HashSet<String>());
        }
        Set<String> objectIds = (Set<String>) validationContext.get(OBJECT_IDS);
        objectIds.add(objectId);
        Context localContext = (Context) validationContext.get(localContextName);
        if (localContext == null) {
            localContext = new Context();
            validationContext.put(localContextName, localContext);
        }
        Context objectContext = (Context) localContext.get(objectId);
        if (objectContext == null) {
            objectContext = new Context();
            localContext.put(objectId, objectContext);
        }
        return objectContext;
    }

    protected void prepareCheckPoint(Context context, String checkPointKey) {
        ValidationReport validationReport = (ValidationReport) context.get(VALIDATION_REPORT);
        CheckPoint checkPoint = validationReport.findCheckPointByName(checkPointKey);
        if (checkPoint == null) {
            initializeCheckPoints(context);
            checkPoint = validationReport.findCheckPointByName(checkPointKey);
        }
        if (checkPoint.getState().equals(CheckPoint.RESULT.UNCHECK)) {
            checkPoint.setState(CheckPoint.RESULT.OK);
        }
    }

    protected boolean isElementPresent(Context context, String expression) throws XPathExpressionException {
        return validateElementNewNew(context, expression, 1);
    }

    protected boolean validateElementPresentNew(Context context, String expression, String errorCode,
            String errorMessage, String checkpointName) throws XPathExpressionException {
        return validateElementNew(context, expression, 1, checkpointName);
    }

    protected boolean validateElementNotPresentNew(Context context, String expression,
            String errorCode, String errorMessage, String checkpointName) throws XPathExpressionException {
        return validateElementNew(context, expression, 0, checkpointName);
    }

    protected void validateElementPresent(Context context, XPath xpath, Document document, String expression,
            String errorCode, String errorMessage, String checkpointName) throws XPathExpressionException {
        validateElement(context, xpath, document, expression, 1, checkpointName);
    }

    protected void validateElementNotPresent(Context context, XPath xpath, Document document, String expression,
            String errorCode, String errorMessage, String checkpointName) throws XPathExpressionException {
        validateElement(context, xpath, document, expression, 0, checkpointName);
    }

    private void validateElement(Context context, XPath xpath, Document document,
            String expression, int expectedCount, String checkpointName) throws XPathExpressionException {
        ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);
        CheckPoint checkpoint = validationReport.findCheckPointByName(checkpointName);
        if (checkpoint == null) {
            log.error("Checkpoint " + checkpointName + " not present in ValidationReport");
        }
        NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        if (nodes.getLength() != expectedCount) {
            checkpoint.setState(CheckPoint.RESULT.NOK);
        } else {
            checkpoint.setState(CheckPoint.RESULT.OK);
        }
    }

    private boolean validateElementNew(Context context, String expression, int expectedCount, String checkpointName) throws XPathExpressionException {
        Document document = (Document) context.get(NETEX_LINE_DATA_DOM);
        XPath xPath = (XPath) context.get(NETEX_LINE_DATA_XPATH);

        boolean result;

        ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);
        CheckPoint checkpoint = validationReport.findCheckPointByName(checkpointName);
        if (checkpoint == null) {
            log.error("Checkpoint " + checkpointName + " not present in ValidationReport");
        }
        NodeList nodes = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        if (nodes.getLength() != expectedCount) {
            result = ERROR;
            checkpoint.setState(CheckPoint.RESULT.NOK);
        } else {
            result = SUCCESS;
            checkpoint.setState(CheckPoint.RESULT.OK);
        }
        return result;
    }

    private boolean validateElementNewNew(Context context, String expression, int expectedCount) throws XPathExpressionException {
        Document document = (Document) context.get(NETEX_LINE_DATA_DOM);
        XPath xPath = (XPath) context.get(NETEX_LINE_DATA_XPATH);
        boolean result;
        NodeList nodes = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        if (nodes.getLength() != expectedCount) {
            result = ERROR;
        } else {
            result = SUCCESS;
        }
        return result;
    }

    public abstract void addObjectReference(Context context, DataManagedObjectStructure object);

    protected void addObjectReference(Context context, String localContext, DataManagedObjectStructure object) {
        String objectId = object.getId();
        Context objectContext = getObjectContext(context, localContext, objectId);
    }

    protected abstract void initializeCheckPoints(Context context);

    protected boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    protected boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

}
