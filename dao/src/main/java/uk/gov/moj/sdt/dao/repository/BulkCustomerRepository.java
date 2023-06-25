package uk.gov.moj.sdt.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.gov.moj.sdt.domain.BulkCustomer;

@Repository
public interface BulkCustomerRepository extends CrudRepository<BulkCustomer, Long> {
}
