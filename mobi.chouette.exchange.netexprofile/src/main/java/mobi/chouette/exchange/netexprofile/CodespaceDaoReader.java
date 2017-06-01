package mobi.chouette.exchange.netexprofile;

import mobi.chouette.dao.CodespaceDAO;
import mobi.chouette.model.Codespace;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class CodespaceDaoReader {

    @EJB
    protected CodespaceDAO codespaceDAO;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Set<Codespace> loadCodespaces() {
        Set<Codespace> codespaces = new HashSet<>();
        codespaces.addAll(codespaceDAO.findAll());
        return codespaces;
    }

}
