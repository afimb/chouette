/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * classe permettant de définir une règle d'ordre des résultats d'une recherche
 */
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class FilterOrder
{
  public static enum Type {ASC,DESC}

  @Getter private final Type type;
  @Getter private final String attribute;

  public static FilterOrder asc(String attribute)
  {
    return new FilterOrder(Type.ASC,attribute);
  }
  public static FilterOrder desc(String attribute)
  {
    return new FilterOrder(Type.DESC,attribute);
  }

}
