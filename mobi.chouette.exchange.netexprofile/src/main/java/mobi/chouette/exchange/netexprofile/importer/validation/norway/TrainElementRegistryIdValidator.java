package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.util.IdVersion;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;

import java.util.Set;
import java.util.stream.Collectors;

@Log4j
public class TrainElementRegistryIdValidator implements ExternalReferenceValidator {

    public static final String NAME = "TrainElementRegistryIdValidator";
	private static final String TRAIN_ELEMENT_CODESPACE = "PEN";

	@Override
    public Set<IdVersion> validateReferenceIds(Context context, Set<IdVersion> externalIds) {
        return externalIds.stream().filter(TrainElementRegistryIdValidator::isValidTrainElementId).collect(Collectors.toSet());
    }

    private static boolean isValidTrainElementId(IdVersion idVersion) {
    	String[] splittedId = idVersion.getId().split(":");
    	return splittedId.length == 3 &&  TRAIN_ELEMENT_CODESPACE.equals(splittedId[0]);
    }

    @Override
    public Set<IdVersion> isOfSupportedTypes(Set<IdVersion> externalIds) {
        return externalIds.stream().filter(e -> e.getElementName().contains("TrainElementRef") && e.getId().contains(":TrainElement:")).collect(Collectors.toSet());
    }

}
