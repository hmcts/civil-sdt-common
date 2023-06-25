package uk.gov.moj.sdt.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IBulkCustomerDao;
import uk.gov.moj.sdt.dao.repository.BulkCustomerRepository;
import uk.gov.moj.sdt.domain.BulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Implements specific DAO functionality based on {@link IBulkCustomerDao}. This is a derived DAO extending
 * {@link GenericDao} which provides generic JPA access. This specific DAO exists in order to construct domain
 * specific selections where column matches are needed on columns other than the id. For each domain specific query, it
 * constructs an array of JPA Predicate which are passed to the generic method
 *
 * @author Robin Compston
 */
@Component("BulkCustomerDao")
public class BulkCustomerDao extends GenericDao<BulkCustomer> implements IBulkCustomerDao {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkCustomerDao.class);

    private final Root<BulkCustomer> root;

    private final CriteriaBuilder criteriaBuilder;

    private final CriteriaQuery<BulkCustomer> criteriaQuery;

    @Autowired
    public BulkCustomerDao(final BulkCustomerRepository crudRepository,
                           EntityManager entityManager) {
        super(crudRepository, entityManager);
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(BulkCustomer.class);
        root = criteriaQuery.from(BulkCustomer.class);
    }

    @Override
    public IBulkCustomer getBulkCustomerBySdtId(final long sdtCustomerId) throws DataAccessException {
        LOGGER.debug("Get bulk customer matching sdt customer id [{}]", sdtCustomerId);

        // Call the generic dao to perform this query.
        try {
            return this.uniqueResult(BulkCustomer.class, () -> {
                Predicate sdtCustomerPredicate = criteriaBuilder.equal(root.get("sdtCustomerId"), sdtCustomerId);
                return criteriaQuery.select(root).where(sdtCustomerPredicate);
            });
        } catch (NoResultException e) {
            return null;
        }
    }
}
