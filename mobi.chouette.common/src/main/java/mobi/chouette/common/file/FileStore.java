package mobi.chouette.common.file;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Access of permanent files.
 */
public interface FileStore {

	InputStream getFileContent(Path filePath);

	void writeFile(Path filePath, InputStream content);

	boolean delete(Path filePath);

	void deleteFolder(Path folder);

	void createFolder(Path folder);

	boolean exists(Path filePath);

}
