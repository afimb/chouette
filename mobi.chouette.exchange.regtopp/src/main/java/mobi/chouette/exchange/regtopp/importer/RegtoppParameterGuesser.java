package mobi.chouette.exchange.regtopp.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.FileUtil;
import mobi.chouette.exchange.regtopp.importer.version.RegtoppVersion;

@Log4j
public class RegtoppParameterGuesser {

	public static final String REGTOPP_DEFAULT_ENCODING = "ISO-8859-1";

	public static String[] norwegianChars = new String[] { "æ", "ø", "å", "Æ", "Ø", "Å" };

	Path path;

	@Getter
	RegtoppVersion detectedVersion = null;

	@Getter
	String encoding = REGTOPP_DEFAULT_ENCODING; // Default

	public RegtoppParameterGuesser(Path path) throws IOException {
		super();
		this.path = path;

		guessEncoding();
		guessVersion();
	}

	private void guessEncoding() throws IOException {

		Set<String> charsets = new HashSet<String>();
		String[] charsetsToBeTested = { "IBM865", REGTOPP_DEFAULT_ENCODING }; // Should cover them all

		List<Path> list = FileUtil.listFiles(path, "*");
		for (Path fileName : list) {
			String name = fileName.getFileName().toString().toUpperCase();
			if (name.endsWith("HPL") || name.endsWith("LIN")|| name.endsWith("MRK")) {

				Charset charset = detectCharset(fileName.toFile(), charsetsToBeTested);
				if (charset != null) {
					charsets.add(charset.toString());
				}

			}
		}

		if (charsets.size() == 1) {
			// Only if single match is detected this is used
			encoding = charsets.iterator().next();
		}

	}

	public static Charset detectCharset(File f, String[] charsets) {

		Charset charset = null;

		for (String charsetName : charsets) {
			charset = detectCharset(f, Charset.forName(charsetName));
			if (charset != null) {
				break;
			}
		}

		return charset;
	}

	private static Charset detectCharset(File f, Charset charset) {
		try {

			CharsetDecoder decoder = charset.newDecoder();
			byte[] content = IOUtils.toByteArray(new FileInputStream(f));
			boolean identified = identify(content, decoder);

			if (identified) {
				log.debug("Detected charset " + charset + " for file " + f.getName());
				String checkNorwegianCharacters = new String(content, charset);
				if (containsNorwegianCharacers(checkNorwegianCharacters)) {
					return charset;
				}
			} else {
			}
		} catch (Exception e) {
		}
		return null;
	}

	private static boolean containsNorwegianCharacers(String s) {
		for (String c : norwegianChars) {
			if (s.contains(c)) {
				return true;
			}
		}

		return false;
	}

	private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
		try {
			decoder.decode(ByteBuffer.wrap(bytes));
		} catch (CharacterCodingException e) {
			return false;
		}
		return true;
	}

	private void guessVersion() throws IOException {
		if (hasFileExtension(path, ".TDA")) {
			detectedVersion = RegtoppVersion.R11D;
		} else if (hasFileExtension(path, ".STP")) {
			detectedVersion = RegtoppVersion.R13A;
		} else {
			int lineLength = findLineLength(encoding, path, ".HPL");
			if (lineLength == 87) {
				detectedVersion = RegtoppVersion.R12;
			} else if (lineLength == 89) {
				detectedVersion = RegtoppVersion.R12N;
			} else {
				log.error("Error detecting Regtopp version: Unexpected HPL line length: " + lineLength);
			}
		}
	}

	private int findLineLength(String charset, Path rootDir, String fileExtension) throws IOException {

		List<Path> list = FileUtil.listFiles(rootDir, "*");
		for (Path fileName : list) {
			String name = fileName.getFileName().toString().toUpperCase();
			if (name.endsWith(fileExtension)) {
				FileInputStream is = new FileInputStream(fileName.toFile());
				InputStreamReader isr = new InputStreamReader(is, charset);
				BufferedReader buffReader = new BufferedReader(isr);
				String line = buffReader.readLine();
				int lineLength = line.length();
				buffReader.close();
				return lineLength;
			}
		}

		return -1;
	}

	private boolean hasFileExtension(Path rootDir, String fileExtension) throws IOException {
		List<Path> list = FileUtil.listFiles(rootDir, "*");
		for (Path fileName : list) {
			String name = fileName.getFileName().toString().toUpperCase();
			if (name.endsWith(fileExtension)) {
				return true;
			}
		}

		return false;
	}

}
