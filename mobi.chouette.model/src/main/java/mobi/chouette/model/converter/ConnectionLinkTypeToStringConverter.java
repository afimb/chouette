package mobi.chouette.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import mobi.chouette.model.type.ConnectionLinkTypeEnum;

@Converter
public class ConnectionLinkTypeToStringConverter implements AttributeConverter<ConnectionLinkTypeEnum, String>{

	@Override
	public String convertToDatabaseColumn(ConnectionLinkTypeEnum attribute) {
		if (attribute == null) return null;
		return attribute.name();
	}

	@Override
	public ConnectionLinkTypeEnum convertToEntityAttribute(String dbData) {
		if (dbData == null) return null;
		if (dbData.isEmpty()) return null;
		return ConnectionLinkTypeEnum.valueOf(dbData);
	}

}
