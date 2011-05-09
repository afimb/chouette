package fr.certu.chouette.service.amivif.base;

import java.util.Hashtable;
import java.util.Map;

import amivif.schema.Registration;
import amivif.schema.Route;
import amivif.schema.SubLine;
import amivif.schema.types.PTDirectionType;
import chouette.schema.ChouetteRoute;
import chouette.schema.RouteExtension;
import fr.certu.chouette.service.identification.ObjectIdLecteur;

public class RouteConverter {

    private chouette.schema.ChouetteRoute atc(amivif.schema.Route amivif, SubLine subline) {

        if (amivif == null) {
            return null;
        }

        chouette.schema.ChouetteRoute chouette = new chouette.schema.ChouetteRoute();

        chouette.setObjectId(amivif.getObjectId());
        if (amivif.hasObjectVersion() && amivif.getObjectVersion() >= 1) {
            chouette.setObjectVersion(amivif.getObjectVersion());
        } else {
            chouette.setObjectVersion(1);
        }
        chouette.setCreationTime(amivif.getCreationTime());
        chouette.setCreatorId(amivif.getCreatorId());
        chouette.setNumber(amivif.getNumber());

        if (subline != null) {
            // une partie de l'itinéraire porte les infos de sa sous ligne
            chouette.setName(subline.getRegistration().getRegistrationNumber());
            chouette.setComment(subline.getComment());
            chouette.setPublishedName(subline.getSublineName());

            // la sous-ligne référence au plus 2 itinéraires (Aller et retour)
            int totalItineraires = subline.getRouteIdCount();
            for (int i = 0; i < totalItineraires; i++) {
                if (!subline.getRouteId(i).equals(amivif.getObjectId())) {
                    // référencer l'itinéraire retour
                    chouette.setWayBackRouteId(subline.getRouteId(i));
                    break;
                }
            }
        }

        RouteExtension routeExtension = new RouteExtension();
        routeExtension.setWayBack("R");
        if (amivif.getDirection() == null || amivif.getDirection().equals(PTDirectionType.A)) {
            routeExtension.setWayBack("A");
        }
        chouette.setRouteExtension(routeExtension);

        int totalLink = amivif.getPtLinkIdCount();
        for (int i = 0; i < totalLink; i++) {
            chouette.addPtLinkId(amivif.getPtLinkId(i));
        }

        int totalJP = amivif.getJourneyPatternIdCount();
        for (int i = 0; i < totalJP; i++) {
            chouette.addJourneyPatternId(amivif.getJourneyPatternId(i));
        }

        return chouette;
    }

    public chouette.schema.ChouetteRoute[] atc(amivif.schema.Route[] amivifs, SubLine[] sublines) {

        if (amivifs == null) {
            return new chouette.schema.ChouetteRoute[0];
        }

        Map<String, SubLine> sousLigneParRouteId = new Hashtable<String, SubLine>();
        if (sublines != null) {
            int totalSousLignes = sublines.length;
            for (int i = 0; i < totalSousLignes; i++) {
                SubLine subLine = sublines[ i];
                int totalItineraires = subLine.getRouteIdCount();
                for (int j = 0; j < totalItineraires; j++) {
                    sousLigneParRouteId.put(subLine.getRouteId(j), subLine);
                }
            }
        }
        int total = amivifs.length;
        chouette.schema.ChouetteRoute[] chouettes = new chouette.schema.ChouetteRoute[total];

        for (int i = 0; i < total; i++) {
            chouettes[i] = atc(amivifs[i], sousLigneParRouteId.get(amivifs[i].getObjectId()));
        }
        return chouettes;
    }

    private amivif.schema.Route cta(chouette.schema.ChouetteRoute chouette) {
        if (chouette == null) {
            return null;
        }

        amivif.schema.Route amivif = new Route();

        amivif.setObjectId(chouette.getObjectId());
        if (chouette.hasObjectVersion() && chouette.getObjectVersion() >= 1) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

//		amivif.setName( chouette.getName());
        amivif.setNumber(chouette.getNumber());
//		amivif.setPublishedName( chouette.getPublishedName());
//		amivif.setComment( chouette.getComment());
        amivif.setDirection(chouette.getRouteExtension().getWayBack().equals("A")
                ? PTDirectionType.A : PTDirectionType.R);

        int totalLink = chouette.getPtLinkIdCount();
        for (int i = 0; i < totalLink; i++) {
            amivif.addPtLinkId(chouette.getPtLinkId(i));
        }

        int totalJP = chouette.getJourneyPatternIdCount();
        for (int i = 0; i < totalJP; i++) {
            amivif.addJourneyPatternId(chouette.getJourneyPatternId(i));
        }

        return amivif;
    }

    private amivif.schema.SubLine ctaSubline(chouette.schema.ChouetteRoute chouette) {
        if (chouette == null) {
            return null;
        }

        amivif.schema.SubLine amivif = new SubLine();

        StringBuffer buffer = new StringBuffer();
        buffer.append(ObjectIdLecteur.lirePartieSysteme(chouette.getObjectId()));
        buffer.append(":SubLine:");
        buffer.append(chouette.getName());

        amivif.setObjectId(buffer.toString());
        if (chouette.hasObjectVersion()) {
            amivif.setObjectVersion(chouette.getObjectVersion());
        } else {
            amivif.setObjectVersion(1);
        }
        amivif.setCreationTime(chouette.getCreationTime());
        amivif.setCreatorId(chouette.getCreatorId());

        amivif.setComment(chouette.getComment());
        amivif.setSublineName(chouette.getPublishedName());

        Registration registration = new Registration();
        registration.setRegistrationNumber(chouette.getName());
        amivif.setRegistration(registration);

        amivif.addRouteId(chouette.getObjectId());

        return amivif;
    }

    public amivif.schema.Route[] cta(chouette.schema.ChouetteRoute[] chouettes) {
        if (chouettes == null) {
            return null;
        }

        int totalItineraires = chouettes.length;
        amivif.schema.Route[] amivifs = new amivif.schema.Route[totalItineraires];
        for (int i = 0; i < totalItineraires; i++) {
            amivifs[ i] = cta(chouettes[ i]);
        }
        return amivifs;
    }

    public amivif.schema.SubLine[] ctaSubline(chouette.schema.ChouetteRoute[] chouettes) {
        if (chouettes == null) {
            return null;
        }

        Map<String, SubLine> sousLigneParitineraire = new Hashtable<String, SubLine>();

        int totalItineraires = chouettes.length;
        for (int i = 0; i < totalItineraires; i++) {
            ChouetteRoute chouetteRoute = chouettes[i];
            String wayBackRouteId = chouetteRoute.getWayBackRouteId();
            if (wayBackRouteId != null && sousLigneParitineraire.containsKey(wayBackRouteId)) {
                SubLine subLine = sousLigneParitineraire.get(chouettes[i].getWayBackRouteId());
                subLine.addRouteId(chouetteRoute.getObjectId());
            } else {
                sousLigneParitineraire.put(chouetteRoute.getObjectId(), ctaSubline(chouetteRoute));
            }
        }
        return sousLigneParitineraire.values().toArray(new SubLine[0]);
    }
}
