package mobi.chouette.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.ContenerChecker;
import mobi.chouette.common.file.FileStore;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import org.apache.commons.io.IOUtils;
import org.rutebanken.helper.gcp.BlobStoreHelper;

import static mobi.chouette.service.GoogleCloudFileStore.BEAN_NAME;

/**
 * Store permanent files in Google Cloud Storage.
 */
@Singleton(name = BEAN_NAME)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Log4j
public class GoogleCloudFileStore implements FileStore {

	public static final String BEAN_NAME = "GoogleCloudFileStore";

	@EJB
	private ContenerChecker checker;

	private Storage storage;

	private String containerName;

	private String baseFolder;


	@PostConstruct
	public void init() {
		baseFolder =  System.getProperty(checker.getContext() + ".directory");
		containerName = System.getProperty(checker.getContext() + ".blobstore.gcs.container.name");
		String credentialPath = System.getProperty(checker.getContext() + ".blobstore.gcs.credential.path");
		String projectId = System.getProperty(checker.getContext() + ".blobstore.gcs.project.id");

		log.info("Initializing blob store service. ContainerName: " + containerName + ", credentialPath: " + credentialPath + ", projectId: " + projectId);

		storage = BlobStoreHelper.getStorage(credentialPath, projectId);
	}


	@Override
	public InputStream getFileContent(Path filePath) {
		return BlobStoreHelper.getBlob(storage, containerName, toGCSPath(filePath));
	}

	@Override
	public void writeFile(Path filePath, InputStream content) {
		try {
			// TODO user BlobStoreHelper.uploadBlobWithRetry directly when proven to work (ie no retries logged)

			byte[] bytes = IOUtils.toByteArray(content);
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);


			Blob blob = BlobStoreHelper.uploadBlobWithRetry(storage, containerName, toGCSPath(filePath), bis, false);
			// TODO this should not be necessary (but is). why? BlobStoreHelper not thread safe?
			if (Long.valueOf(0).equals(blob.getSize()) && bytes.length > 0) {
				log.info("Blob upload created empty blob even though there was content in the stream. Retrying " + filePath);
				bis.reset();

				Blob blobRetry = BlobStoreHelper.uploadBlobWithRetry(storage, containerName, toGCSPath(filePath), bis, false);
				log.info("Retry of fileupload for " + filePath + " resulted in blob with size: " + blobRetry.getSize());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteFolder(Path folder) {
		BlobStoreHelper.deleteBlobsByPrefix(storage, containerName, toGCSPath(folder));
	}


	@Override
	public boolean exists(Path filePath) {
		return getFileContent(filePath) != null;
	}


	@Override
	public void createFolder(Path folder) {
		// Folders do not existing in GC storage
	}

	@Override
	public boolean delete(Path filePath) {
		return BlobStoreHelper.deleteBlobsByPrefix(storage, containerName, toGCSPath(filePath));
	}

	private String toGCSPath(Path path) {
		String withoutBaseFolder = path.toString().replaceFirst(baseFolder, "");
		if (withoutBaseFolder.startsWith("/")) {
			return withoutBaseFolder.replaceFirst("/", "");
		}
		return withoutBaseFolder;
	}

}
