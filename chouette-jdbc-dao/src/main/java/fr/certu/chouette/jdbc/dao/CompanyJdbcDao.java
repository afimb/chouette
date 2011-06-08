package fr.certu.chouette.jdbc.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import fr.certu.chouette.filter.Filter;
import fr.certu.chouette.model.neptune.Company;

/**
 * 
 * @author mamadou keira
 * 
 */

public class CompanyJdbcDao extends AbstractJdbcDao<Company> 
{

	@Override
	public void saveOrUpdateAll(final List<Company> companies)
	{
		String sql = sqlInsert;
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() 
		{			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException 
			{
				Company company = companies.get(i);
				if(company != null)
				{
					ps.setLong(1, company.getId());
					ps.setString(2, company.getObjectId());
					ps.setInt(3, company.getObjectVersion());
					ps.setDate(4, (Date) company.getCreationTime());
					ps.setString(5, company.getCreatorId());
					ps.setString(6, company.getName());
					ps.setString(7, company.getShortName());
					ps.setString(8, company.getOrganisationalUnit());
					ps.setString(9, company.getOperatingDepartmentName());
					ps.setString(10, company.getCode());
					ps.setString(11, company.getPhone());
					ps.setString(12, company.getFax());
					ps.setString(13, company.getEmail());
					ps.setString(14, company.getRegistrationNumber());

				}
			}

			@Override
			public int getBatchSize() 
			{
				return companies.size();
			}
		});
	}

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
	public List<Company> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Company get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Company object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAll(Collection<Company> objects) {
		// TODO Auto-generated method stub

	}

	@Override
	public int removeAll(Filter clause) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void update(Company object) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Company> select(Filter clause) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(String objectId) {
		// TODO Auto-generated method stub
		return false;
	}

}
