package fr.certu.chouette.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:testContext.xml", "classpath*:/chouetteContext.xml" })
public class CommandTest extends AbstractTestNGSpringContextTests
{

	@BeforeMethod(alwaysRun = true)
	public void initDao()
	{
		Command.setBeanFactory(applicationContext);
		Command.initDao();
	}

	@AfterMethod(alwaysRun = true)
	public void closeDao()
	{
		Command.setBeanFactory(applicationContext);
		Command.closeDao();
	}


	@Parameters ({"name"})
	@Test(groups = { "command" }, description = "help should work" )
	public void verifyCommand(String name) throws IOException
	{
		Command command = (Command) applicationContext.getBean("Command");
		String[] args = { "-f", "src/test/data/script_"+name+".txt"};
		PrintStream oldOut = System.out;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream stringOut = new PrintStream(baos,true,"UTF-8");
		System.setOut(stringOut);
		command.execute(args);
		System.setOut(oldOut);
		stringOut.flush();
		String result = baos.toString();
		FileWriter w = new FileWriter(new File("target/surefire-reports/result_"+name+".txt"));
		w.write(result);
		w.close();
		Assert.assertFalse(result.isEmpty(), name+" must give result on console");
		String[] lines = result.split("\n");
		List<String> expectedLines = FileUtils.readLines(new File("src/test/data/result_"+name+".txt"));
		Assert.assertEquals(lines.length,expectedLines.size(),name+" must give "+expectedLines.size()+" lines");
		for (int i = 0; i < lines.length; i++)
		{
			if (expectedLines.get(i).contains("creationTime"))
			{
				Assert.assertTrue(lines[i].contains("creationTime"),name+" : line number "+i+" should contains creationTime");
			}
			else
			{
				Assert.assertEquals(lines[i],expectedLines.get(i),name+" : line number "+i+" differs");
			}
		}
	}

}