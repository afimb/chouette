package fr.certu.chouette.service.export.gtfs;

import fr.certu.chouette.echange.ILectureEchange;
import java.io.File;
import java.util.List;

public interface IGTFSFileWriter {
    public void write(List<ILectureEchange> lecturesEchanges, File _temp, String _nomFichier);
}
