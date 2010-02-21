package fr.certu.chouette.modele;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class BaseObjet implements Serializable
{

  private static final long serialVersionUID = 3256446889040622647L;
  private Long id;

  public Long getId()
  {
    return id;
  }

  public void setId(final Long id)
  {
    this.id = id;
  }

  public String toString()
  {
    return ToStringBuilder.reflectionToString(this,
            ToStringStyle.MULTI_LINE_STYLE);
  }
}
