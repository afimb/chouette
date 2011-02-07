package unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import fr.certu.chouette.modele.InterdictionTraficLocal;
import fr.certu.chouette.modele.Ligne;
import fr.certu.chouette.modele.PositionGeographique;
import fr.certu.chouette.service.commun.CodeIncident;
import fr.certu.chouette.service.commun.ServiceException;
import fr.certu.chouette.service.database.IITLManager;
import fr.certu.chouette.service.database.ILigneManager;
import fr.certu.chouette.service.database.IPositionGeographiqueManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public class ITLTest extends AbstractTestNGSpringContextTests {

    private static final Logger logger = Logger.getLogger(ITLTest.class);
    private IPositionGeographiqueManager positionGeographiqueManager;
    private ILigneManager ligneManager;
    private fr.certu.chouette.service.database.IITLManager itlManager;

    @BeforeMethod
    protected void getBeans() throws Exception {
        positionGeographiqueManager = (IPositionGeographiqueManager) applicationContext.getBean("positionGeographiqueManager");
        ligneManager = (ILigneManager) applicationContext.getBean("ligneManager");
        itlManager = (IITLManager) applicationContext.getBean("itlManager");
    }

    @Test(groups = "tests unitaires", description = "gestion des itl")
    public void crud_itl() {
        logger.debug("Appel");

        assert ligneManager != null;

        Ligne uneLigne = GenerateurDonnee.creerLigne();
        ligneManager.creer(uneLigne);

        int maxArrets = 6;
        List<Long> arretsPhysiquesIds = new ArrayList<Long>();
        List<Long> arretsPhysiquesCrees = new ArrayList<Long>();
        for (int i = 0; i < maxArrets; i++) {
            PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique("");
            positionGeographiqueManager.creer(arretPhysique);
            arretsPhysiquesIds.add(arretPhysique.getId());
            arretsPhysiquesCrees.add(arretPhysique.getId());
        }

        InterdictionTraficLocal itl = new InterdictionTraficLocal();
        itl.setNom("tteyge");
        itl.setIdLigne(uneLigne.getId());
        itl.setArretPhysiqueIds(arretsPhysiquesIds);

        itlManager.creer(itl);

        InterdictionTraficLocal itlLu = itlManager.lire(itl.getId());
        assert itlLu.getObjectId().equals(itl.getObjectId());
        assert itlLu.getNom().equals(itl.getNom());

        List<Long> arretsPhysiquesIdsLus = new ArrayList<Long>(itl.getArretPhysiqueIds());
        arretsPhysiquesIdsLus.removeAll(arretsPhysiquesIds);
        assert arretsPhysiquesIdsLus.size() == 0;

        assert itl.getArretPhysiqueIds().size() == arretsPhysiquesIds.size();

        // modification
        logger.debug(arretsPhysiquesIds.size());
        arretsPhysiquesIds.remove(0);
        arretsPhysiquesIds.remove(0);
        itl.setArretPhysiqueIds(arretsPhysiquesIds);

        itlManager.modifier(itl);

        itlLu = itlManager.lire(itl.getId());
        arretsPhysiquesIdsLus = itl.getArretPhysiqueIds();
        arretsPhysiquesIdsLus.removeAll(arretsPhysiquesIds);
        assert arretsPhysiquesIdsLus.size() == 0;


        itlManager.supprimer(itl.getId());
        ligneManager.supprimer(uneLigne.getId());

        for (Long idPhysique : arretsPhysiquesCrees) {
            positionGeographiqueManager.supprimer(idPhysique);
        }
    }

    @Test(groups = "tests unitaires",
    description = "suppression des ITL à partir d'une suppression de ligne")
    public void suppression_itl_sur_suppression_ligne() {
        logger.debug("Appel");

        assert ligneManager != null;

        Ligne uneLigne = GenerateurDonnee.creerLigne();
        ligneManager.creer(uneLigne);

        int maxArrets = 6;
        List<Long> arretsPhysiquesIds = new ArrayList<Long>();
        List<Long> arretsPhysiquesCrees = new ArrayList<Long>();
        for (int i = 0; i < maxArrets; i++) {
            PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique("");
            positionGeographiqueManager.creer(arretPhysique);
            arretsPhysiquesIds.add(arretPhysique.getId());
            arretsPhysiquesCrees.add(arretPhysique.getId());
        }

        InterdictionTraficLocal itl = new InterdictionTraficLocal();
        itl.setNom("tteyge");
        itl.setIdLigne(uneLigne.getId());
        itl.setArretPhysiqueIds(arretsPhysiquesIds);

        itlManager.creer(itl);

        ligneManager.supprimer(uneLigne.getId());

        try {
            itlManager.lire(itl.getId());
            assert false : "l'itl de la ligne aurait du etre supprimée";
        } catch (ServiceException e) {
            assert e.getCode().equals(CodeIncident.IDENTIFIANT_INCONNU);
        }

        for (Long idPhysique : arretsPhysiquesCrees) {
            positionGeographiqueManager.supprimer(idPhysique);
        }
    }

    @Test(groups = "tests unitaires",
    description = "maj des arrets d'une ITL à partir d'une suppression d'arret physique")
    public void maj_itl_sur_suppression_arret() {
        logger.debug("Appel");

        assert ligneManager != null;

        Ligne uneLigne = GenerateurDonnee.creerLigne();
        ligneManager.creer(uneLigne);

        int maxArrets = 6;
        List<Long> arretsPhysiquesIds = new ArrayList<Long>();
        List<Long> arretsPhysiquesCrees = new ArrayList<Long>();
        for (int i = 0; i < maxArrets; i++) {
            PositionGeographique arretPhysique = GenerateurDonnee.creerArretPhysique("");
            positionGeographiqueManager.creer(arretPhysique);
            arretsPhysiquesIds.add(arretPhysique.getId());
            arretsPhysiquesCrees.add(arretPhysique.getId());
        }

        InterdictionTraficLocal itl = new InterdictionTraficLocal();
        itl.setNom("tteyge");
        itl.setIdLigne(uneLigne.getId());
        itl.setArretPhysiqueIds(arretsPhysiquesIds);

        itlManager.creer(itl);

        Set<Long> idPhysiqueSupprimes = new HashSet<Long>();
        for (Long idPhysique : arretsPhysiquesCrees) {
            positionGeographiqueManager.supprimer(idPhysique);
            idPhysiqueSupprimes.add(idPhysique);

            InterdictionTraficLocal itlLue = itlManager.lire(itl.getId());

            Set<Long> idPhysiqueITL = new HashSet<Long>(itlLue.getArretPhysiqueIds());

            Set<Long> idPhysiqueITLAttendus = new HashSet<Long>(itlLue.getArretPhysiqueIds());
            idPhysiqueITLAttendus.removeAll(idPhysiqueSupprimes);

            assert idPhysiqueITL.containsAll(idPhysiqueITLAttendus) : "Les arrets physiques supprimés " + idPhysiqueITL + " référence toujours son itl " + itl.getId();
            assert idPhysiqueITL.size() == idPhysiqueITLAttendus.size() : "Les arrets physiques de l'ITL " + itl.getId() + " se sont pas correctement mis à jour"
                    + ", idPhysiqueITL=" + idPhysiqueITL + ", idPhysiqueITLAttendus=" + idPhysiqueITLAttendus;
        }

        ligneManager.supprimer(uneLigne.getId());
    }
}
