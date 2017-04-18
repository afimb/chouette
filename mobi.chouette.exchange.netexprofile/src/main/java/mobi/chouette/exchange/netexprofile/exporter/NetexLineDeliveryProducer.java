package mobi.chouette.exchange.netexprofile.exporter;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.exchange.netexprofile.exporter.producer.NetexProducer;

@Log4j
public class NetexLineDeliveryProducer extends NetexProducer implements Constant {

/*
    public static final String NETEX_DATA_OJBECT_VERSION = "1";

    private static final String NETEX_PROFILE_VERSION = "1.04:NO-NeTEx-networktimetable:1.0";
    private static final String DEFAULT_ZONE_ID = "UTC";
    private static final String DEFAULT_LANGUAGE_CODE = "no";
    private static final String NSR_XMLNS = "NSR";
    private static final String NSR_XMLNSURL = "http://www.rutebanken.org/ns/nsr";

    private final static DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart().appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true).optionalEnd()
            .optionalStart().appendPattern("XXXXX")
            .optionalEnd()
            .parseDefaulting(ChronoField.OFFSET_SECONDS,OffsetDateTime.now().getLong(ChronoField.OFFSET_SECONDS) ).toFormatter();

    private static OperatorProducer operatorProducer = new OperatorProducer();
    private static StopPlaceProducer stopPlaceProducer = new StopPlaceProducer();
    private static NetworkProducer networkProducer = new NetworkProducer();
    private static LineProducer lineProducer = new LineProducer();
    private static RouteProducer routeProducer = new RouteProducer();
    private static JourneyPatternProducer journeyPatternProducer = new JourneyPatternProducer();
    private static CalendarProducer calendarProducer = new CalendarProducer();
    private static ServiceJourneyProducer serviceJourneyProducer = new ServiceJourneyProducer();

    private static final Map<String, Codespace> codespaceMapping = new HashMap<>();

    private static final String DEFAULT_NAMESPACE = "http://www.netex.org.uk/netex";
    private static final String VERSION = "version";
    private static final String ID = "id";
    private static final String CREATED = "created";
    private static final String XMLNS = "Xmlns";
    private static final String XMLNSURL = "XmlnsUrl";

    private static NetexXmlStreamMarshaller marshaller;

    @Getter
    private Set<Codespace> codespaces = new HashSet<>();

    static {
        try {
            Properties properties = new Properties();
            properties.load(NetexLineDeliveryProducer.class.getResourceAsStream("/codespaces.properties"));
            Set<String> propertyKeys = properties.stringPropertyNames();

            for (String key : propertyKeys) {
                Codespace codespace = netexFactory.createCodespace()
                        .withId(key.toLowerCase())
                        .withXmlns(key)
                        .withXmlnsUrl(properties.getProperty(key));
                codespaceMapping.put(key, codespace);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load codespaces from file");
        }

        // TODO could be an idea to put the marshaller in context
        try {
            marshaller = NetexXmlStreamMarshaller.getInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create marshaller instance");
        }
    }

    public void produce(Context context) throws Exception {
        // TODO everything here should be moved to calling class, on a higher level, because we must make the writers more generic,
        // TODO because we are now writing both line and shared data.
        NetexprofileExportParameters configuration = (NetexprofileExportParameters) context.get(CONFIGURATION);
        ActionReporter reporter = ActionReporter.Factory.getInstance();
        ExportableData exportableData = (ExportableData) context.get(EXPORTABLE_DATA);
        JobData jobData = (JobData) context.get(JOB_DATA);
        Metadata metadata = (Metadata) context.get(METADATA);
        Path outputPath = Paths.get(jobData.getPathName(), OUTPUT);

        initializeCodespaces(configuration, exportableData);
        collectStopAreas(context, exportableData);

        Line line = exportableData.getLine();
        String fileName = line.getObjectId().replaceAll(":", "-") + (line.getNumber() != null ? line.getNumber() + "-" : "") + (line.getPublishedName() != null ? "-" + line.getPublishedName().replace(' ', '_') : "") + ".xml";
        Path filePath = new File(outputPath.toFile(), fileName).toPath();
        writeToXml(context, filePath, exportableData);

        reporter.addFileReport(context, fileName, IO_TYPE.OUTPUT);

        if (metadata != null) {
            metadata.getResources().add(metadata.new Resource(
                    fileName,
                    NeptuneObjectPresenter.getName(line.getNetwork()),
                    NeptuneObjectPresenter.getName(line)));
        }
    }

    // TODO move up the hierarchy, to where exportable data is collected
    private void collectStopAreas(Context context, ExportableData exportableData) {
        Referential referential = (Referential) context.get(REFERENTIAL);

        Set<mobi.chouette.model.StopArea> stopAreas = new HashSet<>();
        stopAreas.addAll(exportableData.getStopPlaces());
        stopAreas.addAll(exportableData.getCommercialStops());

        for (mobi.chouette.model.StopArea stopArea : stopAreas) {
            if (!referential.getSharedStopAreas().containsKey(stopArea.getObjectId())) {
                referential.getSharedStopAreas().put(stopArea.getObjectId(), stopArea);
            }
        }
    }

    private void initializeCodespaces(NetexprofileExportParameters configuration, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();

        Codespace nsrCodespace = netexFactory.createCodespace()
                .withId(NSR_XMLNS.toLowerCase())
                .withXmlns(NSR_XMLNS)
                .withXmlnsUrl(NSR_XMLNSURL);

        Codespace operatorCodespace = null;
        if (configuration.getValidCodespaces() != null) {
            Map<String, Codespace> validCodespaces = new HashMap<>();
            String[] validCodespacesTuples = StringUtils.split(configuration.getValidCodespaces(), ",");

            for (int i = 0; i < validCodespacesTuples.length; i += 2) {
                Codespace codespace = netexFactory.createCodespace()
                        .withId(validCodespacesTuples[i].toLowerCase())
                        .withXmlns(validCodespacesTuples[i])
                        .withXmlnsUrl(validCodespacesTuples[i + 1]);
                validCodespaces.put(validCodespacesTuples[i].toUpperCase(), codespace);
            }
            if (validCodespaces.containsKey(line.objectIdPrefix().toUpperCase())) {
                operatorCodespace = validCodespaces.get(line.objectIdPrefix().toUpperCase());
            }
        } else {
            if (codespaceMapping.containsKey(line.objectIdPrefix().toUpperCase())) {
                operatorCodespace = codespaceMapping.get(line.objectIdPrefix().toUpperCase());
            } else {
                throw new RuntimeException("Unknown operator codespace");
            }
        }
        if (!codespaces.isEmpty()) {
            codespaces.clear();
        }
        codespaces.addAll(Arrays.asList(operatorCodespace, nsrCodespace));
    }

    private void writeToXml(Context context, Path path, ExportableData exportableData) throws IOException, XMLStreamException {
        try (Writer bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, CREATE, APPEND)) {
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            //outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", Boolean.TRUE);
            XMLStreamWriter xmlStreamWriter = null;

            try {
                xmlStreamWriter = outputFactory.createXMLStreamWriter(bufferedWriter);
                xmlStreamWriter.setDefaultNamespace(DEFAULT_NAMESPACE);
                //xmlStreamWriter.setNamespaceContext(namespaces);

                IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(new EscapingXMLStreamWriter(xmlStreamWriter));
                writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");
                writePublicationDeliveryElement(context, writer, exportableData);
            } finally {
                if (xmlStreamWriter != null) {
                    try {
                        //xmlStreamWriter.writeCharacters("\n");
                        xmlStreamWriter.writeEndDocument();
                        xmlStreamWriter.flush();
                        xmlStreamWriter.close();
                    } catch (XMLStreamException e) {
                        log.error("Could not close XML writer", e);
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            log.error("Could not produce XML file", e);
            throw new RuntimeException(e);
        }
    }

    private void writePublicationDeliveryElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String timestampFormatted = formatter.format(timestamp);

        try {
            writer.writeStartElement(PUBLICATION_DELIVERY);

            writer.writeDefaultNamespace(DEFAULT_NAMESPACE);
            writer.writeNamespace("ns2", "http://www.opengis.net/gml/3.2");
            writer.writeNamespace("ns3", "http://www.siri.org.uk/siri");
            writer.writeAttribute(VERSION, NETEX_PROFILE_VERSION);

            writeElement(writer, PUBLICATION_TIMESTAMP, timestampFormatted);
            writeElement(writer, PARTICIPANT_REF, NSR_XMLNS);
            writeElement(writer, DESCRIPTION, exportableData.getLine().getName());
            writeDataObjectsElement(context, writer, exportableData, timestampFormatted);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDataObjectsElement(Context context, XMLStreamWriter writer, ExportableData exportableData, String timestamp) {
        try {
            writer.writeStartElement(DATA_OBJECTS);
            writeCompositeFrameElement(context, writer, exportableData, timestamp);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCompositeFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData, String timestamp) {
        mobi.chouette.model.Line line = exportableData.getLine();

        try {
            writer.writeStartElement(COMPOSITE_FRAME);

            if (line.getNetwork().getVersionDate() != null) {
                OffsetDateTime createdDateTime = NetexProducerUtils.toOffsetDateTime(line.getNetwork().getVersionDate());
                writer.writeAttribute(CREATED, formatter.format(createdDateTime));
            } else {
                writer.writeAttribute(CREATED, timestamp);
            }

            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, netexId(line.objectIdPrefix(), COMPOSITE_FRAME, line.objectIdSuffix()));

            writeValidityConditionsElement(writer, line);
            writeCodespacesElement(writer);
            writeFrameDefaultsElement(writer);
            writeFramesElement(context, writer, exportableData);

            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeValidityConditionsElement(XMLStreamWriter writer, mobi.chouette.model.Line line) {
        AvailabilityCondition availabilityCondition = createAvailabilityCondition(line);

        try {
            writer.writeStartElement(VALIDITY_CONDITIONS);
            marshaller.marshal(netexFactory.createAvailabilityCondition(availabilityCondition), writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCodespacesElement(XMLStreamWriter writer) {
        try {
            writer.writeStartElement(CODESPACES);

            for (Codespace codespace : codespaces) {
                writeCodespaceElement(writer, codespace);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCodespaceElement(XMLStreamWriter writer, Codespace codespace) {
        try {
            writer.writeStartElement(CODESPACE);
            writer.writeAttribute(ID, codespace.getId());
            writeElement(writer, XMLNS, codespace.getXmlns());
            writeElement(writer, XMLNSURL, codespace.getXmlnsUrl());
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFrameDefaultsElement(XMLStreamWriter writer) {
        try {
            writer.writeStartElement(FRAME_DEFAULTS);
            writer.writeStartElement(DEFAULT_LOCALE);
            writeElement(writer, TIME_ZONE, DEFAULT_ZONE_ID);
            writeElement(writer, DEFAULT_LANGUAGE, DEFAULT_LANGUAGE_CODE);
            writer.writeEndElement();
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFramesElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        try {
            writer.writeStartElement(FRAMES);
            writeResourceFrameElement(context, writer, exportableData);
            writeSiteFrameElement(context, writer, exportableData);
            writeServiceFrameElement(context, writer, exportableData);
            writeServiceCalendarFrameElement(context, writer, exportableData);
            writeTimetableFrameElement(context, writer, exportableData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResourceFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String resourceFrameId = netexId(line.objectIdPrefix(), RESOURCE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(RESOURCE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, resourceFrameId);
            writeOrganisationsElement(context, writer, exportableData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeSiteFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String siteFrameId = netexId(line.objectIdPrefix(), SITE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(SITE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, siteFrameId);
            writeStoPlacesElement(context, writer, exportableData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeServiceFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String serviceFrameId = netexId(line.objectIdPrefix(), SERVICE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(SERVICE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceFrameId);
            writeNetworkElement(context, writer, line);
            writeRoutePointsElement(writer, line);
            writeRoutesElement(context, writer, line);
            writeLinesElement(context, writer, line);
            writeScheduledStopPointsElement(writer, line);
            writeStopAssignmentsElement(writer, line);
            writeJourneyPatternsElement(context, writer, line);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeServiceCalendarFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String serviceCalendarFrameId = netexId(line.objectIdPrefix(), SERVICE_CALENDAR_FRAME, line.objectIdSuffix());

        Map<String, List<? extends DataManagedObjectStructure>> calendarData = calendarProducer.produce(context, exportableData);
        List<DayType> dayTypes = (List<DayType>) calendarData.get(DAY_TYPES_KEY);
        List<DayTypeAssignment> dayTypeAssignments = (List<DayTypeAssignment>) calendarData.get(DAY_TYPE_ASSIGNMENTS_KEY);
        List<OperatingPeriod> operatingPeriods = (List<OperatingPeriod>) calendarData.get(OPERATING_PERIODS_KEY);

        try {
            writer.writeStartElement(SERVICE_CALENDAR_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, serviceCalendarFrameId);
            writeDayTypesElement(writer, dayTypes);

            if (CollectionUtils.isNotEmpty(operatingPeriods)) {
                writeOperatingPeriodsElement(writer, operatingPeriods);
            }

            writeDayTypeAssignmentsElement(writer, dayTypeAssignments);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTimetableFrameElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        mobi.chouette.model.Line line = exportableData.getLine();
        String timetableFrameId = netexId(line.objectIdPrefix(), TIMETABLE_FRAME, line.objectIdSuffix());

        try {
            writer.writeStartElement(TIMETABLE_FRAME);
            writer.writeAttribute(VERSION, NETEX_DATA_OJBECT_VERSION);
            writer.writeAttribute(ID, timetableFrameId);
            writeVehicleJourneysElement(context, writer, exportableData);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOrganisationsElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        Company company = exportableData.getLine().getCompany();
        List<Operator> operators = Collections.singletonList(operatorProducer.produce(context, company));

        try {
            writer.writeStartElement(ORGANISATIONS);

            for (Operator operator : operators) {
                marshaller.marshal(netexFactory.createOperator(operator), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeStoPlacesElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        Set<mobi.chouette.model.StopArea> stopAreas = new HashSet<>();
        stopAreas.addAll(exportableData.getStopPlaces());
        stopAreas.addAll(exportableData.getCommercialStops());
        List<StopPlace> stopPlaces = new ArrayList<>();

        for (mobi.chouette.model.StopArea stopArea : stopAreas) {
            StopPlace stopPlace = stopPlaceProducer.produce(context, stopArea);
            stopPlaces.add(stopPlace);
        }
        try {
            writer.writeStartElement(STOP_PLACES);

            for (StopPlace stopPlace : stopPlaces) {
                marshaller.marshal(netexFactory.createStopPlace(stopPlace), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeNetworkElement(Context context, XMLStreamWriter writer, mobi.chouette.model.Line line) {
        org.rutebanken.netex.model.Network network = networkProducer.produce(context, line.getNetwork());

        try {
            marshaller.marshal(netexFactory.createNetwork(network), writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRoutePointsElement(XMLStreamWriter writer, mobi.chouette.model.Line line) {
        Set<RoutePoint> routePoints = createRoutePoints(line.getRoutes());

        try {
            writer.writeStartElement(ROUTE_POINTS);

            for (RoutePoint routePoint : routePoints) {
                marshaller.marshal(netexFactory.createRoutePoint(routePoint), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeRoutesElement(Context context, XMLStreamWriter writer, mobi.chouette.model.Line line) {
        List<org.rutebanken.netex.model.Route> routes = new ArrayList<>();

        for (mobi.chouette.model.Route neptuneRoute : line.getRoutes()) {
            org.rutebanken.netex.model.Route netexRoute = routeProducer.produce(context, neptuneRoute);
            routes.add(netexRoute);
        }
        try {
            writer.writeStartElement(ROUTES);

            for (org.rutebanken.netex.model.Route route : routes) {
                marshaller.marshal(netexFactory.createRoute(route), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeLinesElement(Context context, XMLStreamWriter writer, mobi.chouette.model.Line line) {
        org.rutebanken.netex.model.Line netexLine = lineProducer.produce(context, line);

        try {
            writer.writeStartElement(LINES);
            marshaller.marshal(netexFactory.createLine(netexLine), writer);
            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeScheduledStopPointsElement(XMLStreamWriter writer, mobi.chouette.model.Line line) {
        Set<ScheduledStopPoint> scheduledStopPoints = createScheduledStopPoints(line.getRoutes());

        try {
            writer.writeStartElement(SCHEDULED_STOP_POINTS);

            for (ScheduledStopPoint scheduledStopPoint : scheduledStopPoints) {
                marshaller.marshal(netexFactory.createScheduledStopPoint(scheduledStopPoint), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeStopAssignmentsElement(XMLStreamWriter writer, mobi.chouette.model.Line line) {
        Set<PassengerStopAssignment> stopAssignments = createStopAssignments(line.getRoutes());

        try {
            writer.writeStartElement(STOP_ASSIGNMENTS);

            for (PassengerStopAssignment stopAssignment : stopAssignments) {
                marshaller.marshal(netexFactory.createPassengerStopAssignment(stopAssignment), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeJourneyPatternsElement(Context context, XMLStreamWriter writer, mobi.chouette.model.Line line) {
        List<org.rutebanken.netex.model.JourneyPattern> journeyPatterns = new ArrayList<>();

        for (mobi.chouette.model.Route route : line.getRoutes()) {
            for (mobi.chouette.model.JourneyPattern neptuneJourneyPattern : route.getJourneyPatterns()) {
                org.rutebanken.netex.model.JourneyPattern netexJourneyPattern = journeyPatternProducer.produce(context, neptuneJourneyPattern);
                journeyPatterns.add(netexJourneyPattern);
            }
        }
        try {
            writer.writeStartElement(JOURNEY_PATTERNS);

            for (JourneyPattern journeyPattern : journeyPatterns) {
                marshaller.marshal(netexFactory.createJourneyPattern(journeyPattern), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeVehicleJourneysElement(Context context, XMLStreamWriter writer, ExportableData exportableData) {
        List<ServiceJourney> serviceJourneys = new ArrayList<>();

        for (mobi.chouette.model.VehicleJourney vehicleJourney : exportableData.getVehicleJourneys()) {
            ServiceJourney serviceJourney = serviceJourneyProducer.produce(context, vehicleJourney, exportableData.getLine());
            serviceJourneys.add(serviceJourney);
        }
        try {
            writer.writeStartElement(VEHICLE_JOURNEYS);

            for (ServiceJourney serviceJourney : serviceJourneys) {
                marshaller.marshal(netexFactory.createServiceJourney(serviceJourney), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDayTypesElement(XMLStreamWriter writer, List<DayType> dayTypes) {
        try {
            writer.writeStartElement(DAY_TYPES);

            for (DayType dayType : dayTypes) {
                marshaller.marshal(netexFactory.createDayType(dayType), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDayTypeAssignmentsElement(XMLStreamWriter writer, List<DayTypeAssignment> dayTypeAssignments) {
        try {
            writer.writeStartElement(DAY_TYPE_ASSIGNMENTS);

            for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
                marshaller.marshal(netexFactory.createDayTypeAssignment(dayTypeAssignment), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeOperatingPeriodsElement(XMLStreamWriter writer, List<OperatingPeriod> operatingPeriods) {
        try {
            writer.writeStartElement(OPERATING_PERIODS);

            for (OperatingPeriod operatingPeriod : operatingPeriods) {
                marshaller.marshal(netexFactory.createOperatingPeriod(operatingPeriod), writer);
            }

            writer.writeEndElement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeElement(XMLStreamWriter writer, String element, String value) {
        try {
            writer.writeStartElement(element);
            writer.writeCharacters(value);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
*/

}
