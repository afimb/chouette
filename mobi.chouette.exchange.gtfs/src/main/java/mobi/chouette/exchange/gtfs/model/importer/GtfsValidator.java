package mobi.chouette.exchange.gtfs.model.importer;

public interface GtfsValidator {

	public abstract class Validator<T> {

		public abstract boolean validate(T input, GtfsImporter dao);

	}
}
