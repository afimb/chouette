package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.NOK;
import static mobi.chouette.exchange.validation.report.ValidationReporter.RESULT.OK;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public abstract class AbstractValidator implements Constant {

	
	public static final String _1_NETEX_DUPLICATE_IDS = "1-NETEXPROFILE-DuplicateIdentificators";
	public static final String _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingVersionAttribute";
	public static final String _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingReferenceVersionAttribute";
	public static final String _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS = "1-NETEXPROFILE-UnresolvedReferenceToCommonElements";

	
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

	protected Node selectNode(String string, XPath xpath, Node dom) throws XPathExpressionException {
		Node node = (Node) xpath.evaluate(string, dom, XPathConstants.NODE);
		return node;
	}

	protected NodeList selectNodeSet(String string, XPath xpath, Node dom) throws XPathExpressionException {
		NodeList node = (NodeList) xpath.evaluate(string, dom, XPathConstants.NODESET);
		return node;
	}

	protected void verifyReferencesToCommonElements(Context context, Set<IdVersion> localRefs, Set<IdVersion> localIds, Map<IdVersion, List<String>> commonIds) {
		if (commonIds != null) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

			Set<String> nonVersionedLocalRefs = localRefs.stream().map(e -> e.getId()).collect(Collectors.toSet());
			Set<String> nonVersionedLocalIds = localIds.stream().map(e -> e.getId()).collect(Collectors.toSet());

			Set<String> unresolvedReferences = new HashSet<>(nonVersionedLocalRefs);
			unresolvedReferences.removeAll(nonVersionedLocalIds);

			Set<String> commonIdsWithoutVersion = commonIds.keySet().stream().map(e -> e.getId()).collect(Collectors.toSet());
			if (commonIdsWithoutVersion.size() > 0) {
				for (String localRef : unresolvedReferences) {
					if (!commonIdsWithoutVersion.contains(localRef)) {
						// TODO add correct location
						validationReporter.addCheckPointReportError(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS,
								new DataLocation((String) context.get(FILE_NAME)));
						log.error("Unresolved reference to " + localRef + " in line file without any counterpart in the commonIds");
					}
				}
			} else {
				validationReporter.reportSuccess(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS);
			}
		}
	}

	protected void verifyUseOfVersionOnRefsToLocalElements(Context context, Set<IdVersion> localIds, Set<IdVersion> localRefs) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		Set<IdVersion> nonVersionedLocalRefs = localRefs.stream().filter(e -> e.getVersion() == null).collect(Collectors.toSet());
		Set<String> localIdsWithoutVersion = localIds.stream().map(e -> e.getId()).collect(Collectors.toSet());

		if (nonVersionedLocalRefs.size() > 0) {
			for (IdVersion id : nonVersionedLocalRefs) {
				if (localIdsWithoutVersion.contains(id.getId())) {
					// TODO add correct location
					validationReporter.addCheckPointReportError(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS,
							new DataLocation((String) context.get(FILE_NAME)));
					log.error("Found local reference to " + id.getId() + " in line file without use of version-attribute");
				}
			}
		} else {
			validationReporter.reportSuccess(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS);

		}
	}

	protected void verifyUseOfVersionOnLocalElements(Context context, Set<IdVersion> localIds) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		Set<IdVersion> nonVersionedLocalIds = localIds.stream().filter(e -> e.getVersion() == null).collect(Collectors.toSet());
		if (nonVersionedLocalIds.size() > 0) {
			for (IdVersion id : nonVersionedLocalIds) {
				// TODO add correct location
				validationReporter.addCheckPointReportError(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS,
						new DataLocation((String) context.get(FILE_NAME)));
				log.error("Id " + id + " in line file does not have version attribute set");
			}
		} else {
			validationReporter.reportSuccess(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS);
		}
	}

	protected void verifyNoDuplicatesWithCommonElements(Context context, Set<IdVersion> localIds, Map<IdVersion, List<String>> commonIds) {
		if (commonIds != null) {
			ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

			Set<IdVersion> overlappingIds = new HashSet<>(localIds);
			// Add code to check no duplicates as well as line file references to common files
			boolean duplicates = overlappingIds.retainAll(commonIds.keySet());
			if (duplicates) {
				for (IdVersion id : overlappingIds) {
					List<String> commonFileNames = commonIds.get(id);
					for (String fileName : commonFileNames) {
						// TODO add correct location
						validationReporter.addCheckPointReportError(context, _1_NETEX_DUPLICATE_IDS, new DataLocation(fileName));

					}
					log.error("Id " + id + " used in both line file and common files "
							+ ToStringBuilder.reflectionToString(commonFileNames.toArray(), ToStringStyle.SIMPLE_STYLE));
				}
			} else {
				validationReporter.reportSuccess(context, _1_NETEX_DUPLICATE_IDS);

			}
		}
	}

	protected Set<IdVersion> collectEntityIdentificators(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "id");
	}

	protected Set<IdVersion> collectEntityReferences(Context context, XPath xpath, Document dom) throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "ref");
	}

	protected Set<IdVersion> collectIdOrRefWithVersion(Context context, XPath xpath, Document dom, String attributeName) throws XPathExpressionException {
		NodeList nodes = (NodeList) xpath.evaluate("//n:*[not(name()='Codespace') and @" + attributeName + "]", dom, XPathConstants.NODESET);
		Set<IdVersion> ids = new HashSet<IdVersion>();
		int idCount = nodes.getLength();
		for (int i = 0; i < idCount; i++) {

			String id = nodes.item(i).getAttributes().getNamedItem(attributeName).getNodeValue();
			String version = null;
			Node versionAttribute = nodes.item(i).getAttributes().getNamedItem("version");
			if (versionAttribute != null) {
				version = versionAttribute.getNodeValue();
			}
			ids.add(new IdVersion(id, version));
		}
		return ids;
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
