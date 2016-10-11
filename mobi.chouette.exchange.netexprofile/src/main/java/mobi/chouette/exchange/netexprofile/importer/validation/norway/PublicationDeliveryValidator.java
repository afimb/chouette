package mobi.chouette.exchange.netexprofile.importer.validation.norway;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.importer.NetexprofileImportParameters;
import mobi.chouette.exchange.netexprofile.importer.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.importer.util.NetexReferential;
import mobi.chouette.exchange.netexprofile.importer.validation.NetexNamespaceContext;
import mobi.chouette.exchange.netexprofile.parser.OrganisationParser;
import mobi.chouette.exchange.validation.ValidationConstraints;
import mobi.chouette.exchange.validation.ValidationException;
import mobi.chouette.exchange.validation.Validator;
import mobi.chouette.exchange.validation.ValidatorFactory;
import mobi.chouette.exchange.validation.report.Detail;
import no.rutebanken.netex.model.*;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j
public class PublicationDeliveryValidator extends AbstractValidator implements Validator<PublicationDeliveryStructure> {

    private static final String _1_NETEX_RESOURCEFRAME = "1-NETEX-RESOURCEFRAME";
    private static final String _1_NETEX_SITEFRAME = "1-NETEX-SITEFRAME";
    private static final String _1_NETEX_SERVICEFRAME = "1-NETEX-SERVICEFRAME";
    private static final String _1_NETEX_SERVICECALENDARFRAME = "1-NETEX-SERVICECALENDARFRAME";
    private static final String _1_NETEX_TIMETABLEFRAME = "1-NETEX-TIMETABLEFRAME";

    @Override
    protected void initializeCheckPoints(Context context) {

    }

    @Override
    public void addObjectReference(Context context, DataManagedObjectStructure object) {

    }

    @Override
    public ValidationConstraints validate(Context context, PublicationDeliveryStructure target) throws ValidationException {
        // TODO move xpath instantiation to higher level in validation hierarchy
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NetexNamespaceContext());
        context.put(NETEX_LINE_DATA_XPATH, xpath);

        NetexprofileImportParameters configuration = (NetexprofileImportParameters) context.get(CONFIGURATION);
        PublicationDeliveryStructure lineDeliveryStructure = (PublicationDeliveryStructure) context.get(NETEX_LINE_DATA_JAVA);
        Document dom = (Document) context.get(NETEX_LINE_DATA_DOM);

        NetexReferential referential = (NetexReferential) context.get(NETEX_REFERENTIAL);

        // TODO consider making reusable in NetexObjectUtil
        PublicationDeliveryStructure.DataObjects dataObjects = lineDeliveryStructure.getDataObjects();
        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = dataObjects.getCompositeFrameOrCommonFrame();

        // TODO consider if its better to initialize referentials to frames also, for use both by parsers and validators, like this
/*
        Collection<ResourceFrame> resourceFrames = referential.getResourceFrames().values();
        if (isListEmpty(resourceFrames)) {
            System.out.println("Add validation error...");
        } else {
            for (ResourceFrame resourceFrame : resourceFrames) {
                System.out.println("Validate resource frame...");
            }
        }
*/

        try {
            // 1. validate resource frame (how many frames acceptable/mandatory)
            prepareCheckPoint(context, _1_NETEX_RESOURCEFRAME);
            if (isElementPresent(context, "//n:ResourceFrame")) {
                // TODO validate resource frame elements
                List<ResourceFrame> resourceFrames = NetexObjectUtil.getFrames(ResourceFrame.class, compositeFrameOrCommonFrame);
                ResourceFrame resourceFrame = resourceFrames.get(0);

                // validate data sources
                DataSourcesInFrame_RelStructure dataSourcesStruct = resourceFrame.getDataSources();
                if (dataSourcesStruct != null) {
                    // TODO validate data sources
                    //List<DataSource> dataSources = dataSourcesStruct.getDataSource();
                }

                // validate responsibility sets
                ResponsibilitySetsInFrame_RelStructure responsibilitySetsStruct = resourceFrame.getResponsibilitySets();
                if (responsibilitySetsStruct != null) {
                    // TODO validate
                }

                TypesOfValueInFrame_RelStructure typesOfValueStruct = resourceFrame.getTypesOfValue();
                if (typesOfValueStruct != null) {
                    // TODO validate
                }

                // validate organisations
                OrganisationValidator organisationValidator = (OrganisationValidator) ValidatorFactory.create(OrganisationValidator.class.getName(), context);
                organisationValidator.validate(context, null);
            } else {
                Detail errorItem = new Detail(_1_NETEX_RESOURCEFRAME, null, "No ResourceFrame");
                addValidationError(context, _1_NETEX_RESOURCEFRAME, errorItem);
            }

            // 2. validate site frame
            prepareCheckPoint(context, _1_NETEX_SITEFRAME);
            if (isElementPresent(context, "//n:SiteFrame")) {
                // TODO validate site frame elements
                List<SiteFrame> siteFrames = NetexObjectUtil.getFrames(SiteFrame.class, compositeFrameOrCommonFrame);
                SiteFrame siteFrame = siteFrames.get(0);
            } else {
                Detail errorItem = new Detail(_1_NETEX_SITEFRAME, null, "No SiteFrame");
                addValidationError(context, _1_NETEX_SITEFRAME, errorItem);
            }

            // 3. validate service frame
            prepareCheckPoint(context, _1_NETEX_SERVICEFRAME);
            if (isElementPresent(context, "//n:ServiceFrame")) {
                // TODO validate service frame elements
            } else {
                Detail errorItem = new Detail(_1_NETEX_SERVICEFRAME, null, "No ServiceFrame");
                addValidationError(context, _1_NETEX_SERVICEFRAME, errorItem);
            }

            // 4. validate service calendar frame
            prepareCheckPoint(context, _1_NETEX_SERVICECALENDARFRAME);
            if (isElementPresent(context, "//n:ServiceCalendarFrame")) {
                // TODO validate service frame elements
            } else {
                Detail errorItem = new Detail(_1_NETEX_SERVICECALENDARFRAME, null, "No ServiceCalendarFrame");
                addValidationError(context, _1_NETEX_SERVICECALENDARFRAME, errorItem);
            }

            // 5. validate timetable frame
            prepareCheckPoint(context, _1_NETEX_TIMETABLEFRAME);
            if (isElementPresent(context, "//n:TimetableFrame")) {
                // TODO validate timetable frame elements
            } else {
                Detail errorItem = new Detail(_1_NETEX_TIMETABLEFRAME, null, "No TimetableFrame");
                addValidationError(context, _1_NETEX_TIMETABLEFRAME, errorItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ValidationConstraints();
    }

}
