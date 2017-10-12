package mobi.chouette.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ejb.Stateless;

import lombok.extern.log4j.Log4j;

import org.apache.commons.io.FileUtils;

import static mobi.chouette.common.file.LocalFileStore.BEAN_NAME;

/**
 * Store permanent files in local file system.
 */
@Stateless(name = BEAN_NAME)
@Log4j
public class LocalFileStore implements FileStore {

	public static final String BEAN_NAME = "LocalFileStore";

	@Override
	public InputStream getFileContent(Path filePath) {
		try {
			return new FileInputStream(filePath.toFile());
		} catch (IOException ioE) {
			throw new FileServiceException("Failed to read from file: " + ioE.getMessage(), ioE);
		}
	}


	@Override
	public void writeFile(Path filePath, InputStream content) {
		try {
			FileUtils.copyInputStreamToFile(content, filePath.toFile());
		} catch (IOException ioE) {
			throw new FileServiceException("Failed to write to file: " + ioE.getMessage(), ioE);
		}
	}

	@Override
	public boolean delete(Path filePath) {
		File file = filePath.toFile();
		if (file.exists()) {
			return filePath.toFile().delete();
		}
		return false;
	}

	@Override
	public void deleteFolder(Path folder) {
		if (Files.exists(folder)) {
			try {
				FileUtils.deleteDirectory(folder.toFile());
			} catch (Exception e) {
				throw new FileServiceException("Failed to delete folder: " + folder, e);
			}
		}
	}

	@Override
	public boolean exists(Path filePath) {
		return Files.exists(filePath);
	}

	@Override
	public void createFolder(Path folder) {
		try {
			if (!Files.exists(folder)) {
				Files.createDirectories(folder);
			}
		} catch (IOException ioE) {
			throw new FileServiceException("Failed to create folder: " + ioE.getMessage(), ioE);
		}
	}
}
