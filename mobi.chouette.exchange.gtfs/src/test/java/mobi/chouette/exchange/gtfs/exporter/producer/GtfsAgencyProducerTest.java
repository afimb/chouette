package mobi.chouette.exchange.gtfs.exporter.producer;

import mobi.chouette.model.Company;

import org.testng.Assert;
import org.testng.annotations.Test;

import static mobi.chouette.common.PropertyNames.GTFS_AGENCY_PHONE_DEFAULTS;
import static mobi.chouette.common.PropertyNames.GTFS_AGENCY_URL_DEFAULTS;

public class GtfsAgencyProducerTest {

	private GtfsAgencyProducer gtfsAgencyProducer = new GtfsAgencyProducer(null);

	@Test
	public void createURLFromOrganisationalUnit_whenShortName_useShortNameDotNo() {
		Company company = new Company();
		company.setShortName("HHH");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.HHH.com");
	}

	@Test
	public void createURLFromOrganisationalUnit_whenNoShortName_useNameDotNo() {
		Company company = new Company();
		company.setName("test");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.test.com");
	}

	@Test
	public void createURLFromOrganisationalUnit_whenIllegalCharsInName_skipIllegalChars() {
		Company company = new Company();
		company.setName("-'__&{¥[{¥[]e£e");
		String url = gtfsAgencyProducer.createURLFromOrganisationalUnit(company);
		Assert.assertEquals(url, "http://www.ee.com");
	}


	@Test
	public void createURLFromProviderDefaults_whenDefaultIsSet_useDefault(){
		Company company = new Company();
		company.setObjectId("TST:Authority:432423");
		System.setProperty(GTFS_AGENCY_URL_DEFAULTS,"KOK=www.kok.se,TST=http://www.testcomp.com");
		String url=gtfsAgencyProducer.createURLFromProviderDefaults(company);
		Assert.assertEquals(url, "http://www.testcomp.com");
	}


	@Test
	public void createURLFromProviderDefaults_whenDefaultIsNotSet_useCompanyShortName(){
		Company company = new Company();
		company.setObjectId("UNK:Authority:432423");
		company.setShortName("unknowncomp");
		System.setProperty(GTFS_AGENCY_URL_DEFAULTS,"KOK=www.kok.se,TST=http://www.testcomp.com");
		String url=gtfsAgencyProducer.createURLFromProviderDefaults(company);
		Assert.assertEquals(url, "http://www.unknowncomp.com");
	}


	@Test
	public void createPhoneFromProviderDefaults(){
		Company company = new Company();
		company.setObjectId("UNK:Authority:432423");
		System.setProperty(GTFS_AGENCY_PHONE_DEFAULTS,"KOK=www.kok.se,UNK=+477777");
		String phone=gtfsAgencyProducer.createPhoneFromProviderDefaults(company);
		Assert.assertEquals(phone, "+477777");
	}
}
