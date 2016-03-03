package mobi.chouette.exchange.regtopp.model.importer;

import java.util.ArrayList;
import java.util.List;

import org.beanio.BeanReader;
import org.beanio.InvalidRecordException;
import org.beanio.RecordContext;
import org.beanio.StreamFactory;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;

import lombok.Getter;
import lombok.extern.log4j.Log4j;
import mobi.chouette.common.Context;
import mobi.chouette.exchange.regtopp.model.RegtoppObject;
import mobi.chouette.exchange.regtopp.model.importer.RegtoppException.ERROR;
import mobi.chouette.exchange.regtopp.validation.ValidationReporter;
import mobi.chouette.exchange.report.FileInfo.FILE_STATE;

@Log4j
public class FileContentParser {
	@Getter
	private List<Object> rawContent = new ArrayList<>();
	
	public void parse(Context context, ParseableFile parseableFile, ValidationReporter validationReporter) throws Exception {
		StreamFactory factory = StreamFactory.newInstance();
	    
	    StreamBuilder builder = new StreamBuilder("regtopp")
	        .format("fixedlength")
	        .parser(new FixedLengthParserBuilder());
	    
	    for(Class<?> clazz : parseableFile.getRegtoppClasses()) {
	        builder = builder.addRecord(clazz);
	    	
	    }

	    factory.define(builder);
	    
	    
	    // TODO consider using error reporter instead if this continues parsing of the file
	    BeanReader in = factory.createReader("regtopp", parseableFile.getFile());
	    Object record = null;
        
        try {
            while ((record =  (RegtoppObject) in.read()) != null) {
                rawContent.add(record);
            }
            log.info("Parsed file OK: "+parseableFile.getFile().getName());
            parseableFile.getFileInfo().setStatus(FILE_STATE.OK);
        }
        catch (InvalidRecordException ex) {
            RecordContext rContext = ex.getRecordContext();
            String fileName = parseableFile.getFile().getName();
			if (rContext.hasRecordErrors()) {
            	for (String error : rContext.getRecordErrors()) {
            		 mobi.chouette.exchange.regtopp.model.importer.Context ctx = new mobi.chouette.exchange.regtopp.model.importer.Context(fileName,-1,-1,null,ERROR.BAD_VALUE,null,null);
            		 
            		
            		RegtoppException e = new RegtoppException(ctx, ex);
					validationReporter.reportError(context, e, fileName);
                	System.err.println("Remember to record error: "+error+ " for class "+parseableFile.getClass().getName());
                }
            }
            if (rContext.hasFieldErrors()) {
                for (String field : rContext.getFieldErrors().keySet()) {
                    for (String error : rContext.getFieldErrors(field)) {

               		 mobi.chouette.exchange.regtopp.model.importer.Context ctx = 
               				 new mobi.chouette.exchange.regtopp.model.importer.Context(fileName,rContext.getLineNumber(),rContext.getFieldCount(field),field,ERROR.BAD_VALUE,field,rContext.getFieldText(field));

             		RegtoppException e = new RegtoppException(ctx, ex);
					validationReporter.reportError(context, e, fileName);
                	System.err.println("Remember to record field error: "+error+ " for class "+parseableFile.getClass().getName());
                    }
                }
            }
        }   finally {
            in.close();
        }      

		
	}

	public void dispose() {
		rawContent.clear();
	}
}
