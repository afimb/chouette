package fr.certu.chouette.jdbc.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		final List<Company> insertables = new ArrayList<Company>();
		final List<Company> updatables = new ArrayList<Company>();
		
		dispatchObjects(companies, insertables, updatables);

		if(!insertables.isEmpty())
			toBatchUpdate(sqlInsert, insertables);
		
		if(!updatables.isEmpty())
			toBatchUpdate(sqlUpdate, updatables);
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
	public boolean exists(String objectId) 
	{
		return (getByObjectId(objectId) != null);
	}

	@Override
	protected void setPreparedStatement(PreparedStatement ps, Company company) throws SQLException 
	{
		ps.setString(1, company.getObjectId());
		ps.setInt(2, company.getObjectVersion());

		Timestamp timestamp = new Timestamp(company.getCreationTime().getTime());
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

}
