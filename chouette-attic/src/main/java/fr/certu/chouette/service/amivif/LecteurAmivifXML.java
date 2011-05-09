package fr.certu.chouette.service.amivif;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

import amivif.schema.RespPTDestrLine;
import amivif.schema.RespPTDestrLineTypeType;
import amivif.schema.RespPTLineStructTimetable;
import amivif.schema.RespPTLineStructTimetableTypeType;
import fr.certu.chouette.service.amivif.util.XMLAdapter;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.validation.commun.TypeInvalidite;
import fr.certu.chouette.service.validation.commun.ValidationException;

public class LecteurAmivifXML implements ILecteurAmivifXML {

    private static final Log logger = LogFactory.getLog(LecteurAmivifXML.class);
    private static final String JEU_CARACTERES = "ISO-8859-1";
    private ValidationException validationException;

    /* (non-Javadoc)
     * @see fr.certu.chouette.service.amivif.ILecteurAmivifXML#lire(java.lang.String)
     */
    //EVOCASTOR
    public RespPTLineStructTimetable lire(String fileName) {
        logger.debug("EVOCASTOR --> unmarshal RespPTLineStructTimetable");
        String contenu = null;
        validationException = new ValidationException();

        try {
            contenu = FileUtils.readFileToString(new File(fileName), JEU_CARACTERES);
        } catch (IOException e) {
            validationException.add(TypeInvalidite.FILE_NOT_FOUND, e.getMessage());
            throw validationException;
        }

        contenu = XMLAdapter.atcSimplify(contenu);

        RespPTLineStructTimetable amivifLine = null;
        try {
            Unmarshaller anUnmarshaller = new Unmarshaller(RespPTLineStructTimetable.class);
            anUnmarshaller.setValidation(false);
            amivifLine = (RespPTLineStructTimetable)anUnmarshaller.unmarshal(new StringReader(contenu));
        } catch (org.exolab.castor.xml.ValidationException e) {
            do {
                validationException.add(TypeInvalidite.INVALID_XML_FILE, e.getMessage());
                e = e.getNext();
            } while (e != null);
            throw validationException;
        } catch (MarshalException e) {
            validationException.add(TypeInvalidite.INVALID_CHOUETTE_FILE, e.getMessage());
            throw validationException;
        }
        return amivifLine;
    }
    /* (non-Javadoc)
     * @see fr.certu.chouette.service.amivif.ILecteurAmivifXML#lire(java.lang.String)
     */

    public void ecrire(RespPTLineStructTimetableTypeType amivif, File file) {
        logger.debug("EVOCASTOR --> marshal RespPTLineStructTimetableTypeType");
        try {
            StringWriter writer = new StringWriter();

            Marshaller aMarshaller = new Marshaller(writer);
            aMarshaller.setEncoding("ISO-8859-1");
            aMarshaller.setRootElement("RespPTLineStructTimetable");
            aMarshaller.setNamespaceMapping("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            aMarshaller.setSchemaLocation("http://www.trident.org/schema/trident RATP-AMIVIF.xsd");
            aMarshaller.setValidation(false);
            aMarshaller.marshal(amivif);

            String contenu = XMLAdapter.ctaSimplify(writer.toString());
            FileUtils.writeStringToFile(file, contenu, JEU_CARACTERES);

        } catch (IOException e) {
            throw new ServiceException(CodeIncident.ERR_XML_ECRITURE, e);
        } catch (MarshalException e) {
            throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
        }
    }

    public void ecrire(RespPTDestrLineTypeType amivif, File file) {
        logger.debug("EVOCASTOR --> marshal RespPTDestrLineTypeType");
        try {
            StringWriter writer = new StringWriter();

            Marshaller aMarshaller = new Marshaller(writer);
            aMarshaller.setEncoding("ISO-8859-1");
            aMarshaller.setRootElement("RespPTDestrLine");
            aMarshaller.setNamespaceMapping("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            aMarshaller.setSchemaLocation("http://www.trident.org/schema/trident RATP-AMIVIF.xsd");
            aMarshaller.setValidation(false);
            aMarshaller.marshal(amivif);

            String contenu = XMLAdapter.ctaSimplify(writer.toString());
            FileUtils.writeStringToFile(file, contenu, JEU_CARACTERES);

        } catch (IOException e) {
            throw new ServiceException(CodeIncident.ERR_XML_ECRITURE, e);
        } catch (MarshalException e) {
            throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
        } catch (org.exolab.castor.xml.ValidationException e) {
            throw new ServiceException(CodeIncident.ERR_XML_FORMAT, e);
        }
    }
}
