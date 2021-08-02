package mobi.chouette.dao;

import lombok.extern.log4j.Log4j;
import mobi.chouette.model.DeadRun;
import mobi.chouette.persistence.hibernate.ContextHolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.File;
import java.sql.SQLException;

@Log4j
public class DeadRunDaoTest extends Arquillian {

    @EJB
    DeadRunDAO deadRunDAO;

    @Resource
    private UserTransaction trx;

    @Deployment
    public static WebArchive createDeployment() {

        try {
            WebArchive result;
            File[] files = Maven.resolver().loadPomFromFile("pom.xml").resolve("mobi.chouette:mobi.chouette.dao")
                    .withTransitivity().asFile();

            result = ShrinkWrap.create(WebArchive.class, "test.war").addAsWebInfResource("postgres-ds.xml")
                    .addAsLibraries(files).addAsResource(EmptyAsset.INSTANCE, "beans.xml");
            return result;
        } catch (RuntimeException e) {
            System.out.println(e.getClass().getName());
            throw e;
        }

    }


    @Test(groups = "DAO")
    public void checkCreateDatedServiceJourney() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        try {
            ContextHolder.setContext("chouette_gui"); // set tenant schema

            DeadRun deadRun = new DeadRun();
            deadRun.setObjectId("TST:" + DeadRun.DEADRUN_KEY + ":1");

            trx.begin();

            deadRunDAO.create(deadRun);

            trx.commit();

        } catch (RuntimeException ex) {
            Throwable cause = ex.getCause();
            while (cause != null) {
                log.error(cause);
                if (cause instanceof SQLException)
                    traceSqlException((SQLException) cause);
                cause = cause.getCause();
            }
            throw ex;
        }
    }


    private void traceSqlException(SQLException ex) {
        while (ex.getNextException() != null) {
            ex = ex.getNextException();
            log.error(ex);
        }
    }

}
