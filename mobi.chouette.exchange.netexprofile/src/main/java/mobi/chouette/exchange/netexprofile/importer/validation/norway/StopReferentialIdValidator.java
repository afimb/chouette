package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidator;
import mobi.chouette.exchange.netexprofile.importer.validation.ExternalReferenceValidatorFactory;

@Log4j
public class StopReferentialIdValidator implements ExternalReferenceValidator {

	public static final String NAME = "StopReferentialIdValidator";

	@Override
	public Set<String> validateReferenceIds(Set<String> externalIds) {

		log.warn("About to validate external ids: "+ToStringBuilder.reflectionToString(externalIds, ToStringStyle.NO_FIELD_NAMES_STYLE));

		Set<String> validIds = externalIds.stream().filter(e -> e.contains(":Quay:") || e.contains(":StopPlace:")).collect(Collectors.toSet());
		
		log.warn("Ids deemed ok without actual check: "+ToStringBuilder.reflectionToString(validIds, ToStringStyle.NO_FIELD_NAMES_STYLE));
		
		return validIds;
	}
	
	
	public static class DefaultExternalReferenceValidatorFactory extends ExternalReferenceValidatorFactory {
		@Override
		protected ExternalReferenceValidator create(Context context) {
			ExternalReferenceValidator instance = (ExternalReferenceValidator) context.get(NAME);
			if (instance == null) {
				instance = new StopReferentialIdValidator();
				context.put(NAME, instance);
			}
			return instance;
		}
	}

	static {
		ExternalReferenceValidatorFactory.factories.put(StopReferentialIdValidator.class.getName(),
				new StopReferentialIdValidator.DefaultExternalReferenceValidatorFactory());
	}


}
