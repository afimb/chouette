package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import fr.certu.chouette.model.neptune.Company;

/**
 * 
 * @author mamadou keira
 * 
 */

public class CompanyJdbcDao extends AbstractJdbcDao<Company> 
{
   private static final Logger logger = Logger.getLogger(CompanyJdbcDao.class);
   
   public Logger getLogger()
   {
      return logger;
   }

	@Override
	protected void populateStatement(PreparedStatement ps, Company company) throws SQLException 
	{
		ps.setString(1, company.getObjectId());
		ps.setInt(2, company.getObjectVersion());

		Timestamp timestamp = null;
		if(company.getCreationTime() != null)
			timestamp = new Timestamp(company.getCreationTime().getTime());
		ps.setTimestamp(3, timestamp);
		ps.setString(4, company.getCreatorId());
		ps.setString(5, company.getName());
		ps.setString(6, company.getShortName());
		ps.setString(7, company.getOrganisationalUnit());
		ps.setString(8, company.getOperatingDepartmentName());
		ps.setString(9, company.getCode());
		ps.setString(10, company.getPhone());
		ps.setString(11, company.getFax());
		ps.setString(12, company.getEmail());
		ps.setString(13, company.getRegistrationNumber());	
	}
}
