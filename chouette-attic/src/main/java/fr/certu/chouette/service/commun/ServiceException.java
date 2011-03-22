package fr.certu.chouette.service.commun;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public  class ServiceException extends RuntimeException {

    private CodeIncident       code;
    private CodeDetailIncident detail;
    private Object[]           args;
    private ResourceBundle     bundle;

    public ServiceException(CodeIncident code, CodeDetailIncident detail, Object...args ) {
        this(ResourceBundle.getBundle("serviceException"), code, detail, args);
    }
    
    public ServiceException(ResourceBundle bundle, CodeIncident code, CodeDetailIncident detail, Object...args ) {
        super("");
        this.bundle = bundle;
        this.code = code;
        this.detail = detail;
        this.args = args;
    }
    
    public ServiceException(CodeIncident code, CodeDetailIncident detail, Throwable exception,Object...args) {
        this(ResourceBundle.getBundle("serviceException"), code, detail, exception, args);
    }

    public ServiceException(ResourceBundle bundle, CodeIncident code, CodeDetailIncident detail, Throwable exception,Object...args) {
        super("", exception);
        this.bundle = bundle;
        this.code = code;
        this.detail = detail;
        this.args = args;
    }

    public ServiceException(CodeIncident code, Throwable exception, Object...args) {
        this(ResourceBundle.getBundle("serviceException"), code, CodeDetailIncident.DEFAULT, exception, args);
    }

    public ServiceException(ResourceBundle bundle, CodeIncident code, Throwable exception, Object...args) {
        this(bundle, code, CodeDetailIncident.DEFAULT, exception, args);
    }

    public ServiceException(CodeIncident code, Throwable exception) {
        this(ResourceBundle.getBundle("serviceException"), code,CodeDetailIncident.DEFAULT, exception, new Object[0]);
    }

    public ServiceException(ResourceBundle bundle, CodeIncident code, Throwable exception) {
        this(bundle, code, CodeDetailIncident.DEFAULT, exception, new Object[0]);
    }

    public ServiceException(CodeIncident code) {
        this(ResourceBundle.getBundle("serviceException"), code, CodeDetailIncident.DEFAULT, new Object[0]);
    }

    public ServiceException(ResourceBundle bundle, CodeIncident code) {
        this(bundle, code, CodeDetailIncident.DEFAULT, new Object[0]);
    }

    public CodeIncident getCode() {
        return code;
    }
    
    public CodeDetailIncident getDetailCode() {
        return detail;
    }

    @Override
    public String getMessage() {
        try {
            String format = "";
            try {
                format = bundle.getString("msg."+code.name()+"."+detail.name());
            }
            catch (MissingResourceException e) {
                format = bundle.getString("msg.noMsg");
                args = new Object[]{code.name(), detail.name()};
            }
            String message = "";
            try {
                message = MessageFormat.format(format, args);
            }
            catch(IllegalArgumentException e) {
                format = bundle.getString("msg.badArgs");
                args = new Object[]{code.name(), detail.name()};
                message = MessageFormat.format(format, args);
            }
            if (this.getCause() != null) {
                format = bundle.getString("msg.cause");
                message += "\n"+MessageFormat.format(format, this.getCause().getLocalizedMessage());
            }
            return message;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}
