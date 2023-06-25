package uk.gov.moj.sdt.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.repository.PurgeJobAuditMessageRepository;
import uk.gov.moj.sdt.domain.PurgeJobAuditMessage;

import javax.persistence.EntityManager;

@Component("PurgeJobAuditMessageDao")
public class PurgeJobAuditMessageDao extends GenericDao<PurgeJobAuditMessage> {

    @Autowired
    public PurgeJobAuditMessageDao(final PurgeJobAuditMessageRepository crudRepository,
                                   EntityManager entityManager) {
        super(crudRepository, entityManager);
    }
}
