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

import chouette.schema.CommonFacilityGroup;

/**
 * Class CommonFacilityGroupDescriptor.
 * 
 * @version $Revision$ $Date$
 */
public class CommonFacilityGroupDescriptor extends org.exolab.castor.xml.util.XMLClassDescriptorImpl {


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

    public CommonFacilityGroupDescriptor() {
        super();
        _nsURI = "http://www.siri.org.uk/siri";
        _xmlName = "CommonFacilityGroup";
        _elementDefinition = false;

        //-- set grouping compositor
        setCompositorAsSequence();
        org.exolab.castor.xml.util.XMLFieldDescriptorImpl  desc           = null;
        org.exolab.castor.mapping.FieldHandler             handler        = null;
        org.exolab.castor.xml.FieldValidator               fieldValidator = null;
        //-- initialize attribute descriptors

        //-- initialize element descriptors

        //-- _fareClassFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.FareClassFacilityEnumeration.class, "_fareClassFacilityList", "FareClassFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getFareClassFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addFareClassFacility( (chouette.schema.types.FareClassFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllFareClassFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("FareClassFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _fareClassFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _ticketingFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.TicketingFacilityEnumeration.class, "_ticketingFacilityList", "TicketingFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getTicketingFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addTicketingFacility( (chouette.schema.types.TicketingFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllTicketingFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("TicketingFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _ticketingFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _nuisanceFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.NuisanceFacilityEnumeration.class, "_nuisanceFacilityList", "NuisanceFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getNuisanceFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addNuisanceFacility( (chouette.schema.types.NuisanceFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllNuisanceFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("NuisanceFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _nuisanceFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _mobilityFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.MobilityFacilityEnumeration.class, "_mobilityFacilityList", "MobilityFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getMobilityFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addMobilityFacility( (chouette.schema.types.MobilityFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllMobilityFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("MobilityFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _mobilityFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _passengerInformationFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.PassengerInformationFacilityEnumeration.class, "_passengerInformationFacilityList", "PassengerInformationFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getPassengerInformationFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addPassengerInformationFacility( (chouette.schema.types.PassengerInformationFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllPassengerInformationFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("PassengerInformationFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _passengerInformationFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _passengerCommsFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.PassengerCommsFacilityEnumeration.class, "_passengerCommsFacilityList", "PassengerCommsFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getPassengerCommsFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addPassengerCommsFacility( (chouette.schema.types.PassengerCommsFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllPassengerCommsFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("PassengerCommsFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _passengerCommsFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _refreshmentFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.RefreshmentFacilityEnumeration.class, "_refreshmentFacilityList", "RefreshmentFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getRefreshmentFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addRefreshmentFacility( (chouette.schema.types.RefreshmentFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllRefreshmentFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("RefreshmentFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _refreshmentFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _accessFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.AccessFacilityEnumeration.class, "_accessFacilityList", "AccessFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getAccessFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addAccessFacility( (chouette.schema.types.AccessFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllAccessFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("AccessFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _accessFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _sanitaryFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.SanitaryFacilityEnumeration.class, "_sanitaryFacilityList", "SanitaryFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getSanitaryFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addSanitaryFacility( (chouette.schema.types.SanitaryFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllSanitaryFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("SanitaryFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _sanitaryFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
        { //-- local scope
        }
        desc.setValidator(fieldValidator);
        //-- _luggageFacilityList
        desc = new org.exolab.castor.xml.util.XMLFieldDescriptorImpl(chouette.schema.types.LuggageFacilityEnumeration.class, "_luggageFacilityList", "LuggageFacility", org.exolab.castor.xml.NodeType.Element);
        handler = new org.exolab.castor.xml.XMLFieldHandler() {
            @Override
            public java.lang.Object getValue( java.lang.Object object ) 
                throws IllegalStateException
            {
                CommonFacilityGroup target = (CommonFacilityGroup) object;
                return target.getLuggageFacility();
            }
            @Override
            public void setValue( java.lang.Object object, java.lang.Object value) 
                throws IllegalStateException, IllegalArgumentException
            {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.addLuggageFacility( (chouette.schema.types.LuggageFacilityEnumeration) value);
                } catch (java.lang.Exception ex) {
                    throw new IllegalStateException(ex.toString());
                }
            }
            public void resetValue(Object object) throws IllegalStateException, IllegalArgumentException {
                try {
                    CommonFacilityGroup target = (CommonFacilityGroup) object;
                    target.removeAllLuggageFacility();
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
        desc.setSchemaType("list");
        desc.setComponentType("LuggageFacilityEnumeration");
        desc.setHandler(handler);
        desc.setNameSpaceURI("http://www.siri.org.uk/siri");
        desc.setMultivalued(true);
        addFieldDescriptor(desc);
        addSequenceElement(desc);

        //-- validation code for: _luggageFacilityList
        fieldValidator = new org.exolab.castor.xml.FieldValidator();
        fieldValidator.setMinOccurs(0);
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
        return chouette.schema.CommonFacilityGroup.class;
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
