package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.validation.report.CheckPoint;
import mobi.chouette.exchange.validation.report.ValidationReport;
import no.rutebanken.netex.model.DataManagedObjectStructure;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO consider merging this class with AbstractNetexProfileValidator
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
                    validationReport.addCheckPoint(
                            new CheckPoint(key, CheckPoint.RESULT.UNCHECK, CheckPoint.SEVERITY.WARNING));
                } else {
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
