package mobi.chouette.exchange.netexprofile.exporter.producer;

import mobi.chouette.common.Context;
import mobi.chouette.common.TimeUtil;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.ConversionUtil;
import mobi.chouette.exchange.netexprofile.exporter.ExportableData;
import mobi.chouette.exchange.netexprofile.exporter.ExportableNetexData;
import mobi.chouette.model.Block;
import mobi.chouette.model.DeadRun;
import mobi.chouette.model.Timetable;
import mobi.chouette.model.VehicleJourney;
import org.rutebanken.netex.model.Block_VersionStructure;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DeadRunRefStructure;
import org.rutebanken.netex.model.JourneyRefs_RelStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.PointRefStructure;
import org.rutebanken.netex.model.PrivateCodeStructure;
import org.rutebanken.netex.model.VehicleJourneyRefStructure;

import javax.xml.bind.JAXBElement;

public class BlockProducer extends NetexProducer {

    public org.rutebanken.netex.model.Block produce(Context context, Block block) {
        ExportableData exportableData = (ExportableData) context.get(Constant.EXPORTABLE_DATA);
        ExportableNetexData exportableNetexData = (ExportableNetexData) context.get(Constant.EXPORTABLE_NETEX_DATA);

        org.rutebanken.netex.model.Block netexBlock = netexFactory.createBlock();
        NetexProducerUtils.populateId(block, netexBlock);

        // name
        if(block.getName() != null) {
            MultilingualString name = netexFactory.createMultilingualString();
            name.setValue(block.getDescription());
            netexBlock.setName(name);
        }

        // private code
        if(block.getPrivateCode() != null) {
            PrivateCodeStructure privateCodeStructure = netexFactory.createPrivateCodeStructure();
            privateCodeStructure.setValue(block.getPrivateCode());
            netexBlock.setPrivateCode(privateCodeStructure);
        }

        // description
        if(block.getDescription() != null) {
            MultilingualString description = netexFactory.createMultilingualString();
            description.setValue(block.getDescription());
            netexBlock.setDescription(description);
        }

        // start time
        if(block.getStartTime() != null) {
            netexBlock.setStartTime(TimeUtil.toLocalTimeFromJoda(block.getStartTime()));
        }

        // end time
        if(block.getEndTime() != null) {
            netexBlock.setEndTime(TimeUtil.toLocalTimeFromJoda(block.getEndTime()));
        }

        // end time day offset
        if(block.getEndTimeDayOffset() != null) {
            netexBlock.setEndTimeDayOffset(ConversionUtil.asBigInteger(block.getEndTimeDayOffset()));
        }

        // start point
        if(block.getStartPoint() != null) {
            PointRefStructure startPointRefStructure = netexFactory.createPointRefStructure();
            NetexProducerUtils.populateReference(block.getStartPoint(),startPointRefStructure, true);
            netexBlock.setStartPointRef(startPointRefStructure);
        }

        // end point
        if(block.getEndPoint() != null) {
            PointRefStructure endPointRefStructure = netexFactory.createPointRefStructure();
            NetexProducerUtils.populateReference(block.getEndPoint(),endPointRefStructure, true);
            netexBlock.setEndPointRef(endPointRefStructure);
        }

        // timetables
        if (!block.getTimetables().isEmpty()) {
            Block_VersionStructure.DayTypes daytypes = new Block_VersionStructure.DayTypes();
            netexBlock.setDayTypes(daytypes);
            for (Timetable t : block.getTimetables()) {
                if (exportableData.getTimetables().contains(t) || exportableNetexData.getSharedDayTypes().containsKey(t.getObjectId())) {
                    DayTypeRefStructure dayTypeRefStruct = netexFactory.createDayTypeRefStructure();
                    NetexProducerUtils.populateReference(t, dayTypeRefStruct, true);
                    JAXBElement<? extends DayTypeRefStructure> dayTypeRef = netexFactory.createDayTypeRef(dayTypeRefStruct);
                    netexBlock.getDayTypes().withDayTypeRef(dayTypeRef);
                }
            }
            if (netexBlock.getDayTypes().getDayTypeRef().isEmpty()) {
                throw new IllegalStateException("No exportable timetable data for block " + block.getObjectId());
            }
        } else {
            throw new IllegalStateException("Missing timetable data for block " + block.getObjectId());
        }

        // vehicle journeys and dead runs
        JourneyRefs_RelStructure journeyRefsRelStructure = netexFactory.createJourneyRefs_RelStructure();
        netexBlock.setJourneys(journeyRefsRelStructure);
        for (VehicleJourney vehicleJourney : block.getVehicleJourneys()) {
            if(vehicleJourney == null) {
                // Hibernate does not properly reorder elements in the vehicleJourneys collection
                // In case of a gap (due to the removal of an element), a null value is added in the collection.
                // This null value should be ignored
                continue;
            }
            VehicleJourneyRefStructure vehicleJourneyRefStructure = netexFactory.createVehicleJourneyRefStructure();
            vehicleJourneyRefStructure.setRef(vehicleJourney.getObjectId());
            NetexProducerUtils.populateReference(vehicleJourney, vehicleJourneyRefStructure, false);
            JAXBElement<?> vehicleJourneyRef = netexFactory.createVehicleJourneyRef(vehicleJourneyRefStructure);
            netexBlock.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator().add(vehicleJourneyRef);
        }
        for (DeadRun deadRun : block.getDeadRuns()) {
            if(deadRun == null) {
                // Hibernate does not properly reorder elements in the deadRuns collection
                // In case of a gap (due to the removal of an element), a null value is added in the collection.
                // This null value should be ignored
                continue;
            }
            DeadRunRefStructure deadRunRefStructure = netexFactory.createDeadRunRefStructure();
            deadRunRefStructure.setRef(deadRun.getObjectId());
            NetexProducerUtils.populateReference(deadRun, deadRunRefStructure, false);
            JAXBElement<?> deadRunRef = netexFactory.createDeadRunRef(deadRunRefStructure);
            netexBlock.getJourneys().getJourneyRefOrJourneyDesignatorOrServiceDesignator().add(deadRunRef);
        }


        return netexBlock;

    }
}
