package uk.gov.moj.sdt.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.dao.PurgeNativeCallFunction;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PurgeServiceTest extends AbstractSdtUnitTestBase {

    private PurgeService purgeService;
    @Mock
    PurgeNativeCallFunction purgeNativeCallFunction;

    private static final String COMMIT_INTERVAL_NOT_EXPECTED_VALUE = "commitInterval parameter is not expected value";

    @BeforeEach
    @Override
    public void setUp() {
        purgeService = new PurgeService(purgeNativeCallFunction);
    }

    @Test
    void shouldPerformTransactionalPurge() {
        purgeService.transactPurgeProc();
        assertTrue(true, "Expected to pass");
        verify(purgeNativeCallFunction).executePurgeStoredProc(anyInt());
    }

    @Test
    void shouldRetrieveDefaultCommitInterval() {
        Integer expectedValue = 500;
        Integer commitInterval =  purgeService.getCommitIntervalInteger();
        assertEquals(expectedValue, commitInterval, COMMIT_INTERVAL_NOT_EXPECTED_VALUE);
    }

    @Test
    void shouldRetrieveDefaultCommitIntervalWhenNull() {
        Integer expectedValue = 500;
        purgeService.setCommitIntervalString(null);
        Integer commitInterval =  purgeService.getCommitIntervalInteger();
        assertEquals(expectedValue, commitInterval, COMMIT_INTERVAL_NOT_EXPECTED_VALUE);
    }

    @Test
    void shouldRetrieveDefaultCommitIntervalWhenEmpty() {
        Integer expectedValue = 500;
        purgeService.setCommitIntervalString("");
        Integer commitInterval =  purgeService.getCommitIntervalInteger();
        assertEquals(expectedValue, commitInterval, COMMIT_INTERVAL_NOT_EXPECTED_VALUE);
    }

    @Test
    void shouldRetrieveCommitIntervalWhenGoodParamValue() {
        Integer expectedValue = 2000;
        purgeService.setCommitIntervalString(expectedValue.toString());
        Integer commitInterval =  purgeService.getCommitIntervalInteger();
        assertEquals(expectedValue, commitInterval, COMMIT_INTERVAL_NOT_EXPECTED_VALUE);
    }

}
