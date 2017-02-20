package mobi.chouette.exchange.netexprofile.parser;

import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.importer.Parser;
import mobi.chouette.exchange.importer.ParserFactory;
import mobi.chouette.exchange.netexprofile.Constant;
import org.rutebanken.netex.model.DayTypeAssignment;
import org.rutebanken.netex.model.DayTypeAssignmentsInFrame_RelStructure;
import org.rutebanken.netex.model.DayTypeRefStructure;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.List;

@Log4j
public class DayTypeAssignmentParser extends NetexParser implements Parser, Constant {

    static final String LOCAL_CONTEXT = "DayTypeAssignment";
    static final String DATE_OF_OPERATION = "dateOfOperation";

    @Override
    public void parse(Context context) throws Exception {
        DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentStruct =
                (DayTypeAssignmentsInFrame_RelStructure) context.get(NETEX_LINE_DATA_CONTEXT);

        if (dayTypeAssignmentStruct != null) {
            List<DayTypeAssignment> dayTypeAssignments = dayTypeAssignmentStruct.getDayTypeAssignment();

            for (DayTypeAssignment dayTypeAssignment : dayTypeAssignments) {
                JAXBElement<? extends DayTypeRefStructure> dayTypeRefElement = dayTypeAssignment.getDayTypeRef();
                LocalDate dateOfOperation = dayTypeAssignment.getDate();

                if (dayTypeRefElement != null && dateOfOperation != null) {
                    String dayTypeIdRef = dayTypeRefElement.getValue().getRef();
                    addDateOfOperation(context, dayTypeIdRef, dateOfOperation);
                }
            }
        }
    }

    private void addDateOfOperation(Context context, String objectId, LocalDate dateOfOperation) {
        Context objectContext = getObjectContext(context, LOCAL_CONTEXT, objectId);
        objectContext.put(DATE_OF_OPERATION, dateOfOperation);
    }

    static {
        ParserFactory.register(DayTypeAssignmentParser.class.getName(), new ParserFactory() {
            private DayTypeAssignmentParser instance = new DayTypeAssignmentParser();

            @Override
            protected Parser create() {
                return instance;
            }
        });
    }

}
