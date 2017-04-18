package mobi.chouette.exchange.netexprofile.parser;

import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import mobi.chouette.exchange.netexprofile.util.NetexObjectUtil;
import mobi.chouette.exchange.netexprofile.util.NetexReferential;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBElement;
import java.time.OffsetDateTime;

public class ServiceCalendarParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "ServiceCalendar";
    static final String VALID_BETWEEN = "validBetween";

    @Override
    public void parse(Context context) throws Exception {
        NetexReferential netexReferential = (NetexReferential) context.get(NETEX_REFERENTIAL);
        ServiceCalendarFrame serviceCalendarFrame = (ServiceCalendarFrame) context.get(NETEX_LINE_DATA_CONTEXT);
        ValidBetween validBetween = getValidBetweenForFrame(context);

        if (serviceCalendarFrame.getDayTypes() != null) {
            for (JAXBElement<? extends DataManagedObjectStructure> dayTypeElement : serviceCalendarFrame.getDayTypes().getDayType_()) {
                DayType dayType = (DayType) dayTypeElement.getValue();
                NetexObjectUtil.addDayTypeRef(netexReferential, dayType.getId(), dayType);
                addValidBetween(context, dayType.getId(), validBetween);
            }
        }
        if (serviceCalendarFrame.getDayTypeAssignments() != null) {
            for (DayTypeAssignment dayTypeAssignment : serviceCalendarFrame.getDayTypeAssignments().getDayTypeAssignment()) {
                String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();
                NetexObjectUtil.addDayTypeAssignmentRef(netexReferential, dayTypeIdRef, dayTypeAssignment);
            }
        }
        if (serviceCalendarFrame.getOperatingPeriods() != null) {
            for (OperatingPeriod_VersionStructure operatingPeriodStruct : serviceCalendarFrame.getOperatingPeriods().getOperatingPeriodOrUicOperatingPeriod()) {
                OperatingPeriod operatingPeriod = (OperatingPeriod) operatingPeriodStruct;
                NetexObjectUtil.addOperatingPeriodRef(netexReferential, operatingPeriod.getId(), operatingPeriod);
            }
        }
        if (serviceCalendarFrame.getOperatingDays() != null) {
            for (OperatingDay operatingDay : serviceCalendarFrame.getOperatingDays().getOperatingDay()) {
                NetexObjectUtil.addOperatingDayRef(netexReferential, operatingDay.getId(), operatingDay);
            }
        }

        if (serviceCalendarFrame.getServiceCalendar() != null) {
            ServiceCalendar serviceCalendar = serviceCalendarFrame.getServiceCalendar();
            ValidBetween calendarValidBetween = getValidBetween(context, serviceCalendar);

            if (serviceCalendar.getDayTypes() != null) {
                for (JAXBElement<?> dayTypeElement : serviceCalendar.getDayTypes().getDayTypeRefOrDayType_()) {
                    DayType dayType = (DayType) dayTypeElement.getValue();
                    NetexObjectUtil.addDayTypeRef(netexReferential, dayType.getId(), dayType);
                    addValidBetween(context, dayType.getId(), calendarValidBetween);
                }
            }
            if (serviceCalendar.getDayTypeAssignments() != null) {
                for (DayTypeAssignment dayTypeAssignment : serviceCalendar.getDayTypeAssignments().getDayTypeAssignment()) {
                    String dayTypeIdRef = dayTypeAssignment.getDayTypeRef().getValue().getRef();
                    NetexObjectUtil.addDayTypeAssignmentRef(netexReferential, dayTypeIdRef, dayTypeAssignment);
                }
            }
            if (serviceCalendar.getOperatingPeriods() != null) {
                for (Object genericOperatingPeriod : serviceCalendar.getOperatingPeriods().getOperatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod()) {
                    OperatingPeriod operatingPeriod = (OperatingPeriod) genericOperatingPeriod;
                    NetexObjectUtil.addOperatingPeriodRef(netexReferential, operatingPeriod.getId(), operatingPeriod);
                }
            }
            if (serviceCalendar.getOperatingDays() != null) {
                for (Object genericOperatingDay : serviceCalendarFrame.getServiceCalendar().getOperatingDays().getOperatingDayRefOrOperatingDay()) {
                    OperatingDay operatingDay = (OperatingDay) genericOperatingDay;
                    NetexObjectUtil.addOperatingDayRef(netexReferential, operatingDay.getId(), operatingDay);
                }
            }
        }
    }

    private ValidBetween getValidBetweenForFrame(Context context) {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

        ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
        if (calendarFrameValidity != null) {
            return calendarFrameValidity;
        } else {
            ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
            if (compositeFrameValidity != null) {
                return compositeFrameValidity;
            }
        }

        return null;
    }

    private ValidBetween getValidBetween(Context context, ServiceCalendar serviceCalendar) throws Exception {
        Context parsingContext = (Context) context.get(PARSING_CONTEXT);
        Context publicationDeliveryContext = (Context) parsingContext.get(PublicationDeliveryParser.LOCAL_CONTEXT);

        if (serviceCalendar.getFromDate() != null && serviceCalendar.getToDate() != null) {
            OffsetDateTime fromDateTime = serviceCalendar.getFromDate();
            OffsetDateTime toDateTime = serviceCalendar.getToDate();
            return new ValidBetween().withFromDate(fromDateTime).withToDate(toDateTime);
        } else {
            ValidBetween entityValidity = getValidBetween(serviceCalendar);
            if (entityValidity != null) {
                return entityValidity;
            } else {
                ValidBetween calendarFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.SERVICE_CALENDAR_FRAME);
                if (calendarFrameValidity != null) {
                    return calendarFrameValidity;
                } else {
                    ValidBetween compositeFrameValidity = (ValidBetween) publicationDeliveryContext.get(PublicationDeliveryParser.COMPOSITE_FRAME);
                    if (compositeFrameValidity != null) {
                        return compositeFrameValidity;
                    }
                }
            }
        }

        return null;
    }

    private void addValidBetween(Context context, String objectId, ValidBetween validBetween) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(VALID_BETWEEN, validBetween);
    }

    static {
        ParserFactory.register(ServiceCalendarParser.class.getName(), new ParserFactory() {
            private ServiceCalendarParser instance = new ServiceCalendarParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
