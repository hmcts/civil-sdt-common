package uk.gov.moj.sdt.utils.cmc.exception;

public class CMCException extends RuntimeException {

    private static final long serialVersionUID = 1921937543087682070L;

    public CMCException(String message) {
        super(message);
    }

    public CMCException(String message, Throwable cause) {
        super(message, cause);
    }
}
