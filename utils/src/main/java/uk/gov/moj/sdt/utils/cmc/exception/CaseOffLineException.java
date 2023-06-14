package uk.gov.moj.sdt.utils.cmc.exception;

public class CaseOffLineException extends RuntimeException {

    private static final long serialVersionUID = 6574656231147221423L;

    public CaseOffLineException(String message) {
        super(message);
    }

    public CaseOffLineException(String message, Throwable cause) {
        super(message, cause);
    }
}
