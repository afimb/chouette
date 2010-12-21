package fr.certu.chouette.service.importateur.multilignes;

import fr.certu.chouette.echange.ILectureEchange;
import fr.certu.chouette.service.importateur.ILecteur;
import java.util.List;

public interface ILecteurPrincipal extends ILecteur {

    public List<ILectureEchange> getLecturesEchange();
    public String getLogFileName();
}
