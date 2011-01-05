/**
 * Projet CHOUETTE
 *
 * ce projet est sous license libre
 * voir LICENSE.txt pour plus de details
 *
 */
package fr.certu.chouette.filter;

/**
 * 
 */
public enum DetailLevelEnum
{
      /**
       * object DetailLevel not set
       */
	  UNINITIALIZED,
      /**
       *  object with only his attributes
       */
      ATTRIBUTE,
      /**
       * object with direct dependencies, <br/>
       * each objet comming in dependency is in ATTRIBUTE level<br/>
       * for Example : lines come with network wich come without any dependency
       */
      NARROW_DEPENDENCIES,
      /**
       * object with direct relational dependencies as for NARROW_DEPENDENCIES<br/>
       * for structural dependency, each objet commes with its structural children<br/>
       * for Example : lines come with routes wich come with stoppoints but not with JourneyPatterns
       */
      STRUCTURAL_DEPENDENCIES,
      /**
       * object with direct relational dependencies as for NARROW_DEPENDENCIES<br/>
       * for any other dependency , the entire hierarchy is returned
       */
      ALL_DEPENDENCIES

}
