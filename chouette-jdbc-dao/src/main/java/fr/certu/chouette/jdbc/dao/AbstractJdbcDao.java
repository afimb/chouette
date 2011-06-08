package fr.certu.chouette.jdbc.dao;

import lombok.Getter;
import lombok.Setter;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import fr.certu.chouette.dao.IDaoTemplate;
import fr.certu.chouette.model.neptune.NeptuneIdentifiedObject;

public abstract class AbstractJdbcDao<T extends NeptuneIdentifiedObject>
		extends JdbcDaoSupport implements IDaoTemplate<T> 
{
	
	@Getter @Setter protected String sqlSelectAll;
	@Getter @Setter protected String sqlSelectByObjectId;
	@Getter @Setter protected String sqlInsert;
	@Getter @Setter protected String sqlUpdate;
	@Getter @Setter protected String sqlDelete;
	@Getter @Setter protected String sqlDeleteAll;
}
