package mobi.chouette.common;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

import lombok.extern.log4j.Log4j;

@Log4j
public class ResourceUtil {

	public static long usedMemory()
	{
		MemoryUsage memory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

// 		NumberFormat format = NumberFormat.getInstance();

// 		long maxMemory = memory.getMax();
		long allocatedMemory = memory.getUsed();
		long commitedMemory = memory.getCommitted();
		long usedMemory = allocatedMemory * 100 / commitedMemory;

//		log.info("commited memory: " + format.format(commitedMemory / 1024) + " ko");
//		log.info("allocated memory: " + format.format(allocatedMemory / 1024) + " ko");
//		log.info("max memory: " + format.format(maxMemory / 1024) + " ko");
//		log.info("used memory: " + format.format(usedMemory) + " %");
	
		return usedMemory;
	}
	
	public static boolean waitForMemory(long amount,int maxWait)
	{
		long mem = usedMemory();
		log.info("before wait : used memory: " + mem + " %");
		if (mem < amount) return true;
		while (maxWait > 0)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}
			maxWait --;
			mem = usedMemory();
			log.info("before wait ("+maxWait+"): used memory: " + mem + " %");
			if (mem < amount) return true;
		}
		return false;
	}
	
	public static void waitForMemory() throws Exception
	{
		if (!waitForMemory(80, 5))
			throw new Exception("Not enought memory to process");
	}

	
}
