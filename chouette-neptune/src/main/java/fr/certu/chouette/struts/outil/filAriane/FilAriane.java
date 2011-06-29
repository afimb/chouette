package fr.certu.chouette.struts.outil.filAriane;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FilAriane
{

  private static final int nbreElementFilAriane = 4;
  private Vector<ElementFilAriane> filAriane;

  public FilAriane()
  {
    filAriane = new Vector<ElementFilAriane>(nbreElementFilAriane);
  }

  public Vector<ElementFilAriane> getFilAriane()
  {
    return filAriane;
  }

  public String getTexteFilAriane()
  {
    StringBuffer texteFilAriane = new StringBuffer("");
    for (ElementFilAriane elementFilAriane : filAriane)
    {
      if (filAriane.firstElement().equals(elementFilAriane))
      {
        texteFilAriane.append("<a href='");
        texteFilAriane.append(elementFilAriane.getUrl());
        texteFilAriane.append("'>");
        texteFilAriane.append(elementFilAriane.getCleTexte());
        if (elementFilAriane.getParametreTexte() != null && !elementFilAriane.getParametreTexte().equals(""))
        {
          texteFilAriane.append(" ");
          texteFilAriane.append(elementFilAriane.getParametreTexte());
        }
        texteFilAriane.append("</a>");
      }
      // Si dernier élément pas de lien url
      else if(filAriane.lastElement().equals(elementFilAriane))
      {
        texteFilAriane.append(" > ");
        texteFilAriane.append(elementFilAriane.getCleTexte());
        if (elementFilAriane.getParametreTexte() != null && !elementFilAriane.getParametreTexte().equals(""))
        {
          texteFilAriane.append(" ");
          texteFilAriane.append(elementFilAriane.getParametreTexte());
        }
      }
      else
      {
        texteFilAriane.append(" > ");
        texteFilAriane.append("<a href='");
        texteFilAriane.append(elementFilAriane.getUrl());
        texteFilAriane.append("'>");
        texteFilAriane.append(elementFilAriane.getCleTexte());
        if (elementFilAriane.getParametreTexte() != null && !elementFilAriane.getParametreTexte().equals(""))
        {
          texteFilAriane.append(" ");
          texteFilAriane.append(elementFilAriane.getParametreTexte());
        }
        texteFilAriane.append("</a>");
      }
    }
    return texteFilAriane.toString();
  }

  public void addElementFilAriane(String cleTexte, String parametreTexte, String url)
  {
    ElementFilAriane elementFilAriane = new ElementFilAriane(cleTexte, parametreTexte, url);
    ElementFilAriane elementSupprime = null;
    for (ElementFilAriane element : filAriane)
    {
      if (element.getCleTexte().equals(elementFilAriane.getCleTexte()) && element.getParametreTexte().equals(elementFilAriane.getParametreTexte()))
      {
        elementSupprime = element;
        break;
      }
    }
    if (elementSupprime != null)
    {
      if (filAriane.indexOf(elementSupprime) < filAriane.size() - 1)
      {
        List<ElementFilAriane> elementsSupprimes = new ArrayList<ElementFilAriane>();
        for (int indexFilAriane = filAriane.indexOf(elementSupprime) + 1; indexFilAriane < filAriane.size(); indexFilAriane++)
        {
          elementsSupprimes.add(filAriane.get(indexFilAriane));
        }
        filAriane.removeAll(elementsSupprimes);
      }
    }
    else
    {
      if (filAriane.size() >= nbreElementFilAriane)
      {
        filAriane.remove(0);
      }
      filAriane.add(elementFilAriane);
    }
  }

  public String getCleTexteDernierElementFilAriane()
  {
    if (!filAriane.isEmpty())
    {
      return filAriane.lastElement().getCleTexte();
    }
    else
    {
      return null;
    }
  }
}
