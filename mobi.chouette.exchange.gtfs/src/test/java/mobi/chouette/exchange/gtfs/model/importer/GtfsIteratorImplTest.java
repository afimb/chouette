package mobi.chouette.exchange.gtfs.model.importer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsIteratorImplTest {

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax is ok")
	public void verifySyntax_ok() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_ok.txt", "r");

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
			Assert.assertEquals(reader.getFieldCount(), 7, "check token count");
			Assert.assertEquals(reader.getValue(0), "toto", "check item 0");
			Assert.assertEquals(reader.getValue(1), "\"titi\"", "check item 1");
			Assert.assertEquals(reader.getValue(2), "tutu", "check item 2");
			Assert.assertEquals(reader.getValue(3), "tata,toto", "check item 3");
			Assert.assertEquals(reader.getValue(4), "toto\"tata", "check item 4");
			Assert.assertEquals(reader.getValue(5), "");
			Assert.assertEquals(reader.getValue(6), "");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: No ending DQUOTE")
	public void verifySyntax_1() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_1.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertFalse(reader.next(), "check line with no ending DQUOTE");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: No starting DQUOTE")
	public void verifySyntax_2_1() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_2_1.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertFalse(reader.next(), "check line with no starting DQUOTE");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: No starting DQUOTE too")
	public void verifySyntax_2_2() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_2_2.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertFalse(reader.next(), "check line with no starting DQUOTE too");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: DQUOTE in the middle")
	public void verifySyntax_3() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_3.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertFalse(reader.next(), "check line with DQUOTE in the middle");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: No DQUOTE")
	public void verifySyntax_4() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_4.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertTrue(reader.next(), "check line without DQUOTE");
		} finally {
			file.close();
		}
	}

	@Test(groups = { "GtfsIteratorImpl" }, description = "test csv syntax not ok: Many DQUOTEs")
	public void verifySyntax_5() throws Exception {
		RandomAccessFile file = new RandomAccessFile("src/test/data/syntax_gtfs_5.txt", "r");

		try {
			FileChannel channel1 = file.getChannel();
			long length = channel1.size();

			MappedByteBuffer buffer = channel1.map(FileChannel.MapMode.READ_ONLY, 0, length);
			buffer.load();
			GtfsIteratorImpl reader = new GtfsIteratorImpl(buffer, 0);
			Assert.assertTrue(reader.next(), "check line with many DQUOTEs");
			System.out.println(reader.getValue(0));
			Assert.assertEquals(reader.getValue(0), "Kerniol-Cliscouët \"\"Rocade\"\"", "check item 0");
		} finally {
			file.close();
		}
	}
}
