package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.NOK;
import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.OK;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

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

	protected void prepareCheckPoint(Context context, String checkPointKey) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
			initializeCheckPoints(context);
			validationReporter.prepareCheckPointReport(context, checkPointKey);
		}
	}

	protected static void addItemToValidation(Context context, String prefix, String name, int count, String... severities) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validationReporter.addItemToValidationReport(context, prefix, name, count, severities);
	}

	protected void addObjectReference(Context context, String localContext, DataManagedObjectStructure object) {
		String objectId = object.getId();
		Context objectContext = getObjectContext(context, localContext, objectId);
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
	
	public static Predicate<Integer> exact(int v) {
		return p -> p == v;
	}

	public static Predicate<Integer> atLeast(int v) {
		return p -> p >= v;
	}

	protected void validateElementPresent(Context context, XPath xpath, Node document, String expression, String errorCode, String errorMessage,
			String checkPointKey) throws XPathExpressionException {
		validateElement(context, xpath, document, expression, exact(1), checkPointKey);
	}

	protected void validateAtLeastElementPresent(Context context, XPath xpath, Node document, String expression, int count, String errorCode, String errorMessage,
			String checkPointKey) throws XPathExpressionException {
		validateElement(context, xpath, document, expression, atLeast(count), checkPointKey);
	}

	protected void validateElementNotPresent(Context context, XPath xpath, Node document, String expression, String errorCode, String errorMessage,
			String checkPointKey) throws XPathExpressionException {
		validateElement(context, xpath, document, expression, exact(0), checkPointKey);
	}

	private void validateElement(Context context, XPath xpath, Node document, String expression, Predicate<Integer> function,String checkPointKey)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
			log.error("Checkpoint " + checkPointKey + " not present in ValidationReport");
		}
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if(function.test(nodes.getLength())) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			// TODO fix reporting with lineNumber etc
			validationReporter.addCheckPointReportError(context, checkPointKey, new DataLocation((String) context.get(FILE_NAME)));
		}
	}

	protected abstract void initializeCheckPoints(Context context);

	protected abstract void addObjectReference(Context context, DataManagedObjectStructure object);

	protected boolean isListEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}

	protected boolean isCollectionEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	protected boolean isEmpty(String text) {
		return text == null || text.isEmpty();
	}


	/*
	 * public static void validateExternalReferenceCorrect(Context context, XPath xpath, Document dom, String expression, ExternalReferenceValidator
	 * externalIdValidator, String checkpointName) throws XPathExpressionException {
	 * 
	 * ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
	 * 
	 * NodeList nodes = (NodeList) xpath.evaluate(expression, dom, XPathConstants.NODESET);
	 * 
	 * Set<String> ids = new HashSet<String>();
	 * 
	 * for (int i = 0; i < nodes.getLength(); i++) { Node item = nodes.item(i); String id = item.getTextContent(); ids.add(id); }
	 * 
	 * Collection<String> invalidIds = externalIdValidator.validateReferenceIds(ids); if (invalidIds.isEmpty()) {
	 * validationReporter.updateCheckPointReportState(context, checkpointName, ValidationReporter.RESULT.OK); } else {
	 * validationReporter.updateCheckPointReportState(context, checkpointName, ValidationReporter.RESULT.NOK); for (String s : invalidIds) { // TODO add details
	 * log.error("Netex profile validation error, invalid external reference " + s); } } }
	 */

}
