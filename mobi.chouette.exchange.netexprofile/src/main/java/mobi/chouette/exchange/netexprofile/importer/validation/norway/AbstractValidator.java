package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.NOK;
import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.OK;

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
        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        for (int i = 1; i <= count; i++) {
            String checkPointKey = prefix + name + "-" + i;
            if (validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
                if (severities[i - 1].equals("W")) {
                    log.info("Adding checkpoint " + checkPointKey);
                    validationReporter.addItemToValidationReport(context, checkPointKey, "W");
                } else {
                    log.info("Adding checkpoint " + checkPointKey);
                    validationReporter.addItemToValidationReport(context, checkPointKey, "E");
                }
            }
        }
    }

    protected void addValidationError(Context context, String checkPointKey) {
        addValidationError(context, checkPointKey, null, (DataLocation) null);
    }

    protected void addValidationError(Context context, String checkPointKey, String detail, DataLocation dataLocation) {
        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        validationReporter.addCheckPointReportError(context, checkPointKey, detail, dataLocation);
        validationReporter.updateCheckPointReportState(context, checkPointKey, NOK);
    }

    /**
     * @deprecated provide file name also
     * @since 3.4.0-SNAPSHOT
     * @param context
     * @param checkPointKey
     * @param locationName
     */
    @Deprecated
    protected void addValidationError(Context context, String checkPointKey, String locationName) {
        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        validationReporter.addCheckPointReportError(context, checkPointKey, new DataLocation(null, locationName));
        validationReporter.updateCheckPointReportState(context, checkPointKey, NOK);
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
        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
            initializeCheckPoints(context);
        }
        validationReporter.updateCheckPointReportState(context, checkPointKey, OK);
    }

    protected boolean isElementPresent(Context context, String expression) throws XPathExpressionException {
        return validateElementNewNew(context, expression, 1);
    }

    protected boolean validateElementPresentNew(Context context, String expression, String errorCode,
            String errorMessage, String checkPointKey) throws XPathExpressionException {
        return validateElementNew(context, expression, 1, checkPointKey);
    }

    protected boolean validateElementNotPresentNew(Context context, String expression,
            String errorCode, String errorMessage, String checkPointKey) throws XPathExpressionException {
        return validateElementNew(context, expression, 0, checkPointKey);
    }

    protected void validateElementPresent(Context context, XPath xpath, Document document, String expression,
            String errorCode, String errorMessage, String checkPointKey) throws XPathExpressionException {
        validateElement(context, xpath, document, expression, 1, checkPointKey);
    }

    protected void validateElementNotPresent(Context context, XPath xpath, Document document, String expression,
            String errorCode, String errorMessage, String checkPointKey) throws XPathExpressionException {
        validateElement(context, xpath, document, expression, 0, checkPointKey);
    }

    private void validateElement(Context context, XPath xpath, Document document,
            String expression, int expectedCount, String checkPointKey) throws XPathExpressionException {
        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
            log.error("Checkpoint " + checkPointKey + " not present in ValidationReport");
        }
        NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        if (nodes.getLength() != expectedCount) {
            validationReporter.updateCheckPointReportState(context, checkPointKey, NOK);
        } else {
            validationReporter.updateCheckPointReportState(context, checkPointKey, OK);
        }
    }

    private boolean validateElementNew(Context context, String expression, int expectedCount, String checkPointKey) throws XPathExpressionException {
        Document document = (Document) context.get(NETEX_LINE_DATA_DOM);
        XPath xPath = (XPath) context.get(NETEX_LINE_DATA_XPATH);

        boolean result;

        ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
        if (validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
            log.error("Checkpoint " + checkPointKey + " not present in ValidationReport");
        }
        NodeList nodes = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);
        if (nodes.getLength() != expectedCount) {
            result = ERROR;
            validationReporter.updateCheckPointReportState(context, checkPointKey, NOK);
        } else {
            result = SUCCESS;
            validationReporter.updateCheckPointReportState(context, checkPointKey, OK);
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

    protected boolean isCollectionEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    protected boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

}
