package mobi.chouette.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mobi.chouette.common.ContenerChecker;

@Stateless(name = ContenerChecker.NAME)

public class DummyChecker implements ContenerChecker
{

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean validateContener(String contenerName) {
		return !contenerName.equals("toto");
		}

	@Override
	public String getContext() {
		return "Dummy";
	}
	

}
