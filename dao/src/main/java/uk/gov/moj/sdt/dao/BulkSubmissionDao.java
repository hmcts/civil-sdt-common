package uk.gov.moj.sdt.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.dao.api.IBulkSubmissionDao;
import uk.gov.moj.sdt.dao.repository.BulkSubmissionRepository;
import uk.gov.moj.sdt.domain.BulkSubmission;
import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Implements specific DAO functionality based on {@link IBulkSubmissionDao}. This is a derived DAO extending
 * {@link GenericDao} which provides generic jpa access. This specific DAO exists in order to construct domain
 * specific selections where column matches are needed on columns other than the id. For each domain specific query, it
 *
 * @author Robin Compston
 */
@Component("BulkSubmissionDao")
public class BulkSubmissionDao extends GenericDao<BulkSubmission> implements IBulkSubmissionDao {
    /**
     * Logger object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkSubmissionDao.class);

    private final Root<BulkSubmission> root;

    private final CriteriaBuilder criteriaBuilder;

    private final CriteriaQuery<BulkSubmission> criteriaQuery;

    @Autowired
    public BulkSubmissionDao(final BulkSubmissionRepository crudRepository,
                             EntityManager entityManager) {
        super(crudRepository, entityManager);
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(BulkSubmission.class);
        root = criteriaQuery.from(BulkSubmission.class);
    }

    @Override
    public IBulkSubmission getBulkSubmission(final IBulkCustomer bulkCustomer, final String customerReference,
                                             final int dataRetention) throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get bulk submission matching the bulk customer[{}], customer reference[{}] and the data retention period[{}]",
                    bulkCustomer, customerReference, dataRetention);
        }

        Predicate sdtCustomerPredicate = criteriaBuilder.equal(
            root.get("bulkCustomer").get("sdtCustomerId"),
            bulkCustomer.getSdtCustomerId()
        );
        Predicate customerReferencePredicate = criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("customerReference")),
            customerReference.toLowerCase()
        );
        Predicate sdtDatePredicate = createDatePredicate(criteriaBuilder, root, dataRetention);

        TypedQuery<BulkSubmission> typedQuery = getEntityManager().createQuery(criteriaQuery.select(root)
                .where(sdtCustomerPredicate, customerReferencePredicate, sdtDatePredicate));

try {
            return typedQuery.getResultStream().findFirst().orElse(null);
        } catch(NoResultException e) {
            return null;
        }
    }

    @Override
    public IBulkSubmission getBulkSubmissionBySdtRef(final IBulkCustomer bulkCustomer, final String sdtBulkReference,
                                                     final int dataRetention) throws DataAccessException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Get bulk submission matching the bulk customer[{}], SDT bulk reference[{}] and the data retention period [{}]",
                    bulkCustomer, sdtBulkReference, dataRetention);
        }

        Predicate sdtCustomerPredicate = criteriaBuilder.equal(
            root.get("bulkCustomer").get("sdtCustomerId"),
            bulkCustomer.getSdtCustomerId()
        );
        Predicate sdtBulkRefPredicate = criteriaBuilder.equal(
            criteriaBuilder.lower(root.get("sdtBulkReference")),
            sdtBulkReference.toLowerCase()
        );
        Predicate sdtDatePredicate = createDatePredicate(criteriaBuilder, root, dataRetention);
        TypedQuery<BulkSubmission> typedQuery = getEntityManager().createQuery(criteriaQuery.select(root)
                .where(sdtCustomerPredicate, sdtBulkRefPredicate, sdtDatePredicate));

        try {
            return typedQuery.getResultStream().findFirst().orElse(null);
        } catch(NoResultException e) {
            return null;
        }
    }
}
