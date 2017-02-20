package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.model.Period;
import org.apache.commons.collections.CollectionUtils;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.netex.model.ValidityConditions_RelStructure;

import javax.xml.bind.JAXBElement;
import java.util.List;

public class NetexParser implements Constant {

    public static void resetContext(Context context) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        if (parsingContext != null) {
            for (String key : parsingContext.keySet()) {
                Context localContext = (Context) parsingContext.get(key);
                localContext.clear();
            }
        }
    }

    static Context getLocalContext(Context context, String localContextName) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        if (parsingContext == null) {
            parsingContext = new Context();
            context.put(PARSING_CONTEXT, parsingContext);
        }

        Context localContext = (Context) parsingContext.get(localContextName);
        if (localContext == null) {
            localContext = new Context();
            parsingContext.put(localContextName, localContext);
        }

        return localContext;
    }

    static Context getObjectContext(Context context, String localContextName, String objectId) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        if (parsingContext == null) {
            parsingContext = new Context();
            context.put(PARSING_CONTEXT, parsingContext);
        }

        Context localContext = (Context) parsingContext.get(localContextName);
        if (localContext == null) {
            localContext = new Context();
            parsingContext.put(localContextName, localContext);
        }

        Context objectContext = (Context) localContext.get(objectId);
        if (objectContext == null) {
            objectContext = new Context();
            localContext.put(objectId, objectContext);
        }

        return objectContext;
    }

    ValidBetween getValidBetween(EntityInVersionStructure entityStruct) throws Exception {
        if (entityStruct == null) {
            return null;
        }

        ValidBetween validBetween = null;

        if (entityStruct.getValidityConditions() != null) {
            validBetween = getValidBetween(entityStruct.getValidityConditions());
        } else if (entityStruct.getValidBetween() != null) {
            validBetween = getValidBetween(entityStruct.getValidBetween());
        }

        return validBetween;
    }

    @SuppressWarnings("unchecked")
    ValidBetween getValidBetween(ValidityConditions_RelStructure validityConditionStruct) throws Exception {
        if (validityConditionStruct == null) {
            return null;
        }

        ValidBetween validBetween = null;
        List<Object> validityConditionElements = validityConditionStruct.getValidityConditionRefOrValidBetweenOrValidityCondition_();

        if (CollectionUtils.isNotEmpty(validityConditionElements)) {
            JAXBElement<?> validityConditionElement = (JAXBElement<?>) validityConditionElements.get(0);

            if(validityConditionElement.getValue() instanceof AvailabilityCondition) {
                AvailabilityCondition availabilityCondition = ((JAXBElement<AvailabilityCondition>) validityConditionElement).getValue();
                validBetween = new ValidBetween()
                        .withFromDate(availabilityCondition.getFromDate())
                        .withToDate(availabilityCondition.getToDate());
            } else {
                throw new RuntimeException("Only support AvailabilityCondition as validityCondition");
            }
        }

        return validBetween;
    }

    ValidBetween getValidBetween(List<ValidBetween> validBetweenList) throws Exception {
        if (CollectionUtils.isEmpty(validBetweenList)) {
            return null;
        }

        return validBetweenList.get(0);
    }

    boolean isPeriodEmpty(Period period) {
        return period.getStartDate() == null && period.getEndDate() == null;
    }

}
