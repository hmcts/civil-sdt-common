package uk.gov.moj.sdt.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.dao.api.ITargetApplicationDao;
import uk.gov.moj.sdt.dao.config.DaoTestConfig;
import uk.gov.moj.sdt.domain.TargetApplication;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.test.utils.AbstractIntegrationTest;
import uk.gov.moj.sdt.test.utils.TestConfig;
import uk.gov.moj.sdt.utils.SpringApplicationContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test {@link TargetApplicationDao} query methods.
 *
 * @author Mark Dathorne
 */
@ActiveProfiles("integ")
@SpringBootTest(classes = { TestConfig.class, DaoTestConfig.class})
@ExtendWith(SpringExtension.class)
@Sql(scripts = {"classpath:uk/gov/moj/sdt/dao/sql/TargetApplicationDaoTest.sql"})
@Transactional
class TargetApplicationDaoTest extends AbstractIntegrationTest {

    CriteriaBuilder criteriaBuilder;
    CriteriaQuery<TargetApplication> criteriaQuery;
    Root<TargetApplication> root;

    private static final String EXISTING_TARGET_APPLICATION_CODE = "MCOL";
    private static final String NON_EXISTENT_TARGET_APPLICATION_CODE = "UNKNOWN";

    @BeforeEach
    public void setup() {
        final ITargetApplicationDao targetApplicationDao = SpringApplicationContext.getBean(ITargetApplicationDao.class);
        criteriaBuilder = targetApplicationDao.getEntityManager().getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(TargetApplication.class);
        root = criteriaQuery.from(TargetApplication.class);
    }

    @Test
    void successfullyGetTargetApplicationForExistingCode() {
        final ITargetApplicationDao targetApplicationDao = SpringApplicationContext.getBean(ITargetApplicationDao.class);

        final ITargetApplication targetApplication = targetApplicationDao.getTargetApplicationByCode(EXISTING_TARGET_APPLICATION_CODE);
        assertNotNull(targetApplication,
                "Could not find target application with code [" + EXISTING_TARGET_APPLICATION_CODE + "]");
        assertEquals(EXISTING_TARGET_APPLICATION_CODE, targetApplication.getTargetApplicationCode(),
                "Incorrect target application - code is [" + targetApplication.getTargetApplicationCode()
                        + "] when should be [" + EXISTING_TARGET_APPLICATION_CODE + "]");
    }

    @Test
    void failToGetTargetApplicationForNonExistentCode() {
        final ITargetApplicationDao targetApplicationDao = SpringApplicationContext.getBean(ITargetApplicationDao.class);

        final ITargetApplication targetApplication = targetApplicationDao.getTargetApplicationByCode(NON_EXISTENT_TARGET_APPLICATION_CODE);
        assertNull(targetApplication,
                "Found target application with code [" + NON_EXISTENT_TARGET_APPLICATION_CODE + "]");
    }
}
