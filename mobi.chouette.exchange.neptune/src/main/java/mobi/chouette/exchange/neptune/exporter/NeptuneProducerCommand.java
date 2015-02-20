package mobi.chouette.exchange.neptune.exporter;

import java.sql.Date;

import javax.ejb.Stateless;

import lombok.ToString;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Constant;
import mobi.chouette.common.Context;
import mobi.chouette.common.chain.Command;
import mobi.chouette.model.Line;

@Stateless(name = NeptuneProducerCommand.COMMAND)
@ToString
@Log4j
public class NeptuneProducerCommand implements Command, Constant {

	public static final String COMMAND = "NeptuneProducerCommand";

	@Override
	public boolean execute(Context context) throws Exception {

		// Context should have a LINE entry with the line to process
		Line line = (Line) context.get(LINE);
		ExportableData collection = new ExportableData();
		Date startDate = null;
		Date endDate = null;

		NeptuneDataCollector collector = new NeptuneDataCollector();
		collector.collect(collection, line, startDate, endDate);

		context.put(EXPORTABLE_DATA, collection);

		ChouettePTNetworkProducer producer = new ChouettePTNetworkProducer();

		producer.produce(context);
		//

		return false;
	}

}
