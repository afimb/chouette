package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.CheckPoint.RESULT;
import mobi.chouette.exchange.validation.report.CheckPoint.SEVERITY;
import mobi.chouette.exchange.validation.report.ValidationReport;
import no.rutebanken.netex.model.PublicationDeliveryStructure;

@Log4j
public abstract class AbstractNetexProfileValidator implements NetexProfileValidator {

	private XPathFactory factory = XPathFactory.newInstance();

	@Override
	public boolean validate(Context context) throws XPathExpressionException {

		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new NetexNamespaceContext());

		// http://www.netex.org.uk/netex netex namespace
		NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(Constant.CONFIGURATION);
		PublicationDeliveryStructure lineDeliveryStructure = (PublicationDeliveryStructure) context.get(Constant.NETEX_LINE_DATA_JAVA);
		Document dom = (Document) context.get(Constant.NETEX_LINE_DATA_DOM);

		// Call concrete validator implementation
		validate(context, lineDeliveryStructure, dom, xpath);

		// Check if validation report has errors
		ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);

		boolean validationOK = true;
		for (CheckPoint c : validationReport.getCheckPoints()) {
			if (c.getSeverity() == SEVERITY.ERROR && c.getState() == RESULT.NOK) {
				validationOK = false;
				break;
			}
		}

		return validationOK;
	}

	protected abstract void validate(Context context, PublicationDeliveryStructure lineDeliveryStructure, Document dom, XPath xpath)
			throws XPathExpressionException;

	/*
	 * Validate that a set of references identified by the given xpath expression is valid (by calling given external validator)
	 */
	public static void validateExternalReferenceCorrect(Context context, XPath xpath, Document dom, String expression,
			ExternalReferenceValidator externalIdValidator, String checkpointName) throws XPathExpressionException {

		ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);
		CheckPoint checkpoint = validationReport.findCheckPointByName(checkpointName);

		NodeList nodes = (NodeList) xpath.evaluate(expression, dom, XPathConstants.NODESET);

		Set<String> ids = new HashSet<String>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Node item = nodes.item(i);
			String id = item.getTextContent();
			ids.add(id);
		}

		Collection<String> invalidIds = externalIdValidator.validateReferenceIds(ids);
		if (invalidIds.isEmpty()) {
			checkpoint.setState(RESULT.OK);
		} else {
			checkpoint.setState(RESULT.NOK);
			for (String s : invalidIds) {
				// TODO add details
				log.error("Netex profile validation error, invalid external reference " + s);
			}
		}
	}

	/*
	 * Validate that an xml element is present
	 */
	public static void validateElementPresent(Context context, XPath xpath, Document document, String expression, String errorCode, String errorMessage,
			String checkpointName) throws XPathExpressionException {
		validateElement(context, xpath, document, expression, 1, checkpointName);
	}

	/*
	 * Validate that an xml element is present
	 */
	public static void validateElementNotPresent(Context context, XPath xpath, Document document, String expression, String errorCode, String errorMessage,
			String checkpointName) throws XPathExpressionException {
		validateElement(context, xpath, document, expression, 0, checkpointName);
	}

	/*
	 * Validate that the occurrence(s) of an xml element conform to the correct cardinality
	 */
	private static void validateElementCardinality(Context context, XPath xpath, Document document, String expression,
			int minOccurs, int maxOccurs, String checkpointName) throws XPathExpressionException {
/*
		String countExpression = String.format("count(%s)", expression);
		double elementCount = (Double) xpath.evaluate(countExpression, document, XPathConstants.NUMBER);
*/
	}

	private static void validateElement(Context context, XPath xpath, Document document, String expression, int expectedCount, String checkpointName)
			throws XPathExpressionException {
		ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);
		CheckPoint checkpoint = validationReport.findCheckPointByName(checkpointName);
		if (checkpoint == null) {
			log.error("Checkpoint " + checkpointName + " not present in ValidationReport");
		}

		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() != expectedCount) {
			checkpoint.setState(RESULT.NOK);
		} else {
			checkpoint.setState(RESULT.OK);
		}
	}

	protected void addCheckpoints(Context context, String key, CheckPoint.SEVERITY severity) {

		ValidationReport validationReport = (ValidationReport) context.get(Constant.VALIDATION_REPORT);
		// Add checkpoints that are to be checked in the validate() method above
		if (validationReport.findCheckPointByName(key) == null) {
			log.info("Adding checkpoint " + key);
			validationReport.addCheckPoint(new CheckPoint(key, CheckPoint.RESULT.UNCHECK, severity));
		}

	}

}
