package mobi.chouette.service;

import lombok.extern.log4j.Log4j;
import mobi.chouette.dao.ReferentialDAO;
import mobi.chouette.dao.ReferentialLastUpdateDAO;
import mobi.chouette.model.dto.ReferentialInfo;
import mobi.chouette.persistence.hibernate.ContextHolder;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 */
@Singleton(name = ReferentialService.BEAN_NAME)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Log4j
public class ReferentialService {

    private static final String MIGRATED_SCHEMA_PREFIX = "rb_";
    public static final String BEAN_NAME = "ReferentialService";

    @EJB
    ReferentialDAO referentialDAO;

    @EJB
    ReferentialLastUpdateDAO referentialLastUpdateDAO;

    private String defaultReferentialAdminUserName;
    private String defaultReferentialAdminEmailFormat;
    private String defaultReferentialUserEmailFormat;
    private String defaultDataspaceBounds;
    private String defaultDataspaceTimezone;

    private String defaultMasterUserEmail;
    private String defaultMasterOrganisationName;

    private String adminUserInitialEncryptedPassword;
    private String userInitialEncryptedPassword;


    @PostConstruct
    public void init() {

        defaultReferentialAdminUserName = System.getProperty("iev.referential.admin.username", "Entur Admin");
        defaultReferentialAdminEmailFormat = System.getProperty("iev.referential.admin.email.format", "admin+%s@entur.org");
        defaultReferentialUserEmailFormat = System.getProperty("iev.referential.user.email.format", "%s@entur.org");
        defaultDataspaceBounds = System.getProperty("iev.referential.dataspace.bounds", "SRID=4326;POLYGON((3.0 57.0,3.0 62.0,10.0 62.0,10.0 57.0,3.0 57.0))");
        defaultDataspaceTimezone = System.getProperty("iev.referential.dataspace.timezone", "Paris");

        defaultMasterUserEmail = System.getProperty("iev.referential.master.user.email", "admin@entur.org");
        defaultMasterOrganisationName = System.getProperty("iev.referential.master.organisation.name", "Rutebanken");

        adminUserInitialEncryptedPassword = System.getenv("IEV_REFERENTIAL_ADMIN_INITIAL_ENCRYPTED_PASSWORD");
        userInitialEncryptedPassword = System.getenv("IEV_REFERENTIAL_USER_INITIAL_ENCRYPTED_PASSWORD");

        if (adminUserInitialEncryptedPassword == null) {
            throw new IllegalStateException("Environment variable IEV_REFERENTIAL_ADMIN_INITIAL_ENCRYPTED_PASSWORD is not set");
        }
        if (userInitialEncryptedPassword == null) {
            throw new IllegalStateException("Environment variable IEV_REFERENTIAL_USER_INITIAL_ENCRYPTED_PASSWORD is not set");
        }

    }

    public boolean createReferential(ReferentialInfo referentialInfo) throws ServiceException {

        log.info("Creating referential for: " + referentialInfo);

        String schemaName = referentialInfo.getSchemaName();

        if (referentialDAO.hasReferential(schemaName)) {
            log.warn("The referential already exists: " + schemaName + ". Ignoring creation request");
            return false;
        }

        if (referentialInfo.getAdminUserName() == null) {
            referentialInfo.setAdminUserName(defaultReferentialAdminUserName);
        }
        if (referentialInfo.getAdminUserEmail() == null) {
            referentialInfo.setAdminUserEmail(String.format(defaultReferentialAdminEmailFormat, schemaName));
        }
        if (referentialInfo.getUserEmail() == null) {
            referentialInfo.setUserEmail(String.format(defaultReferentialUserEmailFormat, schemaName));
        }
        if (referentialInfo.getDataspaceBounds() == null) {
            referentialInfo.setDataspaceBounds(defaultDataspaceBounds);
        }
        if (referentialInfo.getDataspaceTimezone() == null) {
            referentialInfo.setDataspaceTimezone(defaultDataspaceTimezone);
        }
        if (referentialInfo.getMasterUserEmail() == null) {
            referentialInfo.setMasterUserEmail(defaultMasterUserEmail);
        }
        if (referentialInfo.getMasterOrganisationName() == null) {
            referentialInfo.setMasterOrganisationName(defaultMasterOrganisationName);
        }

        referentialInfo.setAdminUserInitialEncryptedPassword(adminUserInitialEncryptedPassword);
        referentialInfo.setUserInitialEncryptedPassword(userInitialEncryptedPassword);


        if (schemaName.startsWith(MIGRATED_SCHEMA_PREFIX)) {
            referentialInfo.setDataspacePrefix(schemaName.replace(MIGRATED_SCHEMA_PREFIX, "").toUpperCase());
            referentialDAO.createMigratedReferential(referentialInfo);

        } else {
            referentialInfo.setDataspacePrefix(schemaName.toUpperCase());
            referentialDAO.createReferential(referentialInfo);
        }

        log.info("Created referential for: " + referentialInfo);

        return true;

    }

    public void updateReferential(ReferentialInfo referentialInfo) throws ServiceException {

        log.info("Updating referential for: " + referentialInfo);

        String schemaName = referentialInfo.getSchemaName();
        if (!referentialDAO.getReferentials().contains(schemaName)) {
            throw new ServiceException(ServiceExceptionCode.INVALID_REQUEST, "Cannot update referential: referential not found: " + referentialInfo);
        }

        boolean updated;
        if (schemaName.startsWith(MIGRATED_SCHEMA_PREFIX)) {
            if (referentialInfo.getMasterOrganisationName() == null) {
                referentialInfo.setMasterOrganisationName(defaultMasterOrganisationName);
            }
            updated = referentialDAO.updateMigratedReferential(referentialInfo);

        } else {
            updated = referentialDAO.updateReferential(referentialInfo);
        }
        if (!updated) {
            throw new ServiceException(ServiceExceptionCode.INTERNAL_ERROR, "Cannot update referential: internal error: " + referentialInfo);
        }

        log.info("Updated referential for: " + referentialInfo);
    }


    public void deleteReferential(ReferentialInfo referentialInfo) throws ServiceException {

        log.info("Deleting referential for: " + referentialInfo);

        String schemaName = referentialInfo.getSchemaName();
        if (!referentialDAO.getReferentials().contains(schemaName)) {
            throw new ServiceException(ServiceExceptionCode.INVALID_REQUEST, "Cannot delete referential: referential not found: " + referentialInfo);
        }

        if (referentialInfo.getUserEmail() == null) {
            referentialInfo.setUserEmail(String.format(defaultReferentialUserEmailFormat, schemaName));
        }
        if (referentialInfo.getAdminUserEmail() == null) {
            referentialInfo.setAdminUserEmail(String.format(defaultReferentialAdminEmailFormat, schemaName));
        }

        referentialDAO.deleteReferential(referentialInfo);

        log.info("Deleted referential for: " + referentialInfo);
    }

    public List<String> getReferentialCodes() {
        return referentialDAO.getReferentials();
    }

    public LocalDateTime getLastUpdateTimestamp(String referential) throws ServiceException {

        if (!referentialDAO.getReferentials().contains(referential)) {
            throw new ServiceException(ServiceExceptionCode.INVALID_REQUEST, "Cannot retrieve last update timestamp: referential not found: " + referential);
        }
        ContextHolder.setContext(referential);
        return referentialLastUpdateDAO.getLastUpdateTimestamp();
    }
}
