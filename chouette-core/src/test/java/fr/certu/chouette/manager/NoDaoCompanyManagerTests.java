package fr.certu.chouette.manager;

import org.testng.annotations.BeforeMethod;

import fr.certu.chouette.model.neptune.Company;

public class NoDaoCompanyManagerTests extends AbstractNoDaoManagerTests<Company> {

	@BeforeMethod (alwaysRun=true)
	public void createManager()
	{
		Company bean = new Company();
		bean.setId(Long.valueOf(1));
		bean.setObjectId("TestNG:Company:1");
		initManager("Company","companyManager",bean);
	}


}
