package mobi.chouette.exchange.regtopp.model.v12;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppErrorsHashSet;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppException;
import mobi.chouette.exchange.regtopp.model.importer.parser.RegtoppExceptionsHashSet;

@ToString
@NoArgsConstructor
public abstract class RegtoppObject {

	@Getter
	@Setter
	private int recordLineNumber;

	public abstract String getIndexingKey();

	protected String pad(String val, int size) {
		return StringUtils.leftPad(val, size, '0');
	}

	protected String pad(int val, int size) {
		return pad(String.valueOf(val), size);
	}

	@Getter
	protected Set<RegtoppException> errors = new RegtoppExceptionsHashSet<>();

	@Getter
	protected Set<RegtoppException.ERROR> okTests = new RegtoppErrorsHashSet<>();

}
