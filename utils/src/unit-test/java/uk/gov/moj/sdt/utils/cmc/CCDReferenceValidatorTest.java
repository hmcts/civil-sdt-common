package uk.gov.moj.sdt.utils.cmc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CCDReferenceValidatorTest {

    private CCDReferenceValidator ccdReferenceValidator = new CCDReferenceValidator();

    @Test
    void shouldReturnTrueWhenValidCCDReferenceIsPassed() {
        boolean value = ccdReferenceValidator.isValidCCDReference("1676030589543579");
        assertTrue(value);
    }

    @Test
    void shouldReturnFalseWhenInValidCCDReference() {
        boolean value = ccdReferenceValidator.isValidCCDReference("9QZ00007");
        assertFalse(value);
    }
}
