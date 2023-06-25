package uk.gov.moj.sdt.validators.exception;

public class InvalidRequestTypeException extends AbstractBusinessException {

    private static final long serialVersionUID = -8123237642014768431L;

    public InvalidRequestTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestTypeException(final String code, final String description) {
        super(code, description);
    }

}
