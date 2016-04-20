package mobi.chouette.exchange.regtopp.validation;

import mobi.chouette.exchange.regtopp.importer.RegtoppImporter;

public interface RegtoppValidator {

	public abstract class Validator<T> {

		public abstract boolean validate(T input, RegtoppImporter dao);

	}
}
