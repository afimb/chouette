package mobi.chouette.dao;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.dto.ReferentialInfo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

@Stateless(name = "ReferentialDAO")
@Log4j
public class ReferentialDAOImpl implements ReferentialDAO {

    private static final String SQL_SELECT_REFERENTIAL = "SELECT * FROM PUBLIC.REFERENTIALS WHERE slug=:dest_schema";

    private static final String SQL_SELECT_SLUG = "SELECT SLUG FROM PUBLIC.REFERENTIALS";

    private static final String SQL_UPDATE_REFERENTIAL = "UPDATE public.referentials " +
            "SET updated_at=current_timestamp, name=:dataspace_name, projection_type=:dataspace_projection, data_format=:dataspace_format WHERE slug=:dest_schema";

    private static final String SQL_DROP_SCHEMA = "DROP SCHEMA %s CASCADE";

    private static final String SQL_DELETE_REFERENTIAL = "DELETE FROM public.referentials WHERE slug=:dest_schema";

    private static final String SQL_DELETE_USERS = "DELETE FROM public.users WHERE email=:email";

    @PersistenceContext(unitName = "public")
    private EntityManager em;

    @Override
    public boolean hasReferential(String referentialName) {
        Query query = em.createNativeQuery(SQL_SELECT_REFERENTIAL);
        query.setParameter("dest_schema", referentialName);
        return !query.getResultList().isEmpty();
    }

    @Override
    public List<String> getReferentials() {
        Query query = em.createNativeQuery(SQL_SELECT_SLUG);
        return query.getResultList();
    }

    @Override
    public void createReferential(ReferentialInfo referentialInfo) {

        StoredProcedureQuery procedureQuery = em.createStoredProcedureQuery("public.create_provider_schema");

        procedureQuery.registerStoredProcedureParameter("dest_schema", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_format", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_encrypted_password", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("user_encrypted_password", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("organisation_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_prefix", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_projection", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_timezone", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_bounds", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns_url", String.class, ParameterMode.IN);

        procedureQuery.setParameter("dest_schema", referentialInfo.getSchemaName());
        procedureQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        procedureQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        procedureQuery.setParameter("admin_user_name", referentialInfo.getAdminUserName());
        procedureQuery.setParameter("admin_user_email", referentialInfo.getAdminUserEmail());
        procedureQuery.setParameter("admin_user_encrypted_password", referentialInfo.getAdminUserInitialEncryptedPassword());
        procedureQuery.setParameter("user_name", referentialInfo.getUserName());
        procedureQuery.setParameter("user_email", referentialInfo.getUserEmail());
        procedureQuery.setParameter("user_encrypted_password", referentialInfo.getUserInitialEncryptedPassword());
        procedureQuery.setParameter("organisation_name", referentialInfo.getOrganisationName());
        procedureQuery.setParameter("dataspace_prefix", referentialInfo.getDataspacePrefix());
        procedureQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        procedureQuery.setParameter("dataspace_timezone", referentialInfo.getDataspaceTimezone());
        procedureQuery.setParameter("dataspace_bounds", referentialInfo.getDataspaceBounds());
        procedureQuery.setParameter("xmlns", referentialInfo.getXmlns());
        procedureQuery.setParameter("xmlns_url", referentialInfo.getXmlnsUrl());

        procedureQuery.execute();
    }

    @Override
    public void createMigratedReferential(ReferentialInfo referentialInfo) {

        StoredProcedureQuery procedureQuery = em.createStoredProcedureQuery("public.create_rutebanken_schema");

        procedureQuery.registerStoredProcedureParameter("dest_schema", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_format", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("admin_user_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("master_organisation_name", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("master_user_email", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_prefix", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_projection", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_timezone", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("dataspace_bounds", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns", String.class, ParameterMode.IN);
        procedureQuery.registerStoredProcedureParameter("xmlns_url", String.class, ParameterMode.IN);

        procedureQuery.setParameter("dest_schema", referentialInfo.getSchemaName());
        procedureQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        procedureQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        procedureQuery.setParameter("admin_user_name", referentialInfo.getAdminUserName());
        procedureQuery.setParameter("master_organisation_name", referentialInfo.getMasterOrganisationName());
        procedureQuery.setParameter("master_user_email", referentialInfo.getMasterUserEmail());
        procedureQuery.setParameter("dataspace_prefix", referentialInfo.getDataspacePrefix());
        procedureQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        procedureQuery.setParameter("dataspace_timezone", referentialInfo.getDataspaceTimezone());
        procedureQuery.setParameter("dataspace_bounds", referentialInfo.getDataspaceBounds());
        procedureQuery.setParameter("xmlns", referentialInfo.getXmlns());
        procedureQuery.setParameter("xmlns_url", referentialInfo.getXmlnsUrl());

        procedureQuery.execute();

    }

    @Override
    public boolean updateReferential(ReferentialInfo referentialInfo) {
        Query updateReferentialQuery = em.createNativeQuery(SQL_UPDATE_REFERENTIAL);
        updateReferentialQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        updateReferentialQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        updateReferentialQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        updateReferentialQuery.setParameter("dest_schema", referentialInfo.getSchemaName());

        int nbModifiedRow = updateReferentialQuery.executeUpdate();
        return nbModifiedRow != 0;
    }

    @Override
    public boolean updateMigratedReferential(ReferentialInfo referentialInfo) {
        Query updateReferentialQuery = em.createNativeQuery(SQL_UPDATE_REFERENTIAL);
        updateReferentialQuery.setParameter("dataspace_projection", referentialInfo.getDataspaceProjection());
        updateReferentialQuery.setParameter("dataspace_format", referentialInfo.getDataspaceFormat());
        updateReferentialQuery.setParameter("dataspace_name", referentialInfo.getDataspaceName());
        updateReferentialQuery.setParameter("dest_schema", referentialInfo.getSchemaName());

        int nbModifiedRow = updateReferentialQuery.executeUpdate();
        return nbModifiedRow != 0;
    }

    @Override
    public boolean deleteReferential(ReferentialInfo referentialInfo) {

        Query dropSchemaQuery = em.createNativeQuery(String.format(SQL_DROP_SCHEMA, referentialInfo.getSchemaName()));
        dropSchemaQuery.executeUpdate();

        Query deleteUserQuery = em.createNativeQuery(SQL_DELETE_USERS);
        deleteUserQuery.setParameter("email", referentialInfo.getUserEmail());
        deleteUserQuery.executeUpdate();
        deleteUserQuery.setParameter("email", referentialInfo.getAdminUserEmail());
        deleteUserQuery.executeUpdate();

        Query deleteReferentialQuery = em.createNativeQuery(SQL_DELETE_REFERENTIAL);
        deleteReferentialQuery.setParameter("dest_schema", referentialInfo.getSchemaName());

        int nbModifiedRow = deleteReferentialQuery.executeUpdate();
        return nbModifiedRow != 0;
    }

}
