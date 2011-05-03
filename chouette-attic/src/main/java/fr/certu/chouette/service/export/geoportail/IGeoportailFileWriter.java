package fr.certu.chouette.service.export.geoportail;

import fr.certu.chouette.echange.ILectureEchange;
import java.io.File;
import java.util.List;

public interface IGeoportailFileWriter {
    public void write(List<ILectureEchange> lecturesEchanges, File _temp, String _nomFichier);
}
