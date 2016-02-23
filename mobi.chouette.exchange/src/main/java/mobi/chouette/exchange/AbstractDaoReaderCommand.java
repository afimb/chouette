package mobi.chouette.exchange;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import mobi.chouette.common.Constant;
import mobi.chouette.dao.GenericDAO;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

public class AbstractDaoReaderCommand implements Constant {

	@Resource
	protected SessionContext daoContext;

	@EJB (mappedName="java:app/mobi.chouette.dao/LineDAO")
	protected GenericDAO<Line> lineDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/NetworkDAO")
	protected GenericDAO<Network> ptNetworkDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/CompanyDAO")
	protected GenericDAO<Company> companyDAO;

	@EJB (mappedName="java:app/mobi.chouette.dao/GroupOfLineDAO")
	protected GenericDAO<GroupOfLine> groupOfLineDAO;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	protected Set<Long> loadLines(String type, List<Long> ids) {
		Set<Line> lines = new HashSet<Line>();
		Set<Long> lineIds = new HashSet<Long>();
		if (ids == null || ids.isEmpty()) {
			lines.addAll(lineDAO.findAll());
		} else {
			if (type.equals("line")) {
				lines.addAll(lineDAO.findAll(ids));
			} else if (type.equals("network")) {
				List<Network> list = ptNetworkDAO.findAll(ids);
				for (Network ptNetwork : list) {
					lines.addAll(ptNetwork.getLines());
				}
			} else if (type.equals("company")) {
				List<Company> list = companyDAO.findAll(ids);
				for (Company company : list) {
					lines.addAll(company.getLines());
				}
			} else if (type.equals("group_of_line")) {
				List<GroupOfLine> list = groupOfLineDAO.findAll(ids);
				for (GroupOfLine groupOfLine : list) {
					lines.addAll(groupOfLine.getLines());
				}
			}
		}
		for (Line line : lines) {
			lineIds.add(line.getId());
		}
		// daoContext.setRollbackOnly();
		return lineIds;
	}

}
