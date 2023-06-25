package uk.gov.moj.sdt.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.moj.sdt.domain.PurgeJobAuditMessage;

@Repository
public interface PurgeJobAuditMessageRepository extends CrudRepository<PurgeJobAuditMessage, Long> {
}
