package uk.gov.moj.sdt.utils.cmc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CCDReferenceValidatorTest {

    private CCDReferenceValidator ccdReferenceValidator;

    @BeforeEach
    void setUp() {
        ccdReferenceValidator = new CCDReferenceValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1676030589543579", "1234-5678-1234-5678"})
    void shouldReturnTrueWhenValidCCDReferenceIsPassed(String claimNumber) {
        boolean value = ccdReferenceValidator.isValidCCDReference(claimNumber);
        assertTrue(value, "Claim number should be a valid CCD reference");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"9QZ00007"})
    void shouldReturnFalseWhenInValidCCDReference(String claimNumber) {
        boolean value = ccdReferenceValidator.isValidCCDReference(claimNumber);
        assertFalse(value, "Claim number should not be a valid CCD reference");
    }
}
