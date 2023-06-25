package uk.gov.moj.sdt.services.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.ErrorMessageDao;
import uk.gov.moj.sdt.dao.api.IGenericDao;
import uk.gov.moj.sdt.domain.ErrorMessage;
import uk.gov.moj.sdt.domain.api.IErrorMessage;
import uk.gov.moj.sdt.services.cache.api.IErrorMessagesCache;
import uk.gov.moj.sdt.services.mbeans.SdtManagementMBean;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.utils.mbeans.api.ISdtManagementMBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for error messages cache.
 *
 * @author d130680
 */
@ExtendWith(MockitoExtension.class)
class ErrorMessagesCacheTest extends AbstractSdtUnitTestBase {

    /**
     * Generic dao.
     */
    @Mock
    private IGenericDao mockGenericDao;

    /**
     * Error messages cache.
     */
    private ErrorMessagesCache cache;

    /**
     * Dao query results.
     */
    private ErrorMessage[] result;

    private static final String ERROR_DESCRIPTION_1 = "errorDescription 1";
    private static final String ERROR_DESCRIPTION_2 = "errorDescription 2";
    private static final String ERROR_DESCRIPTION_3 = "errorDescription 3";

    private ISdtManagementMBean managementMBean;

    /**
     * Setup mock objects and inject DAO dependencies into our class.
     */
    @BeforeEach
    @Override
    public void setUp() {
        managementMBean = Mockito.mock(ISdtManagementMBean.class);
        cache = new ErrorMessagesCache(managementMBean, mockGenericDao);


        // Setup some results
        result = new ErrorMessage[3];
        result[0] = new ErrorMessage();
        result[0].setErrorCode("1");
        result[0].setErrorDescription(ERROR_DESCRIPTION_1);
        result[1] = new ErrorMessage();
        result[1].setErrorCode("2");
        result[1].setErrorDescription(ERROR_DESCRIPTION_2);
        result[2] = new ErrorMessage();
        result[2].setErrorCode("3");
        result[2].setErrorDescription(ERROR_DESCRIPTION_3);
    }

    /**
     * Test the getValue method.
     */
    @Test
    void testGetErrorMessage() {

        // Activate the mock generic dao
        when(mockGenericDao.query(ErrorMessage.class)).thenReturn(result);

        // Get some values
        IErrorMessage errorMessage = cache.getValue(IErrorMessage.class, "1");
        assertEquals( "1", errorMessage.getErrorCode());
        assertEquals(ERROR_DESCRIPTION_1, errorMessage.getErrorDescription());

        errorMessage = cache.getValue(IErrorMessage.class, "3");
        assertEquals("3", errorMessage.getErrorCode());
        assertEquals(ERROR_DESCRIPTION_3, errorMessage.getErrorDescription());

        errorMessage = cache.getValue(IErrorMessage.class, "2");
        assertEquals("2", errorMessage.getErrorCode());
        assertEquals(ERROR_DESCRIPTION_2, errorMessage.getErrorDescription());

        verify(mockGenericDao).query(ErrorMessage.class);
        verify(managementMBean, times(3)).getCacheResetControl();
    }

    /**
     * Test key not found.
     */
    @Test
    void testKeyNotFound() {

        // Activate the mock generic dao
        when(mockGenericDao.query(ErrorMessage.class)).thenReturn(result);

        IErrorMessage errorMessage = null;
        try {
            // Get some values
            errorMessage = cache.getValue(IErrorMessage.class, "dont_exist");
            fail("IllegalException should have been thrown");
        } catch (final IllegalStateException e) {
            assertNull(errorMessage);
        }

        verify(mockGenericDao).query(ErrorMessage.class);
        verify(managementMBean).getCacheResetControl();
    }

    /**
     * Test uncache works.
     */
    @Test
    void testUncache() {

        // Activate the mock generic dao
        when(mockGenericDao.query(ErrorMessage.class)).thenReturn(result);

        // Get some values to prove the cache is not empty
        final IErrorMessage errorMessage = cache.getValue(IErrorMessage.class, "1");
        assertEquals("1", errorMessage.getErrorCode());
        assertEquals(ERROR_DESCRIPTION_1, errorMessage.getErrorDescription());

        cache.uncache();

        assertEquals(0, cache.getErrorMessages().size());

        verify(mockGenericDao).query(ErrorMessage.class);
        verify(managementMBean).getCacheResetControl();
    }

    @Test
    void testSetGenericDao(){
        SdtManagementMBean mockSdtManagementMBean = mock(SdtManagementMBean.class);
        ErrorMessageDao mockErrorMessageDao = mock(ErrorMessageDao.class);
        ErrorMessagesCache errorMessageCache = new ErrorMessagesCache(mockSdtManagementMBean,mockErrorMessageDao);
        errorMessageCache.setGenericDao(mockGenericDao);

        Object genericDaoResult = this.getAccessibleField(ErrorMessagesCache.class, "genericDao",IErrorMessagesCache.class,errorMessageCache);

        assertNotNull(errorMessageCache,"Object should have been populated");
        assertEquals(mockGenericDao, genericDaoResult, "GenericDao should be set to an object");
    }

}
