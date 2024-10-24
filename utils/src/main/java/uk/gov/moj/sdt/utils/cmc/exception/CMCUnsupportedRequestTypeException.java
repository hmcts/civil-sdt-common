package uk.gov.moj.sdt.utils.cmc.exception;

import java.io.Serial;

public class CMCUnsupportedRequestTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4190292439477027658L;

    public CMCUnsupportedRequestTypeException(String message) {
        super(message);
    }
}
