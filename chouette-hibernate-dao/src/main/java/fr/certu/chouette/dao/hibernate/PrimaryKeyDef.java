package fr.certu.chouette.dao.hibernate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author michel
 *
 */
public class PrimaryKeyDef
{
	@Getter @Setter private String key;
	@Getter @Setter private String table;
	@Getter @Setter private String columns;
	private String[] columnNames = null;

	public String[] getColumnNames()
	{
		if (columnNames == null)
		{
			columnNames=columns.split(",");
			for (int i = 0; i < columnNames.length; i++) 
			{
				columnNames[i] = columnNames[i].trim();
			}
		}
		return columnNames;
	}
	public Object[] toArray()
	{
		
		return new Object[]{
				table,
				key,
				columns};
	}
}

