package mobi.chouette.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class FileUtil {

	public static List<Path> listFiles(Path path, String glob) throws IOException {
		final PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + glob);

		final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.isDirectory(entry) || matcher.matches(entry.getFileName());
			}
		};
		List<Path> result = new ArrayList<Path>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					result.addAll(listFiles(entry, glob));
					return result;
				}
				result.add(entry);
			}
		}
		return result;
	}

	public static List<Path> listFiles(Path path, String glob, String exclusionGlob) throws IOException {
		final PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + glob);

		final PathMatcher excludeMatcher = path.getFileSystem().getPathMatcher("glob:" + exclusionGlob);

		final DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {

			@Override
			public boolean accept(Path entry) throws IOException {
				return Files.isDirectory(entry)
						|| (matcher.matches(entry.getFileName()) && !excludeMatcher.matches(entry.getFileName()));
			}
		};
		List<Path> result = new ArrayList<Path>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					result.addAll(listFiles(entry, glob, exclusionGlob));
					return result;
				}
				result.add(entry);
			}
		}
		return result;
	}

	public static void uncompress(String filename, String path) throws IOException, ArchiveException {
		ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(new BufferedInputStream(
				new FileInputStream(new File(filename))));
		ArchiveEntry entry = null;
		while ((entry = in.getNextEntry()) != null) {

			String name = FilenameUtils.getName(entry.getName());
			File file = new File(path, name);
			if (entry.isDirectory()) {
				// if (!file.exists()) {
				// file.mkdirs();
				// }
			} else {
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				IOUtils.copy(in, out);
				IOUtils.closeQuietly(out);
			}
		}
		IOUtils.closeQuietly(in);

	}

	public static void compress(String path, String filename) throws IOException {

		File directoryToZip = new File(path);
		List<File> fileList = new ArrayList<File>();
		getAllFiles(directoryToZip, fileList);
		writeZipFile(directoryToZip, filename, fileList);

		// Path dir = Paths.get(path);
		// DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		//
		// ZipArchiveOutputStream zout = new
		// ZipArchiveOutputStream(Files.newOutputStream(Paths.get(filename)));
		// for (Path file : stream) {
		//
		// String name = file.getName(file.getNameCount() - 1).toString();
		// long size = Files.size(file);
		//
		// ZipArchiveEntry entry = new ZipArchiveEntry(name);
		// entry.setSize(size);
		// InputStream in = Files.newInputStream(file);
		//
		// zout.putArchiveEntry(entry);
		// IOUtils.copy(in, zout);
		// zout.closeArchiveEntry();
		// IOUtils.closeQuietly(in);
		//
		// }
		// IOUtils.closeQuietly(zout);

	}

	private static void getAllFiles(File dir, List<File> fileList) {

		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			}
		}

	}

	private static void writeZipFile(File path, String zipName, List<File> fileList) {

		try {
			FileOutputStream fos = new FileOutputStream(zipName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(path, file, zos);
				}
			}

			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
			IOException {

		FileInputStream fis = new FileInputStream(file);

		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

}
