package mobi.chouette.scheduler;

import java.io.File;
import java.nio.file.Files;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.JSONUtil;

import org.apache.log4j.BasicConfigurator;
import org.testng.annotations.Test;

@Log4j
public class ParametersTest {

	@Test
	public void test() throws Exception {
		
		BasicConfigurator.configure();
		
		String filename = "src/test/resources/parameters.json";
		File f = new File(filename);
		byte[] bytes = Files.readAllBytes(f.toPath());
		String text = new String(bytes, "UTF-8");
		Parameters parameters = (Parameters) JSONUtil.fromJSON(text, Parameters.class);
		// log.info("ParametersTest.test() : \n" + payload.toString());

		String result = JSONUtil.toJSON(parameters);
		//log.info("ParametersTest.test() : \n" + result);
	}

}
