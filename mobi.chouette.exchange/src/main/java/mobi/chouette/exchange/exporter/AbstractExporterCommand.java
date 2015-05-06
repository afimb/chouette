package mobi.chouette.exchange.exporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import mobi.chouette.common.Constant;
import mobi.chouette.dao.CompanyDAO;
import mobi.chouette.dao.GroupOfLineDAO;
import mobi.chouette.dao.LineDAO;
import mobi.chouette.dao.NetworkDAO;
import mobi.chouette.model.Company;
import mobi.chouette.model.GroupOfLine;
import mobi.chouette.model.Line;
import mobi.chouette.model.Network;

public class AbstractExporterCommand implements Constant {

	@EJB
	protected LineDAO lineDAO;

	@EJB
	protected NetworkDAO ptNetworkDAO;

	@EJB
	protected CompanyDAO companyDAO;

	@EJB
	protected GroupOfLineDAO groupOfLineDAO;

	protected Set<Line> loadLines(String type, List<Long> ids) {
		Set<Line> lines = new HashSet<Line>();
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
		return lines;
	}

}
