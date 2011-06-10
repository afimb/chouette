package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.model.neptune.Company;

/**
 * 
 * @author mamadou keira
 * 
 */

public class CompanyJdbcDao extends AbstractJdbcDao<Company> 
{
	@Override
	public Company getByObjectId(String objectId) 
	{
		String sql = sqlSelectByObjectId;		 
		Company company = (Company)getJdbcTemplate().queryForObject(sql, 
				new Object[] {objectId}, 
				new BeanPropertyRowMapper(Company.class));
		return company;
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
