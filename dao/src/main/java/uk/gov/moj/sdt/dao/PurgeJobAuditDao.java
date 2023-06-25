package uk.gov.moj.sdt.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.repository.PurgeJobAuditRepository;
import uk.gov.moj.sdt.domain.PurgeJobAudit;

import javax.persistence.EntityManager;

@Component("PurgeJobAuditDao")
public class PurgeJobAuditDao extends GenericDao<PurgeJobAudit> {

    @Autowired
    public PurgeJobAuditDao(final PurgeJobAuditRepository crudRepository,
                            EntityManager entityManager) {
        super(crudRepository, entityManager);
    }
}
