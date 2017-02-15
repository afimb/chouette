package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.model.Company;
import mobi.chouette.model.Line;
import org.rutebanken.netex.model.Operator;
import org.rutebanken.netex.model.OrganisationsInFrame_RelStructure;
import org.rutebanken.netex.model.ResourceFrame;

import static mobi.chouette.exchange.netexprofile.exporter.ModelTranslator.netexId;
import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractNetexProducer.NETEX_DATA_OJBECT_VERSION;
import static mobi.chouette.exchange.netexprofile.exporter.producer.AbstractNetexProducer.netexFactory;
import static mobi.chouette.exchange.netexprofile.util.NetexObjectIdTypes.RESOURCE_FRAME_KEY;

public class ResourceFrameProducer implements FrameProducer<ResourceFrame> {

    private static OperatorProducer operatorProducer = new OperatorProducer();

    @Override
    public ResourceFrame produce(ExportableData data) {
        Line line = data.getLine();
        String resourceFrameId = netexId(line.objectIdPrefix(), RESOURCE_FRAME_KEY, line.objectIdSuffix());

        ResourceFrame resourceFrame = netexFactory.createResourceFrame()
                .withVersion(NETEX_DATA_OJBECT_VERSION)
                .withId(resourceFrameId);

        Company company = line.getCompany();
        Operator operator = operatorProducer.produce(company);

        OrganisationsInFrame_RelStructure organisationsStruct = netexFactory.createOrganisationsInFrame_RelStructure();
        organisationsStruct.getOrganisation_().add(netexFactory.createOperator(operator));
        resourceFrame.setOrganisations(organisationsStruct);

        return resourceFrame;
    }

}
