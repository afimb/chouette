package fr.certu.chouette.model.neptune.type;


/**
 * Service status values
 *
 */
@Deprecated
public enum ServiceStatusValueEnum
{
   Normal, Delayed, Cancelled, Disrupted, ReducedService, IncreasedService, Rerouted, NotStopping, Early;
}
