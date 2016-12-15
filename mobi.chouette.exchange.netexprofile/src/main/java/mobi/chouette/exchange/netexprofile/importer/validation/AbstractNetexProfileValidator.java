package mobi.chouette.exchange.netexprofile.importer.validation;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import mobi.chouette.exchange.netexprofile.importer.PositionalXMLReader;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.util.ProfileValidatorCodespace;
import mobi.chouette.exchange.validation.report.DataLocation;
import mobi.chouette.exchange.validation.report.ValidationReporter;

@Log4j
public abstract class AbstractNetexProfileValidator implements Constant {

	public static final String _1_NETEX_UNKNOWN_PROFILE = "1-NETEX-UnknownProfile";
	public static final String _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES = "1-NETEXPROFILE-DuplicateIdentificatorsAcrossLineAndCommonFiles";
	public static final String _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingVersionAttribute";
	public static final String _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingReferenceVersionAttribute";
	public static final String _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS = "1-NETEXPROFILE-UnresolvedReferenceToCommonElements";
	public static final String _1_NETEX_INVALID_ID_STRUCTURE = "1-NETEXPROFILE-InvalidIdStructure";
	public static final String _1_NETEX_UNAPPROVED_CODESPACE_DEFINED = "1-NETEXPROFILE-UnapprovedCodespaceDefined";
	public static final String _1_NETEX_USE_OF_UNAPPROVED_CODESPACE = "1-NETEXPROFILE-UseOfUnapprovedCodespace";
	protected static final String PREFIX = "2-NETEX-";
	protected static final String OBJECT_IDS = "encountered_ids";
	public static final String _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES = "1-NETEXPROFILE-DuplicateIdentificatorsAcrossLineFiles";

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

	protected void validateElementPresent(Context context, XPath xpath, Node document, String expression, String errorCode, String errorMessage,
			String checkPointKey) throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() == 1) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: "+expression+" did not return 1 node");

			validationReporter.addCheckPointReportError(context, checkPointKey, DataLocationHelper.findDataLocation(context, document));
		}
	}

	protected void validateAtLeastElementPresent(Context context, XPath xpath, Node document, String expression, int count, String errorCode,
			String errorMessage, String checkPointKey) throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() >= count) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: "+expression+" did not return at least 1 node but "+nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				validationReporter.addCheckPointReportError(context, checkPointKey, DataLocationHelper.findDataLocation(context, nodes.item(i)));
			}
		}
	}

	protected void validateElementNotPresent(Context context, XPath xpath, Node document, String expression, String errorCode, String errorMessage,
			String checkPointKey) throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() == 0) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: "+expression+" should return 0 nodes, but returned "+nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				validationReporter.addCheckPointReportError(context, checkPointKey, DataLocationHelper.findDataLocation(context, nodes.item(i)));
			}
		}
	}

	protected void validateCheckpointExists(Context context, String checkPointKey, ValidationReporter validationReporter) {
		if (!validationReporter.checkIfCheckPointExists(context, checkPointKey)) {
			log.error("Checkpoint " + checkPointKey + " not present in ValidationReport");
			throw new RuntimeException(
					"Checkpoint " + checkPointKey + " does not exist - did you add a validation rule but forgot to register the checkpoint?");
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

	protected void verifyAcceptedCodespaces(Context context, XPath xpath, Node dom, Set<ProfileValidatorCodespace> acceptedCodespaces)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		boolean onlyAcceptedCodespaces = true;
		NodeList codespaces = selectNodeSet("//n:Codespace", xpath, dom);
		for (int i = 0; i < codespaces.getLength(); i++) {
			Node n = codespaces.item(i);
			ProfileValidatorCodespace cs = new ProfileValidatorCodespace((String) xpath.evaluate("n:Xmlns", n, XPathConstants.STRING),
					(String) xpath.evaluate("n:XmlnsUrl", n, XPathConstants.STRING));
			if (!acceptedCodespaces.contains(cs)) {
				// TODO add correct location
				validationReporter.addCheckPointReportError(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED, DataLocationHelper.findDataLocation(context, n));
				log.error("Codespace " + cs + " is not accepted for this validation");
				onlyAcceptedCodespaces = false;
			}
		}

		if(onlyAcceptedCodespaces) {
			validationReporter.reportSuccess(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED);
		}
	}

	protected void verifyReferencesToCommonElements(Context context, Set<IdVersion> localRefs, Set<IdVersion> localIds,
			Map<IdVersion, List<String>> commonIds) {
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
						// TODO add faster lookup
						IdVersion id = null;
						for(IdVersion i : localRefs ) {
							if(i.getId().equals(localRef)) {
								id = i;
								break;
							}
						}
						validationReporter.addCheckPointReportError(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS,
								DataLocationHelper.findDataLocation(id));
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
							DataLocationHelper.findDataLocation(id));
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
				validationReporter.addCheckPointReportError(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS, DataLocationHelper.findDataLocation(id));
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
			overlappingIds.retainAll(commonIds.keySet());
			if (overlappingIds.size() > 0) {
				for (IdVersion id : overlappingIds) {
					List<String> commonFileNames = commonIds.get(id);
					for (String fileName : commonFileNames) {
						// TODO add correct location
						validationReporter.addCheckPointReportError(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES,
								DataLocationHelper.findDataLocation(fileName, id));

					}
					log.error("Id " + id + " used in both line file and common files "
							+ ToStringBuilder.reflectionToString(commonFileNames.toArray(), ToStringStyle.SIMPLE_STYLE));
				}
			} else {
				validationReporter.reportSuccess(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES);

			}
		}
	}

	protected void verifyNoDuplicatesAcrossLineFiles(Context context, Set<IdVersion> localIds, Set<String> ignorableElementNames) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		Set<IdVersion> alreadyFoundLocalIds = (Set<IdVersion>) context.get(Constant.NETEX_EXISTING_LINE_IDS);
		if (alreadyFoundLocalIds == null) {
			alreadyFoundLocalIds = new HashSet<>();
			context.put(Constant.NETEX_EXISTING_LINE_IDS, alreadyFoundLocalIds);
		}

		boolean duplicateFound = false;

		for (IdVersion id : localIds) {
			if (alreadyFoundLocalIds.contains(id) && !ignorableElementNames.contains(id.getElementName())) {
				// Log duplicate
				duplicateFound = true;
				validationReporter.addCheckPointReportError(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES, DataLocationHelper.findDataLocation(id));
				log.error("Id " + id + " in line file have already been defined in another file");
			} else {
				alreadyFoundLocalIds.add(id);
			}
		}

		if (!duplicateFound) {
			validationReporter.reportSuccess(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES);
		}
	}

	protected void verifyIdStructure(Context context, Set<IdVersion> localIds, Map<IdVersion, List<String>> commonIds, String regex,
			Set<ProfileValidatorCodespace> validCodespaces) {
		Set<String> validPrefixes = null;
		if (validCodespaces != null) {
			validPrefixes = new HashSet<>();
			for (ProfileValidatorCodespace cs : validCodespaces) {
				validPrefixes.add(cs.getXmlns());
			}
		}

		Pattern p = Pattern.compile(regex);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		boolean allIdStructuresValid = true;
		boolean allCodespacesValid = true;

		for (IdVersion id : localIds) {
			Matcher m = p.matcher(id.getId());
			if (!m.matches()) {
				// TODO add correct location
				validationReporter.addCheckPointReportError(context, _1_NETEX_INVALID_ID_STRUCTURE, DataLocationHelper.findDataLocation(id));
				log.error("Id " + id + " in line file have an invalid format. Correct format is " + regex);
				allIdStructuresValid = false;
			} else if (validPrefixes != null) {
				String prefix = m.group(1);
				if (!validPrefixes.contains(prefix)) {
					// TODO add correct location
					validationReporter.addCheckPointReportError(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE, DataLocationHelper.findDataLocation(id));
					log.error("Id " + id + " in line file are using an unaccepted codepsace prefix " + prefix + ". Valid prefixes are "
							+ ToStringBuilder.reflectionToString(validPrefixes, ToStringStyle.SIMPLE_STYLE));
					allCodespacesValid = false;
				}
			}
		}

		if (commonIds != null) {
			for (IdVersion id : commonIds.keySet()) {
				Matcher m = p.matcher(id.getId());
				if (!m.matches()) {
					for (String commonFileName : commonIds.get(id)) {
						// TODO add correct location
						validationReporter.addCheckPointReportError(context, _1_NETEX_INVALID_ID_STRUCTURE,
								DataLocationHelper.findDataLocation(commonFileName, id));
						log.error("Id " + id + " in common file file " + commonFileName + "have an invalid format. Correct format is " + regex);
						allIdStructuresValid = false;

					}

				} else if (validPrefixes != null) {
					String prefix = m.group(1);
					if (!validPrefixes.contains(prefix)) {
						for (String commonFileName : commonIds.get(id)) {
							// TODO add correct location
							validationReporter.addCheckPointReportError(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE,
									DataLocationHelper.findDataLocation(commonFileName, id));
							log.error("Id " + id + " in common file are using an unaccepted codepsace prefix " + prefix + ". Valid prefixes are "
									+ ToStringBuilder.reflectionToString(validPrefixes, ToStringStyle.SIMPLE_STYLE));
							allCodespacesValid = false;

						}

					}
				}
			}
		}
		if (allIdStructuresValid) {
			validationReporter.reportSuccess(context, _1_NETEX_INVALID_ID_STRUCTURE);
		}
		if (allCodespacesValid) {
			validationReporter.reportSuccess(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE);
		}
	}

	protected Set<IdVersion> collectEntityIdentificators(Context context, XPath xpath, Document dom, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "id", ignorableElementNames);
	}

	protected Set<IdVersion> collectEntityReferences(Context context, XPath xpath, Document dom, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		return collectIdOrRefWithVersion(context, xpath, dom, "ref", ignorableElementNames);
	}

	protected Set<IdVersion> collectIdOrRefWithVersion(Context context, XPath xpath, Document dom, String attributeName, Set<String> ignorableElementNames)
			throws XPathExpressionException {
		StringBuilder filterClause = new StringBuilder();
		filterClause.append("//n:*[");
		if (ignorableElementNames != null) {
			for (String elementName : ignorableElementNames) {
				filterClause.append("not(name()='" + elementName + "') and ");
			}
		}
		filterClause.append("@" + attributeName + "]");

		NodeList nodes = (NodeList) xpath.evaluate(filterClause.toString(), dom, XPathConstants.NODESET);
		Set<IdVersion> ids = new HashSet<IdVersion>();
		int idCount = nodes.getLength();
		for (int i = 0; i < idCount; i++) {
			Node n = nodes.item(i);
			String elementName = n.getNodeName();
			String id = n.getAttributes().getNamedItem(attributeName).getNodeValue();
			String version = null;
			Node versionAttribute = n.getAttributes().getNamedItem("version");
			if (versionAttribute != null) {
				version = versionAttribute.getNodeValue();
			}
			ids.add(new IdVersion(id, version, elementName,(String) context.get(Constant.FILE_NAME), (Integer) n.getUserData(PositionalXMLReader.LINE_NUMBER_KEY_NAME),
					(Integer) n.getUserData(PositionalXMLReader.COLUMN_NUMBER_KEY_NAME)));
			
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
