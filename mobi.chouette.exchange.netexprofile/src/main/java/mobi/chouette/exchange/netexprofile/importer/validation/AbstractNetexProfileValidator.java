package mobi.chouette.exchange.netexprofile.importer.validation;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.importer.util.DataLocationHelper;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.validation.report.ValidationReporter;
import mobi.chouette.model.Codespace;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j
public abstract class AbstractNetexProfileValidator implements Constant, NetexProfileValidator {

	public static final String _1_NETEX_SCHEMA_VALIDATION_ERROR = "1-NETEXPROFILE-SchemaValidationError";
	public static final String _1_NETEX_UNKNOWN_PROFILE = "1-NETEXPROFILE-UnknownProfile";
	public static final String _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES = "1-NETEXPROFILE-DuplicateIdentificatorsAcrossLineAndCommonFiles";
	public static final String _1_NETEX_DUPLICATE_IDS_ACROSS_COMMON_FILES = "1-NETEXPROFILE-DuplicateIdentificatorsAcrossCommonFiles";
	public static final String _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingVersionAttribute";
	public static final String _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS = "1-NETEXPROFILE-MissingReferenceVersionAttribute";
	public static final String _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS = "1-NETEXPROFILE-UnresolvedReferenceToCommonElements";
	public static final String _1_NETEX_INVALID_ID_STRUCTURE = "1-NETEXPROFILE-InvalidIdStructure";
	public static final String _1_NETEX_INVALID_ID_STRUCTURE_NAME = "1-NETEXPROFILE-InvalidIdStructureName";
	public static final String _1_NETEX_UNAPPROVED_CODESPACE_DEFINED = "1-NETEXPROFILE-UnapprovedCodespaceDefined";
	public static final String _1_NETEX_USE_OF_UNAPPROVED_CODESPACE = "1-NETEXPROFILE-UseOfUnapprovedCodespace";
	protected static final String OBJECT_IDS = "encountered_ids";
	public static final String _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES = "1-NETEXPROFILE-DuplicateIdentificatorsAcrossLineFiles";
	public static final String _1_NETEX_UNRESOLVED_EXTERNAL_REFERENCE = "1-NETEXPROFILE-UnresolvedExternalReference";

	
	private List<ExternalReferenceValidator> externalReferenceValidators = new ArrayList<>();
	
	@Override
	public void addExternalReferenceValidator(ExternalReferenceValidator v) {
		if(!externalReferenceValidators.contains(v)) {
			externalReferenceValidators.add(v);
		}
	}
	
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

	protected void validateElementPresent(Context context, XPath xpath, Node document, String expression, String checkPointKey)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() == 1) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: " + expression + " did not return 1 node");

			validationReporter.addCheckPointReportError(context, checkPointKey, DataLocationHelper.findDataLocation(context, document));
		}
	}

	protected void validateAtLeastElementPresent(Context context, XPath xpath, Node document, String expression, int count, String checkPointKey)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() >= count) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: " + expression + " did not return at least 1 node but " + nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				validationReporter.addCheckPointReportError(context, checkPointKey, DataLocationHelper.findDataLocation(context, nodes.item(i)));
			}
		}
	}

	protected void validateElementNotPresent(Context context, XPath xpath, Node document, String expression, String checkPointKey)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		validateCheckpointExists(context, checkPointKey, validationReporter);
		NodeList nodes = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		if (nodes.getLength() == 0) {
			validationReporter.reportSuccess(context, checkPointKey);
		} else {
			log.error("Checkpoint " + checkPointKey + " failed: " + expression + " should return 0 nodes, but returned " + nodes.getLength());
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

	public abstract void initializeCheckPoints(Context context);

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

	protected void verifyAcceptedCodespaces(Context context, XPath xpath, Node dom, Set<Codespace> acceptedCodespaces)
			throws XPathExpressionException {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		String referenceValue = StringUtils.join(acceptedCodespaces.stream()
				.map(codespace -> codespace.getXmlns() + "/" + codespace.getXmlnsUrl())
				.collect(Collectors.toList()), ' ');

		boolean onlyAcceptedCodespaces = true;
		NodeList codespaces = selectNodeSet("//n:Codespace", xpath, dom);
		for (int i = 0; i < codespaces.getLength(); i++) {
			Node n = codespaces.item(i);
			Codespace codespace = new Codespace();
			codespace.setXmlns((String) xpath.evaluate("n:Xmlns", n, XPathConstants.STRING));
			codespace.setXmlnsUrl((String) xpath.evaluate("n:XmlnsUrl", n, XPathConstants.STRING));

			Predicate<Codespace> equalsXmlns = (validCodespace) -> validCodespace.getXmlns().equals(codespace.getXmlns());
			Predicate<Codespace> equalsXmlnsUrl = (validCodespace) -> validCodespace.getXmlnsUrl().equals(codespace.getXmlnsUrl());

			if (acceptedCodespaces.stream().noneMatch(equalsXmlns.and(equalsXmlnsUrl))) {
				// TODO add correct location
				validationReporter.addCheckPointReportError(context, _1_NETEX_UNAPPROVED_CODESPACE_DEFINED, null,
						DataLocationHelper.findDataLocation(context, n), codespace.getXmlns() + "/" + codespace.getXmlnsUrl(), referenceValue);
				log.error("Codespace " + codespace + " is not accepted for this validation");
				onlyAcceptedCodespaces = false;
			}
		}

		if (onlyAcceptedCodespaces) {
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
			
			// Dont report on references that are supposed to be validated externally
			for(ExternalReferenceValidator v : externalReferenceValidators) {
				Set<IdVersion> ofSupportedTypes = v.isOfSupportedTypes(localRefs);
				unresolvedReferences.removeAll(ofSupportedTypes.stream().map(e -> e.getId()).collect(Collectors.toSet()));
			}
			
			Set<String> commonIdsWithoutVersion = commonIds.keySet().stream().map(e -> e.getId()).collect(Collectors.toSet());

			if (commonIdsWithoutVersion.size() > 0) {
				for (String localRef : unresolvedReferences) {
					if (!commonIdsWithoutVersion.contains(localRef)) {
						// TODO add faster lookup
						IdVersion id = null;
						for (IdVersion i : localRefs) {
							if (i.getId().equals(localRef)) {
								id = i;
								break;
							}
						}
						validationReporter.addCheckPointReportError(context, _1_NETEX_UNRESOLVED_REFERENCE_TO_COMMON_ELEMENTS, null,
								DataLocationHelper.findDataLocation(id), id.getId());
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

		boolean foundErrors = false;
		
		if (nonVersionedLocalRefs.size() > 0) {
			for (IdVersion id : nonVersionedLocalRefs) {
				if (localIdsWithoutVersion.contains(id.getId())) {
					foundErrors = true;
					validationReporter.addCheckPointReportError(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS, null,
							DataLocationHelper.findDataLocation(id), id.getId());
					log.error("Found local reference to " + id.getId() + " in line file without use of version-attribute");
				}
			}
		} 
		if(!foundErrors) {
			validationReporter.reportSuccess(context, _1_NETEX_MISSING_REFERENCE_VERSION_TO_LOCAL_ELEMENTS);

		}
	}

	protected void verifyUseOfVersionOnLocalElements(Context context, Set<IdVersion> localIds) {
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		Set<IdVersion> nonVersionedLocalIds = localIds.stream().filter(e -> e.getVersion() == null).collect(Collectors.toSet());
		if (nonVersionedLocalIds.size() > 0) {
			for (IdVersion id : nonVersionedLocalIds) {
				// TODO add correct location
				validationReporter.addCheckPointReportError(context, _1_NETEX_MISSING_VERSION_ON_LOCAL_ELEMENTS, null, DataLocationHelper.findDataLocation(id),
						id.getId());
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
						validationReporter.addCheckPointReportError(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_AND_COMMON_FILES, null,
								DataLocationHelper.findDataLocation(fileName, id), id.getId());

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
				validationReporter.addCheckPointReportError(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES, null, DataLocationHelper.findDataLocation(id),
						id.getId());
				log.error("Id " + id + " in line file have already been defined in another file");
			} else {
				alreadyFoundLocalIds.add(id);
			}
		}

		if (!duplicateFound) {
			validationReporter.reportSuccess(context, _1_NETEX_DUPLICATE_IDS_ACROSS_LINE_FILES);
		}
	}
	
	protected void verifyExternalRefs(Context context, Set<IdVersion> externalRefs, Set<IdVersion> localIds) {
		
		Set<IdVersion> possibleExternalReferences = externalRefs.stream().filter(e -> !localIds.contains(e)).collect(Collectors.toSet());
		
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();
		Set<IdVersion> unverifiedExternalRefs = new HashSet<>();
		
		for(ExternalReferenceValidator validator : externalReferenceValidators) {
			unverifiedExternalRefs.addAll(validator.validateReferenceIds(context,possibleExternalReferences));
		}
		
		if(unverifiedExternalRefs.size() > 0) {
			for(IdVersion id : unverifiedExternalRefs) {
				validationReporter.addCheckPointReportError(context, _1_NETEX_UNRESOLVED_EXTERNAL_REFERENCE, null,
						DataLocationHelper.findDataLocation(id), id.getId());
			}
			
		} else {
			validationReporter.reportSuccess(context, _1_NETEX_UNRESOLVED_EXTERNAL_REFERENCE);
		}
	}

	protected void verifyIdStructure(Context context, Set<IdVersion> localIds, String regex, Set<Codespace> validCodespaces) {
		Set<String> validPrefixes = null;
		if (validCodespaces != null) {
			validPrefixes = new HashSet<>();
			for (Codespace codespace : validCodespaces) {
				validPrefixes.add(codespace.getXmlns());
			}
		}

		Pattern p = Pattern.compile(regex);
		ValidationReporter validationReporter = ValidationReporter.Factory.getInstance();

		boolean allIdStructuresValid = true;
		boolean allIdStructuresNameValid = true;
		boolean allCodespacesValid = true;

		for (IdVersion id : localIds) {
			Matcher m = p.matcher(id.getId());
			if (!m.matches()) {
				validationReporter.addCheckPointReportError(context, _1_NETEX_INVALID_ID_STRUCTURE, null, DataLocationHelper.findDataLocation(id), id.getId());
				log.error("Id " + id + " in line file have an invalid format. Correct format is " + regex);
				allIdStructuresValid = false;
			} else {
				if (!m.group(2).equals(id.getElementName())) {
					String expectedId = m.group(1) + ":" + id.getElementName() + ":" + m.group(3);
					validationReporter.addCheckPointReportError(context, _1_NETEX_INVALID_ID_STRUCTURE_NAME, null, DataLocationHelper.findDataLocation(id),
							id.getId(), expectedId);
					log.error("Id " + id + " in file have an invalid format for the name part. Expected " + expectedId);
					allIdStructuresNameValid = false;
				}

				if (validPrefixes != null) {
					String prefix = m.group(1);
					if (!validPrefixes.contains(prefix)) {
						validationReporter.addCheckPointReportError(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE, null,
								DataLocationHelper.findDataLocation(id), id.getId());
						log.error("Id " + id + " in file are using an unaccepted codepsace prefix " + prefix + ". Valid prefixes are "
								+ ToStringBuilder.reflectionToString(validPrefixes, ToStringStyle.SIMPLE_STYLE));
						allCodespacesValid = false;
					}
				}
			}
		}

	
		if (allIdStructuresValid) {
			validationReporter.reportSuccess(context, _1_NETEX_INVALID_ID_STRUCTURE);
		}
		if (allIdStructuresNameValid) {
			validationReporter.reportSuccess(context, _1_NETEX_INVALID_ID_STRUCTURE_NAME);
		}
		if (allCodespacesValid) {
			validationReporter.reportSuccess(context, _1_NETEX_USE_OF_UNAPPROVED_CODESPACE);
		}
	}


}
