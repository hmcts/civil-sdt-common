package uk.gov.moj.sdt.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.repository.ErrorLogRepository;
import uk.gov.moj.sdt.domain.ErrorLog;

import javax.persistence.EntityManager;

@Component("ErrorLogDao")
public class ErrorLogDao extends GenericDao<ErrorLog> {

    @Autowired
    public ErrorLogDao(final ErrorLogRepository crudRepository,
                       EntityManager entityManager) {
        super(crudRepository, entityManager);
    }
}
