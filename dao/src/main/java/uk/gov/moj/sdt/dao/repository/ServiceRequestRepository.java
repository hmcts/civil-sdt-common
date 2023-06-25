package uk.gov.moj.sdt.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.moj.sdt.domain.ServiceRequest;

@Repository
public interface ServiceRequestRepository extends CrudRepository<ServiceRequest, Long> {
}
