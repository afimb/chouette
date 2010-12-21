/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 1.3.0.1</a>, using an XML
 * Schema.
 * $Id$
 */

package chouette.schema.descriptors;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import chouette.schema.AllFacilitiesFeatureStructureType;

/**
 * Class AllFacilitiesFeatureStructureTypeDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class AllFacilitiesFeatureStructureTypeDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _elementDefinition.
     */
    private boolean _elementDefinition;

    /**
     * Field _nsPrefix.
     */
    private java.lang.String _nsPrefix;

    /**
     * Field _nsURI.
     */
    private java.lang.String _nsURI;

    /**
     * Field _xmlName.
     */
    private java.lang.String _xmlName;

    /**
     * Field _identity.
     */
    private org.exolab.castor.xml.XMLFieldDescriptor _identity;


      //----------------/
     //- Constructors -/
    //----------------/

    public AllFacilitiesFeatureStructureTypeDescriptor() {
        super();
        _nsURI = "http://www.siri.org.uk/siri";
        _xmlName = "AllFacilitiesFeatureStructure";
        _elementDefinition = false;

        //-- set grouping compositor
        setCompositorAsChoice();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.mapping.FieldHandler             handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- initialize element descriptors

        //-- _accessFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.AccessFacilityEnumeration.class, "_accessFacility", "AccessFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getAccessFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setAccessFacility( (chouette.schema.types.AccessFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.AccessFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.AccessFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _accessFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _accommodationFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.AccommodationFacilityEnumeration.class, "_accommodationFacility", "AccommodationFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getAccommodationFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setAccommodationFacility( (chouette.schema.types.AccommodationFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.AccommodationFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.AccommodationFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _accommodationFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _assistanceFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.AssistanceFacilityEnumeration.class, "_assistanceFacility", "AssistanceFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getAssistanceFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setAssistanceFacility( (chouette.schema.types.AssistanceFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.AssistanceFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.AssistanceFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _assistanceFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _fareClassFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.FareClassFacilityEnumeration.class, "_fareClassFacility", "FareClassFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getFareClassFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setFareClassFacility( (chouette.schema.types.FareClassFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.FareClassFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.FareClassFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _fareClassFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _hireFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.HireFacilityEnumeration.class, "_hireFacility", "HireFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getHireFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setHireFacility( (chouette.schema.types.HireFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.HireFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.HireFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _hireFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _luggageFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.LuggageFacilityEnumeration.class, "_luggageFacility", "LuggageFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getLuggageFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setLuggageFacility( (chouette.schema.types.LuggageFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.LuggageFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.LuggageFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _luggageFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _mobilityFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.MobilityFacilityEnumeration.class, "_mobilityFacility", "MobilityFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getMobilityFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setMobilityFacility( (chouette.schema.types.MobilityFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.MobilityFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.MobilityFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _mobilityFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _nuisanceFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.NuisanceFacilityEnumeration.class, "_nuisanceFacility", "NuisanceFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getNuisanceFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setNuisanceFacility( (chouette.schema.types.NuisanceFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.NuisanceFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.NuisanceFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _nuisanceFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _parkingFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.ParkingFacilityEnumeration.class, "_parkingFacility", "ParkingFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getParkingFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setParkingFacility( (chouette.schema.types.ParkingFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.ParkingFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.ParkingFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _parkingFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _passengerCommsFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.PassengerCommsFacilityEnumeration.class, "_passengerCommsFacility", "PassengerCommsFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getPassengerCommsFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setPassengerCommsFacility( (chouette.schema.types.PassengerCommsFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.PassengerCommsFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.PassengerCommsFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _passengerCommsFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _passengerInformationFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.PassengerInformationFacilityEnumeration.class, "_passengerInformationFacility", "PassengerInformationFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getPassengerInformationFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setPassengerInformationFacility( (chouette.schema.types.PassengerInformationFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.PassengerInformationFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.PassengerInformationFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _passengerInformationFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _refreshmentFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.RefreshmentFacilityEnumeration.class, "_refreshmentFacility", "RefreshmentFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getRefreshmentFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setRefreshmentFacility( (chouette.schema.types.RefreshmentFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.RefreshmentFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.RefreshmentFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _refreshmentFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _reservedSpaceFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.ReservedSpaceFacilityEnumeration.class, "_reservedSpaceFacility", "ReservedSpaceFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getReservedSpaceFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setReservedSpaceFacility( (chouette.schema.types.ReservedSpaceFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.ReservedSpaceFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.ReservedSpaceFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _reservedSpaceFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _retailFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.RetailFacilityEnumeration.class, "_retailFacility", "RetailFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getRetailFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setRetailFacility( (chouette.schema.types.RetailFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.RetailFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.RetailFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _retailFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _sanitaryFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.SanitaryFacilityEnumeration.class, "_sanitaryFacility", "SanitaryFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getSanitaryFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setSanitaryFacility( (chouette.schema.types.SanitaryFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.SanitaryFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.SanitaryFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _sanitaryFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ticketingFacility
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.TicketingFacilityEnumeration.class, "_ticketingFacility", "TicketingFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                return target.getTicketingFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    AllFacilitiesFeatureStructureType target = (AllFacilitiesFeatureStructureType) object;
                    target.setTicketingFacility( (chouette.schema.types.TicketingFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            @Override
            @SuppressWarnings("unused")
            public java.lang.Object newInstance(java.lang.Object parent) {
                return null;
            }
        };
        handler = new org.exolab.castor.xml.handlers.EnumFieldHandler(chouette.schema.types.TicketingFacilityEnumeration.class, handler);
        desc.setImmutable(true);
        desc.setSchemaType("chouette.schema.types.TicketingFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setRequired(true);
        desc.setMultivalued(false);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _ticketingFacility
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(1);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
    }


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method getAccessMode.
     * 
     * @return the access mode specified for this class.
     */
    @Override()
    public org.exolab.castor.mapping.AccessMode getAccessMode(
    ) {
        return null;
    }

    /**
     * Method getIdentity.
     * 
     * @return the identity field, null if this class has no
     * identity.
     */
    @Override()
    public org.exolab.castor.mapping.FieldDescriptor getIdentity(
    ) {
        return _identity;
    }

    /**
     * Method getJavaClass.
     * 
     * @return the Java class represented by this descriptor.
     */
    @Override()
    public java.lang.Class getJavaClass(
    ) {
        return chouette.schema.AllFacilitiesFeatureStructureType.class;
    }

    /**
     * Method getNameSpacePrefix.
     * 
     * @return the namespace prefix to use when marshaling as XML.
     */
    @Override()
    public java.lang.String getNameSpacePrefix(
    ) {
        return _nsPrefix;
    }

    /**
     * Method getNameSpaceURI.
     * 
     * @return the namespace URI used when marshaling and
     * unmarshaling as XML.
     */
    @Override()
    public java.lang.String getNameSpaceURI(
    ) {
        return _nsURI;
    }

    /**
     * Method getValidator.
     * 
     * @return a specific validator for the class described by this
     * ClassDescriptor.
     */
    @Override()
    public org.exolab.castor.xml.TypeValidator getValidator(
    ) {
        return this;
    }

    /**
     * Method getXMLName.
     * 
     * @return the XML Name for the Class being described.
     */
    @Override()
    public java.lang.String getXMLName(
    ) {
        return _xmlName;
    }

    /**
     * Method isElementDefinition.
     * 
     * @return true if XML schema definition of this Class is that
     * of a global
     * element or element with anonymous type definition.
     */
    public boolean isElementDefinition(
    ) {
        return _elementDefinition;
    }

}
