package fr.certu.chouette.service.importateur.multilignes.genericcsv;

import fr.certu.chouette.modele.Reseau;

public interface ILecteurReseau extends ILecteurSpecifique {

    public Reseau getReseau();
}
