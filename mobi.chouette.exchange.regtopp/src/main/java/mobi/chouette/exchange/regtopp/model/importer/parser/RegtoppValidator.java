package mobi.chouette.exchange.regtopp.model.importer.parser;

public interface RegtoppValidator {

	public abstract class Validator<T> {

		public abstract boolean validate(T input, RegtoppImporter dao);

	}
}
