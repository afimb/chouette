package fr.certu.chouette.struts.massiveExport;

import com.opensymphony.xwork2.Preparable;
import fr.certu.chouette.modele.Reseau;
import fr.certu.chouette.service.database.IReseauManager;
import fr.certu.chouette.service.exportateur.IMassiveExportManager;
import fr.certu.chouette.service.exportateur.impl.MassiveExportManager;
import fr.certu.chouette.struts.GeneriqueAction;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class MassiveExportAction extends GeneriqueAction implements Preparable {

    private static final long                  serialVersionUID = 1200152356288194220L;
    private static final Logger                logger           = Logger.getLogger(MassiveExportAction.class);
    private              long                  networkId;
    private              Date                  startDate;
    private              Date                  endDate;
    private              boolean               excludeConnectionLinks;
    private              IReseauManager        networkManager;
    private              IMassiveExportManager massiveExportManager;
    private              List<Reseau>          networks;
    
    @Override
    public void prepare() {
        logger.debug("Prepare massive export");
        this.networks = new ArrayList<Reseau>(networkManager.lire());
    }
    
    public String list() {
        return LIST;
    }
    
    public String exportNetwork() throws Exception {
        logger.debug("exportNetwork");
        this.massiveExportManager.exportNetworkInBackground(networkId, startDate, endDate, excludeConnectionLinks);
        addActionMessage(getText("message.massiveExport.network", new String[] {this.massiveExportManager.getNotificationEmailAddress()}));
        return EXPORT;
    }

    @Override
    public void validate() {
        if (this.massiveExportManager.isPending())
            addActionError(getText("errors.export.pending"));
    }
    
    public List<File> getExportFiles() {
        File exportDir = new File(MassiveExportManager.EXPORT_DIR);
        return Arrays.asList(exportDir.listFiles());
    }
    
    public void setNetworks(List<Reseau> networks) {
        this.networks = networks;
    }
    
    public List<Reseau> getTest() {
        return this.networks;
    }
    
    public void setNetworkManager(IReseauManager networkManager) {
        this.networkManager = networkManager;
    }
    
    public void setMassiveExportManager(IMassiveExportManager massiveExportManager) {
        this.massiveExportManager = massiveExportManager;
    }
    
    public void setNetworkId(long networkId) {
        this.networkId = networkId;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setExcludeConnectionLinks(boolean excludeConnectionLinks) {
        this.excludeConnectionLinks = excludeConnectionLinks;
    }
    
    public boolean isExcludeConnectionLinks() {
        return excludeConnectionLinks;
    }
}
