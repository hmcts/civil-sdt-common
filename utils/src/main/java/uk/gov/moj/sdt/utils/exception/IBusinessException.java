package uk.gov.moj.sdt.utils.exception;

/**
 * Interface for business exceptions.
 *
 * @author d130680
 */
public interface IBusinessException {

    /**
     * Get the error code.
     *
     * @return error code
     */
    String getErrorCode();

    /**
     * Get the error description.
     *
     * @return error description
     */
    String getErrorDescription();
}