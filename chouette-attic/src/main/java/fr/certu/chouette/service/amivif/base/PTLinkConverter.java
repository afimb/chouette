package fr.certu.chouette.service.amivif.base;

import java.math.BigDecimal;

import org.apache.commons.beanutils.BeanUtils;

import fr.certu.chouette.service.amivif.IAccesseurAreaStop;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;

public class PTLinkConverter {

    private chouette.schema.PtLink atc(amivif.schema.PTLink amivif) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.PtLink chouette = new chouette.schema.PtLink();
        amivif.setLinkDistance(new BigDecimal(0L));
        try {
            BeanUtils.copyProperties(chouette, amivif);
        } catch (Exception e) {
            throw new ServiceException(CodeIncident.DONNEE_INVALIDE, e);
        }
        if (!amivif.hasObjectVersion() || amivif.getObjectVersion() > 1) {
            chouette.setObjectVersion(1);
        }
        return chouette;
    }

    public chouette.schema.PtLink[] atc(amivif.schema.PTLink[] amivifs) {

        if (amivifs == null) {
            return new chouette.schema.PtLink[0];
        }

        int total = amivifs.length;
        chouette.schema.PtLink[] chouettes = new chouette.schema.PtLink[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i]);
        }
        return chouettes;
    }

    private amivif.schema.PTLink cta(chouette.schema.PtLink chouette, IAccesseurAreaStop accesseur) {
        amivif.schema.PTLink amivif = new amivif.schema.PTLink();
        amivif.setObjectId(chouette.getObjectId());
        amivif.setStartOfLink(accesseur.getStopAreaOfStop(chouette.getStartOfLink()).getObjectId());
        amivif.setEndOfLink(accesseur.getStopAreaOfStop(chouette.getEndOfLink()).getObjectId());
        if (!chouette.hasObjectVersion() || chouette.getObjectVersion() > 1) {
            amivif.setObjectVersion(1);
        }
        return amivif;
    }

    public amivif.schema.PTLink[] cta(chouette.schema.PtLink[] chouettes, IAccesseurAreaStop accesseur) {
        if (chouettes == null) {
            return null;
        }

        int total = chouettes.length;
        amivif.schema.PTLink[] amivifs = new amivif.schema.PTLink[total];
        for (int i = 0; i < total; i++) {
            amivifs[ i] = cta(chouettes[ i], accesseur);
        }
        return amivifs;
    }
}
