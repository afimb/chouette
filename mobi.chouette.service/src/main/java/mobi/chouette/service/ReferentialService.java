package mobi.chouette.service;

import mobi.chouette.dao.iev.ReferentialDAO;

import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import java.util.List;

@Stateless(name = ReferentialService.BEAN_NAME)
@Startup
public class ReferentialService {

    public static final String BEAN_NAME = "ReferentialService";

    @EJB
    ReferentialDAO referentialDAO;


    public List<String> getReferentials(){
        return referentialDAO.getReferentials();
    }
}
