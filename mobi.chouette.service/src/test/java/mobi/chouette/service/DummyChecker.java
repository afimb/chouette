package mobi.chouette.service;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless(name = ContenerCheckerInterface.NAME)

public class DummyChecker implements ContenerCheckerInterface
{

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean validateContener(String contenerName) {
		return !contenerName.equals("toto");
		}
	

}
