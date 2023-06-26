package uk.gov.moj.sdt.interceptors.service;

import org.springframework.dao.DataAccessException;
import uk.gov.moj.sdt.domain.api.IDomainObject;

public interface IPersistServiceRequest {

    void persist(final Object domainObject) throws DataAccessException;

    <D extends IDomainObject> D fetch(final Class<D> domainType, final long id)
            throws DataAccessException;

}
