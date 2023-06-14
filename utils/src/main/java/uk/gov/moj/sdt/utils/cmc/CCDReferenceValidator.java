package uk.gov.moj.sdt.utils.cmc;

import org.springframework.stereotype.Component;

@Component
public class CCDReferenceValidator {

    private static final String CASE_REFERENCE_REGEX = "(?:^\\d{16}$|^\\d{4}-\\d{4}-\\d{4}-\\d{4}$)";

    public boolean isValidCCDReference(String claimNumber) {
        return claimNumber != null && claimNumber.matches(CASE_REFERENCE_REGEX);
    }
}
