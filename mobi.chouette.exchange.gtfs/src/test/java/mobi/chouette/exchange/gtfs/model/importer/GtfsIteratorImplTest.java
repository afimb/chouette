package mobi.chouette.exchange.gtfs.model.importer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsIteratorImplTest {

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax")
	public void verifySyntax() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);

			Assert.assertTrue(reader.next(), "check line 1");
			for (int i = 0; i < reader.getFieldCount(); i++)
			{
				System.out.println(reader.getValue(i));
			}
			Assert.assertEquals(reader.getFieldCount(), 5, "check token count");
			Assert.assertEquals(reader.getValue(0), "toto", "check item 0");
			Assert.assertEquals(reader.getValue(1), "\"titi\"", "check item 1");
			Assert.assertEquals(reader.getValue(2), "tutu", "check item 2");
			Assert.assertEquals(reader.getValue(3), "tata,toto", "check item 3");
			Assert.assertEquals(reader.getValue(4), "toto\"tata", "check item 4");
			Assert.assertFalse(reader.next(), "check line 2");
			Assert.assertFalse(reader.next(), "check line 3");
			Assert.assertFalse(reader.next(), "check line 4");
			Assert.assertTrue(reader.next(), "check line 5");
		} finally {
			file.close();
		}
	}

}
