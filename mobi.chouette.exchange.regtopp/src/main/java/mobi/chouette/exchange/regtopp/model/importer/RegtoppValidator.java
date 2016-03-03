package mobi.chouette.exchange.regtopp.model.importer;

public interface RegtoppValidator {

	public abstract class Validator<T> {

		public abstract boolean validate(T input, RegtoppImporter dao);

	}
}
