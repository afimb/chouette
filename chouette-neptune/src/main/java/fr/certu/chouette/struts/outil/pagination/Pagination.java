package fr.certu.chouette.struts.outil.pagination;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public class Pagination
{

  private static final Log log = LogFactory.getLog(Pagination.class);
  private Integer nbTotalPages;
  private Integer nbTotalColonnes;
  private Integer nbColonnesParPage;
  private Integer numeroPageCourante;

  public <T extends NeptuneIdentifiedObject> List<T> getCollectionPageCourante(List<T> list)
  {
    try
    {
      return list.subList(
              this.getIndexPremiereColonnePageCourante(),
              this.getIndexDerniereColonnePageCourante() + 1);
    }
    catch (Exception e)
    {
      numeroPageCourante = 1;
      return list;
    }
  }

  public int getIndexPremiereDonneePageCouranteDansCollectionPaginee(int nombreLignesParColonne)
  {
    return nombreLignesParColonne * getIndexPremiereColonnePageCourante();
  }

  public int getIndexPremiereColonnePageCourante()
  {
    // Les colonnes son indexées à partir de 0
    return (numeroPageCourante - 1) * nbColonnesParPage;
  }

  public int getIndexDerniereColonnePageCourante()
  {
    //	Indice de la derniere colonne si toutes les colonnes sont remplies
    int indexDerniereColonne = getIndexPremiereColonnePageSuivante() - 1;

    // Les colonnes son indexées à partir de 0
    return Math.min(nbTotalColonnes - 1, indexDerniereColonne);
  }

  public int getIndexPremiereColonnePageSuivante()
  {
    return numeroPageCourante * nbColonnesParPage;
  }

  /**
   * Retourne une liste d'Integer permettant l'affichage de la liste des pages et donc la pagination
   * @return
   */
  public List<Integer> getPages()
  {
    List<Integer> pages = new ArrayList<Integer>();
    int maxPagesPrecedentesAffichees = (nbTotalPages - 1) / 2;
    int maxPagesSuivantesAffichees = (nbTotalPages - 1) - maxPagesPrecedentesAffichees;

    //	Nombre de page à afficher avant la page courante
    int nbrePageAvantPageCourante = getNbrePagesPrecedentes(maxPagesSuivantesAffichees, maxPagesPrecedentesAffichees, numeroPageCourante);
    //	Nombre de page à afficher après la page courante
    int nbrePageApresPageCourante = getNbrePagesSuivantes(maxPagesSuivantesAffichees, maxPagesPrecedentesAffichees, numeroPageCourante);

    //	Ajout dans la liste des pages précédentes,
    //	de la page courante
    //	et des pages suivantes
    for (int i = numeroPageCourante - nbrePageAvantPageCourante; i <= numeroPageCourante + nbrePageApresPageCourante; i++)
    {
      pages.add(i);
    }

    return pages;
  }

  /**
   * Renvoie le nombre de pages qui n'ont pas été fournie car on arrive dans les derniéres pages
   * @param maxPagesSuivantesAffichees
   * @param pageCourante
   * @return
   */
  private int getPagesLibereesApres(int maxPagesSuivantesAffichees, int numeroPage)
  {
    int nbrePagesRestantesApres = getNumeroDernierePage() - numeroPage;
    return Math.max(0, maxPagesSuivantesAffichees - nbrePagesRestantesApres);
  }

  /**
   * Renvoie le nombre de pages qui n'ont pas été fournie car on arrive dans les premières pages
   * @param maxPagesPrecedentesAffichees
   * @param pageCourante
   * @return
   */
  private int getPagesLibereesAvant(int maxPagesPrecedentesAffichees, int numeroPage)
  {
    int nbrePagesRestantesAvant = numeroPage - 1;
    return Math.max(0, maxPagesPrecedentesAffichees - nbrePagesRestantesAvant);
  }

  public int getNbrePagesPrecedentes(int maxPagesSuivantesAffichees, int maxPagesPrecedentesAffichees, int numeroPage)
  {
    return Math.min(maxPagesPrecedentesAffichees + getPagesLibereesApres(maxPagesSuivantesAffichees, numeroPage), numeroPage - 1);
  }

  public int getNbrePagesSuivantes(int maxPagesSuivantesAffichees, int maxPagesPrecedentesAffichees, int numeroPage)
  {
    return Math.min(maxPagesSuivantesAffichees + getPagesLibereesAvant(maxPagesPrecedentesAffichees, numeroPage), getNumeroDernierePage() - numeroPage);
  }

  public Integer getNumeroPage()
  {
    return numeroPageCourante;
  }

  public void setNumeroPage(Integer numeroPage)
  {
    this.numeroPageCourante = numeroPage;
  }

  public Integer getNbColonnesParPage()
  {
    return nbColonnesParPage;
  }

  public void setNbColonnesParPage(Integer nbColonnesParPage)
  {
    log.debug("Columns for 1 page : " + nbColonnesParPage);
    this.nbColonnesParPage = nbColonnesParPage;
  }

  public Integer getNbTotalPages()
  {
    return nbTotalPages;
  }

  public void setNbTotalPages(Integer nbPages)
  {
    this.nbTotalPages = nbPages;
  }

  public Integer getNumeroPagePrecedente()
  {
    return Math.max(1, numeroPageCourante - 1);
  }

  public Integer getNumeroPageSuivante()
  {
    return Math.min(getNumeroDernierePage(), numeroPageCourante + 1);
  }

  public int getNumeroDernierePage()
  {
    int quotient = nbTotalColonnes / nbColonnesParPage;
    return ((nbTotalColonnes % nbColonnesParPage) == 0) ? quotient : quotient + 1;
  }

  public void setNbTotalColonnes(Integer nombreColonnes)
  {
    this.nbTotalColonnes = nombreColonnes;
  }
}
