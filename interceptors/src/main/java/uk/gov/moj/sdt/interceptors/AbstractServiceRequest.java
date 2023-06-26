package uk.gov.moj.sdt.interceptors;

import uk.gov.moj.sdt.interceptors.service.RequestDaoService;

/**
 * Shared abstract class for audit logging.
 *
 * @author d195274
 */
public abstract class AbstractServiceRequest extends AbstractSdtInterceptor {

    /**
     * The persistence class for this interceptor.
     */
    private RequestDaoService requestDaoService;

    /**
     * Default constructor.
     *
     * @param phase the CXF phase to apply this to.
     */
    protected AbstractServiceRequest(final String phase) {
        super(phase);
    }

    public RequestDaoService getRequestDaoService() {
        return requestDaoService;
    }

    public void setRequestDaoService(RequestDaoService requestDaoService) {
        this.requestDaoService = requestDaoService;
    }

}
