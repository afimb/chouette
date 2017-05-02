package mobi.chouette.exchange.netexprofile.exporter;

import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.JobData;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;
import mobi.chouette.exchange.report.ActionReporter;
import mobi.chouette.exchange.report.IO_TYPE;
import mobi.chouette.model.Network;
import org.rutebanken.netex.model.Authority;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.ContactStructure;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static mobi.chouette.exchange.netexprofile.Constant.EXPORTABLE_NETEX_DATA;
import static mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducerUtils.netexId;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.AUTHORITY;

public class NetexSharedDataProducer extends NetexProducer implements Constant {

    private static final String SHARED_DATA_FILE_NAME = "_Shared-Data.xml";
    private static final String NSR_OBJECT_ID = "NSR:Authority:NSR";
    private static final String NSR_COMPANY_NUMBER = "917422575";
    private static final String NSR_NAME = "Nasjonal Stoppestedsregister";
    private static final String NSR_LEGAL_NAME = "NASJONAL STOPPESTEDSREGISTER";
    private static final String NSR_PHONE = "0047 236 20 000";

    public void produce(Context context) throws Exception {
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        JobData jobData = (JobData) context.get(JOB_DATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(EXPORTABLE_NETEX_DATA);

        Network firstOccurrenceNetwork = exportableData.getNetworks().iterator().next();
        AvailabilityCondition availabilityCondition = createAvailabilityCondition(firstOccurrenceNetwork);
        exportableNetexData.setCommonCondition(availabilityCondition);

        if (firstOccurrenceNetwork.getSourceIdentifier() != null && !firstOccurrenceNetwork.getSourceIdentifier().isEmpty()) {
            if (!exportableNetexData.getSharedAuthorities().containsKey(firstOccurrenceNetwork.getSourceIdentifier())) {
                Authority networkAuthority = createNetworkAuthority(firstOccurrenceNetwork);
                exportableNetexData.getSharedAuthorities().put(firstOccurrenceNetwork.getSourceIdentifier(), networkAuthority);
            } else {
                String version = firstOccurrenceNetwork.getObjectVersion() > 0 ? String.valueOf(firstOccurrenceNetwork.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION;
                String objectId = netexId(firstOccurrenceNetwork.objectIdPrefix(), AUTHORITY, firstOccurrenceNetwork.objectIdSuffix());
                Authority networkAuthority = createNetworkAuthority(version, objectId);
                exportableNetexData.getSharedAuthorities().put(objectId, networkAuthority);
            }
        }

        if (!exportableNetexData.getSharedAuthorities().containsKey(NSR_OBJECT_ID)) {
            Authority nsrAuthority = createNsrAuthority(firstOccurrenceNetwork);
            exportableNetexData.getSharedAuthorities().put(NSR_OBJECT_ID, nsrAuthority);
        }

        Path filePath = new File(outputPath.toFile(), SHARED_DATA_FILE_NAME).toPath();

        NetexFileWriter writer = new NetexFileWriter();
        writer.writeXmlFile(filePath, exportableData, exportableNetexData, NetexFragmentMode.SHARED);

        reporter.addFileReport(context, SHARED_DATA_FILE_NAME, IO_TYPE.OUTPUT);
    }

    private Authority createNetworkAuthority(Network network) {
        return createNetworkAuthority(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) :
                NETEX_DATA_OJBECT_VERSION, network.getSourceIdentifier());
    }

    private Authority createNetworkAuthority(String version, String objectId) {
        return netexFactory.createAuthority()
                .withVersion(version)
                .withId(objectId)
                .withCompanyNumber("999999999")
                .withName(getMultilingualString("Dummy Authority"))
                .withLegalName(getMultilingualString("DUMMY AUTHORITY"))
                .withContactDetails(createContactStructure("0047 999 99 999", "http://www.dummy-authority.org/"))
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
    }

    private Authority createNsrAuthority(Network network) {
        return netexFactory.createAuthority()
                .withVersion(network.getObjectVersion() > 0 ? String.valueOf(network.getObjectVersion()) : NETEX_DATA_OJBECT_VERSION)
                .withId(NSR_OBJECT_ID)
                .withCompanyNumber(NSR_COMPANY_NUMBER)
                .withName(getMultilingualString(NSR_NAME))
                .withLegalName(getMultilingualString(NSR_LEGAL_NAME))
                .withContactDetails(createContactStructure(NSR_PHONE, NSR_XMLNSURL))
                .withOrganisationType(OrganisationTypeEnumeration.AUTHORITY);
    }

    private ContactStructure createContactStructure(String phone, String url) {
        return netexFactory.createContactStructure()
                .withPhone(phone)
                .withUrl(url);
    }

}
