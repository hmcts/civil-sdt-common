package uk.gov.moj.sdt.transformers.exception;

public class JaxbXmlConversionException extends RuntimeException {

    public JaxbXmlConversionException(Throwable cause) {
        super(cause);
    }

    public JaxbXmlConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
