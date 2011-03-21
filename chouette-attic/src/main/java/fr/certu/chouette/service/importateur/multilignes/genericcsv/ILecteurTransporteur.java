package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import fr.certu.chouette.modele.Transporteur;

public interface ILecteurTransporteur extends ILecteurSpecifique {

    public Transporteur getTransporteur();
}
