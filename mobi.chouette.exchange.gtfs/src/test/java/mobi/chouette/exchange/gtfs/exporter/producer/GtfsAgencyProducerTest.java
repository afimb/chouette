package mobi.chouette.exchange.gtfs.exporter.producer;

import mobi.chouette.model.Company;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GtfsAgencyProducerTest {

	private GtfsAgencyProducer gtfsAgencyProducer = new GtfsAgencyProducer(null);

	@Test
	public void createURLFromOrganisationalUnit_whenShortName_useShortNameDotNo() {
		Company company = new Company();
		company.setShortName("HHH");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.HHH.no");
	}

	@Test
	public void createURLFromOrganisationalUnit_whenNoShortName_useNameDotNo() {
		Company company = new Company();
		company.setName("test");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.test.no");
	}

	@Test
	public void createURLFromOrganisationalUnit_whenIllegalCharsInName_skipIllegalChars() {
		Company company = new Company();
		company.setName("-'__&{¥[{¥[]e£e");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.ee.no");
	}


}
