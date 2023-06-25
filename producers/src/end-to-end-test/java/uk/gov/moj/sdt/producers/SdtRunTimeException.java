package uk.gov.moj.sdt.producers;

public class SdtRunTimeException extends RuntimeException {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Error code.
     */
    private String errorCode;

    /**
     * Error description.
     */
    private String errorDescription;

    /**
     * Constructor for non tokenised description.
     *
     * @param code        code for the error message
     * @param description for the error message
     */
    public SdtRunTimeException(final String code, final String description) {
        super("The following exception occurred [" + code + "] message[" + description + "]");
        this.errorCode = code;
        this.errorDescription = description;
    }

    /**
     * Get the error code.
     *
     * @return error code
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get the error description.
     *
     * @return error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

}
