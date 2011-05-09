package fr.certu.chouette.service.amivif.base;

//import java.util.ArrayList;
import org.apache.commons.beanutils.BeanUtils;

import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class LineConverter {

    public chouette.schema.Line atc(amivif.schema.Line amivifLine) {
        if (amivifLine == null) {
            return null;
        }
        chouette.schema.Line chouetteLine = new chouette.schema.Line();
        RegistrationConverter registration = new RegistrationConverter();
        amivif.schema.Registration amivifRegistration = amivifLine.getRegistration();
        chouette.schema.Registration chouetteRegistration = registration.atc(amivifRegistration);
        amivifLine.setRegistration(null);
        TransportModeNameConverter modename = new TransportModeNameConverter();
        amivif.schema.types.TransportModeNameType amivifTransportModeName = amivifLine.getTransportModeName();
        chouette.schema.types.TransportModeNameType chouetteTransportModeName = modename.atc(amivifTransportModeName);
        amivifLine.setTransportModeName(null);
        try {
            BeanUtils.copyProperties(chouetteLine, amivifLine);
        } catch (Exception e) {
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e);
        }

        if (!amivifLine.hasObjectVersion() || amivifLine.getObjectVersion() < 1) {
            chouetteLine.setObjectVersion(1);
        }

        amivifLine.setRegistration(amivifRegistration);
        amivifLine.setTransportModeName(amivifTransportModeName);
        chouetteLine.setRegistration(chouetteRegistration);
        chouetteLine.setTransportModeName(chouetteTransportModeName);
        return chouetteLine;
    }

    public chouette.schema.Line[] atc(amivif.schema.Line[] amivifLines) {
        if (amivifLines == null) {
            return new chouette.schema.Line[0];
        }
        int totalLignes = amivifLines.length;
        chouette.schema.Line[] chouetteLignes = new chouette.schema.Line[totalLignes];
        for (int i = 0; i < totalLignes; i++) {
            chouetteLignes[i] = atc(amivifLines[i]);
        }
        return chouetteLignes;
    }

    public amivif.schema.Line cta(chouette.schema.Line chouetteLine) {
        if (chouetteLine == null) {
            return null;
        }
        amivif.schema.Line amivifLine = new amivif.schema.Line();
        RegistrationConverter registration = new RegistrationConverter();
        chouette.schema.Registration chouetteRegistration = chouetteLine.getRegistration();
        amivif.schema.Registration amivifRegistration = registration.cta(chouetteRegistration);
        chouetteLine.setRegistration(null);
        TransportModeNameConverter modename = new TransportModeNameConverter();
        chouette.schema.types.TransportModeNameType chouetteTransportModeName = chouetteLine.getTransportModeName();
        amivif.schema.types.TransportModeNameType amivifTransportModeName = modename.cta(chouetteTransportModeName);
        chouetteLine.setTransportModeName(null);
        try {
            BeanUtils.copyProperties(amivifLine, chouetteLine);
        } catch (Exception e) {
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e);
        }

        if (!chouetteLine.hasObjectVersion() || chouetteLine.getObjectVersion() < 1) {
            amivifLine.setObjectVersion(1);
        }
        
        chouetteLine.setRegistration(chouetteRegistration);
        chouetteLine.setTransportModeName(chouetteTransportModeName);
        amivifLine.setRegistration(amivifRegistration);
        amivifLine.setTransportModeName(amivifTransportModeName);
        return amivifLine;
    }
}
