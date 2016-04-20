package mobi.chouette.exchange.regtopp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;

import mobi.chouette.common.FileUtil;
import mobi.chouette.exchange.regtopp.model.importer.parser.FileContentParser;

/*
 * This class extracts a single line (transmodel concept) from a regtopp1.2 dataset
 * It deliberately does not use the beanio implementation in order not to duplicate any parsing errors (code errors) there
 */
public class TestDataExtractor {

	private final Map<String, Set<String>> referenceMap = new HashMap<String, Set<String>>();
	private final List<String> fileExtensions = new ArrayList<String>();

	public void extractLine(File sourceZipFile, File destZipFile, String adminCode, String counter, String lineId, String version) throws IOException, ArchiveException {

		File tmpFolder = null;

		try {
			// Create tmp folder for source files
			File sysTmp = new File(System.getProperty("java.io.tmpdir"));
			tmpFolder = new File(sysTmp, "" + System.currentTimeMillis());

			// Folder to unzip original files into
			File inputFolder = new File(tmpFolder, "input");

			// Where to write new files
			File outputFolder = new File(tmpFolder, "output");

			inputFolder.mkdirs();
			outputFolder.mkdirs();

			FileUtil.uncompress(sourceZipFile.getAbsolutePath(), inputFolder.getAbsolutePath());

			// Register all files in a given parse order
			registerFileExtension("FRM");
			registerFileExtension("TIX");
			registerFileExtension("TMS");
			registerFileExtension("TDA");
			registerFileExtension("HPL");
			registerFileExtension("DKO");
			registerFileExtension("DST");
			registerFileExtension("MRK");
			registerFileExtension("GAV");
			registerFileExtension("SAM");
			registerFileExtension("SON");
			registerFileExtension("LIN");
			registerFileExtension("VLP");
			registerFileExtension("TAB");
			registerFileExtension("PER");
			registerFileExtension("RUT");

			// Read in given order
			for (String fileExtension : fileExtensions) {

				// Find all files
				File[] listFiles = inputFolder.listFiles();

				for (File f : listFiles) {

					// Iterate over files in folder
					if (f.getName().toUpperCase().substring(f.getName().lastIndexOf(".") + 1).equals(fileExtension)) {

						if ("SAM".equals(fileExtension) || "VLP".equals(fileExtension) || "TAB".equals(fileExtension) || "PER".equals(fileExtension)
								|| "RUT".equals(fileExtension)) {
							System.err.println("Ignoring file " + fileExtension);
							continue;
						}

						System.out.println("Found file " + f.getName());
						File outFile = new File(outputFolder, f.getName());
						outFile.createNewFile();

						// Create reader
						FileInputStream is = new FileInputStream(f);
						InputStreamReader isr = new InputStreamReader(is, FileContentParser.REGTOPP_CHARSET);
						BufferedReader buffReader = new BufferedReader(isr);

						// Create writer
						FileOutputStream os = new FileOutputStream(outFile);
						OutputStreamWriter osr = new OutputStreamWriter(os, FileContentParser.REGTOPP_CHARSET);
						BufferedWriter buffWriter = new BufferedWriter(osr);

						int lineCounter = 1;

						// Read (file content) line by line, do a check based on file type whether to keep the line

						// For each line, do a simple substring check on some fields to verify whether it should be included in the output file
						for (String line; (line = buffReader.readLine()) != null;) {

							boolean keepLine = false;

							switch (fileExtension) {
							case "TIX":
								keepLine = line.startsWith(adminCode + counter + lineId);
								if (keepLine) {
									// Register any references that are needed in other files
									registerReference("LIN", line.substring(4, 8));
									registerReference("DKO", line.substring(15, 19));
									registerReference("MRK", line.substring(22, 25));
									registerReference("MRK", line.substring(25, 28));
									registerReference("DST", line.substring(28, 32));
									if(!version.equals("1.1D")) {
										registerReference("DST", line.substring(51, 55));
									}
								}
								break;
							case "TMS":
								keepLine = line.startsWith(adminCode + counter + lineId);
								if (keepLine) {
									registerReference("HPL", line.substring(14, 22));
									registerReference("DST", line.substring(32, 36));
									registerReference("MRK", line.substring(36, 39));
								}
								break;

							case "HPL":
								// check that line starts with correct admincode and counter as well that we have a reference to this entity (in this case from
								// TMS file)
								keepLine = line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 12));
								if (keepLine) {
									registerReference("SON", line.substring(47, 53));
								}
								break;

							case "DKO":
								keepLine = (lineCounter == 1
										|| line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 8)));
								break;

							case "DST":
								keepLine = line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 8));
								break;

							case "MRK":
								keepLine = line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 7));
								break;
							case "GAV":
								keepLine = line.startsWith(adminCode + counter) && (referenceMap.get(fileExtension).contains(line.substring(4, 12))
										|| referenceMap.get(fileExtension).contains(line.substring(12, 20)));
								break;
							case "SON":
								keepLine = line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 9));
								break;
							case "LIN":
								keepLine = line.startsWith(adminCode + counter) && referenceMap.get(fileExtension).contains(line.substring(4, 8));
								break;
							case "FRM": {
								keepLine = true;
								break;
							}
							case "TDA": {
								keepLine = true;
								registerReference("HPL", line.substring(0, 8));
							}
							}

							if (keepLine) {
								System.out.println("File " + fileExtension + ": " + line);
								buffWriter.write(line);
								buffWriter.newLine();
							}
							lineCounter++;

						}
						buffReader.close();
						buffWriter.close();

					}
				}

				if (destZipFile.exists()) {
					destZipFile.delete();
				}

			}
			FileUtil.compress(outputFolder.getAbsolutePath(), destZipFile.getAbsolutePath());

		} finally

		{
			if (tmpFolder != null) {
				FileUtils.deleteQuietly(tmpFolder);
			}
		}

	}

	private void registerFileExtension(String fileExtension) {
		fileExtensions.add(fileExtension);
		Set<String> referenceSet = new HashSet<String>();
		referenceMap.put(fileExtension, referenceSet);

		// TODO Auto-generated method stub

	}

	private void registerReference(String fileExtension, String id) {

		if (!id.matches("0{1,}$")) {
			System.out.println("File " + fileExtension + " register " + id);
			Set<String> referenceSet = referenceMap.get(fileExtension);
			referenceSet.add(id);
		} else {
			System.out.println("File " + fileExtension + " ignore " + id);
		}
	}

	public static void main(String[] args) throws IOException, ArchiveException {
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/kolumbus_regtopp_20160329-20160624.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/kolumbus_line2306.zip").getAbsoluteFile(), "500", "1", "2306","1.2");
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/kolumbus_regtopp_20160329-20160624.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/kolumbus_line5560.zip").getAbsoluteFile(), "500", "1", "5560","1.2");
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/atb-20160118-20160619.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/atb_line0098.zip").getAbsoluteFile(), "161", "1", "0098","1.2");
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/atb-20160118-20160619.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/atb_line0076.zip").getAbsoluteFile(), "161", "1", "0076","1.2");
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/R0511_212F29Mars16_3.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/ot_line5001.zip").getAbsoluteFile(), "051", "1", "5001","1.2");
		new TestDataExtractor().extractLine(new File("src/test/data/fullsets/Troms-rutedata-tom-231216.zip").getAbsoluteFile(),
				new File("src/test/data/lineextracts/troms_line0002.zip").getAbsoluteFile(), "190", "0", "0002","1.1D");
	}

}
